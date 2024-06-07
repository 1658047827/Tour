<template>
    <div>
        <div id="chat">
            <div style="width: 90%;">
                <el-input id="msg" size="large" v-model="message" autocomplete="off" placeholder="Enter a message" />
            </div>
            <el-button id="sendBtn" size="large" type="primary" :icon="Message" @click="sendChatMessage" />
            <el-button id="closeBtn" size="large" type="danger" :icon="CloseBold" @click="endChat" />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import * as THREE from 'three';
import { FBXLoader } from 'three/addons/loaders/FBXLoader';
import { io } from "socket.io-client";
import { CloseBold, Message } from '@element-plus/icons-vue';

const ActionType = {
    Idle: "Idle",
    Walk: "Walk",
    Run: "Run",
    WalkBackwards: "WalkBackwards",
    LeftTurn: "LeftTurn",
    RightTurn: "RightTurn",
    Talk: "Talk",
};

class Character {
    constructor(scene, socketId, model, color) {
        this.scene = scene;

        this.socketId = socketId;
        this.model = model;
        this.color = color;

        this.mixer = null;
        this.object = null;
        this.texture = null;

        this.velocity = new THREE.Vector3();

        this.animations = {};
        this.actions = {};

        this.currActionType = ActionType.Idle;
    }

    async initialize() {
        await this.loadModel();
        if (this.socketId in initializingCharacters) {
            remoteCharacters[this.socketId] = this;
            delete initializingCharacters[this.socketId];
        }
    }

    async loadModel() {
        const loader = new FBXLoader();
        const model = await loader.loadAsync(`model/${this.model}.fbx`);
        this.object = new THREE.Object3D();
        this.object.add(model);
        this.scene.add(this.object);
        this.mixer = new THREE.AnimationMixer(model);

        // Load texture and apply.
        const textureLoader = new THREE.TextureLoader();
        this.texture = await textureLoader.loadAsync(`texture/${this.model}_${this.color}.png`);
        this.texture.colorSpace = THREE.SRGBColorSpace;
        model.traverse((child) => {
            if (child.isMesh) {
                const material = new THREE.MeshStandardMaterial({
                    map: this.texture,
                    metalness: 0.5,
                    roughness: 0.5,
                });

                this.texture.wrapS = THREE.RepeatWrapping;
                this.texture.wrapT = THREE.RepeatWrapping;
                this.texture.repeat.set(1, 1);
                this.texture.offset.set(0, 0);

                child.material = material;
                child.material.needsUpdate = true;
            }
        });

        // Clip actions and play Idle animation by default.
        for (let key of Object.keys(ActionType))
            this.actions[key] = this.mixer.clipAction(animations[key]);
        this.currActionType = ActionType.Idle;
        this.actions[ActionType.Idle].play();
    }

    setAction(type) {
        if (this.currActionType && this.currActionType !== type) {
            const prevActionType = this.currActionType;
            this.currActionType = type;

            this.actions[prevActionType].fadeOut(0.5);
            this.actions[this.currActionType].reset().fadeIn(0.5).play();
        }
    };

    update(delta) {
        if (this.mixer) this.mixer.update(delta);
        if (this.actions) this.setAction(remoteData[this.socketId].action);
        if (this.object) {
            this.object.position.x = remoteData[this.socketId].x;
            this.object.position.y = remoteData[this.socketId].y;
            this.object.position.z = remoteData[this.socketId].z;

            const euler = new THREE.Euler(
                remoteData[this.socketId].pitch,
                remoteData[this.socketId].heading,
                remoteData[this.socketId].bank
            );
            this.object.quaternion.setFromEuler(euler);
        }
    }
}

class LocalCharacter extends Character {
    constructor(scene, model, color) {
        super(scene, null, model, color);

        this.socket = io('http://localhost:3000');

        this.socket.on("setSocketId", (data) => this.socketId = data.socketId);

        this.socket.on("remoteData", (data) => remoteData = data);

        this.socket.on("deletePlayer", (data) => {
            delete remoteData[data.socketId];
            if (data.socketId in initializingCharacters) {
                delete initializingCharacters[data.socketId];
            } else if (data.socketId in remoteCharacters) {
                const character = remoteCharacters[data.socketId];
                delete remoteCharacters[data.socketId];
                this.scene.remove(character.object);
            }
        });

        this.socket.on("startChat", (data) => {
            console.log(`Start chat with player ${data.socketId}`);
            document.getElementById('chat').style.bottom = '0px';
            speechBubble.character = remoteCharacters[data.socketId];
            speechBubble.update("");
            chatSocketId = data.socketId;
            this.scene.add(speechBubble.mesh);
            removeKeyboardListeners();
            localCharacter.setAction(ActionType.Talk);
        });

        this.socket.on("chatMessage", (data) => {
            console.log(`Received message '${data.message}' from player ${data.socketId}`);
            speechBubble.update(data.message);
        });

        this.socket.on("endChat", (data) => {
            console.log(`End chat with player ${data.socketId}`);
            document.getElementById('chat').style.bottom = '-55px';
            speechBubble.character = null;
            chatSocketId = null;
            this.scene.remove(speechBubble.mesh);
            addKeyboardListeners();
        });

        setInterval(() => this.socket.emit("getRemoteData"), 16);

        this.move = { forward: 0, turn: 0 };

        this.cameraGroup = null;
        this.cameraObject = null;
    }

    async initialize() {
        await super.initialize();
        this.socketInit();
        this.setupCamera();
    }

    socketInit() {
        this.socket.emit("init", {
            model: this.model,
            color: this.color,
            x: this.object.position.x,
            y: this.object.position.y,
            z: this.object.position.z,
            heading: this.object.rotation.y,
            pitch: this.object.rotation.x,
            bank: this.object.rotation.z,
            action: this.currActionType
        });
    }

    socketUpdate() {
        if (this.socket) {
            this.socket.emit("update", {
                x: this.object.position.x,
                y: this.object.position.y,
                z: this.object.position.z,
                heading: this.object.rotation.y,
                pitch: this.object.rotation.x,
                bank: this.object.rotation.z,
                action: this.currActionType
            });
        }
    }

    setupCamera() {
        this.cameraGroup = new THREE.Group();
        this.cameraObject = new THREE.Object3D();
        this.cameraObject.position.set(0, 400, -900);
        this.cameraGroup.add(this.cameraObject);
        this.object.add(this.cameraGroup);
    }

    control() {
        if (keys.w > 0) {
            if (this.currActionType !== ActionType.Walk && this.currActionType !== ActionType.Run)
                this.setAction(ActionType.Walk);
            else if (this.currActionType === ActionType.Walk && (Date.now() - keys.w > 1000))
                this.setAction(ActionType.Run);
        } else if (keys.s > 0) {
            if (this.currActionType !== ActionType.WalkBackwards)
                this.setAction(ActionType.WalkBackwards);
        } else if (keys.a > 0) {
            if (this.currActionType !== ActionType.LeftTurn)
                this.setAction(ActionType.LeftTurn);
        } else if (keys.d > 0) {
            if (this.currActionType !== ActionType.RightTurn)
                this.setAction(ActionType.RightTurn);
        } else if (keys.w === 0 && keys.a === 0 && keys.s === 0 && keys.d === 0) {
            this.setAction(ActionType.Idle);
        }

        if (keys.w > 0)
            this.move.forward = 1;
        else if (keys.s > 0)
            this.move.forward = -1;
        else
            this.move.forward = 0;

        if (keys.a > 0)
            this.move.turn = 1.5;
        else if (keys.d > 0)
            this.move.turn = -1.5;
        else
            this.move.turn = 0;
    }

    makeMovement(delta) {
        const pos = this.object.position.clone();
        pos.y += 60;
        let dir = new THREE.Vector3();
        this.object.getWorldDirection(dir);
        if (this.move.forward < 0) dir.negate();
        let raycaster = new THREE.Raycaster(pos, dir);
        let blocked = false;

        if (colliders) {
            const intersect = raycaster.intersectObjects(colliders);
            if (intersect.length > 0) {
                if (intersect[0].distance < 50) blocked = true;
            }
        }

        if (!blocked) {
            if (this.move.forward > 0) {
                const speed = (this.currActionType === ActionType.Run) ? 400 : 150;
                this.object.translateZ(delta * speed);
            } else if (this.move.forward < 0) {
                this.object.translateZ(-delta * 100);
            }
        }

        if (colliders !== undefined) {
            //cast left
            dir.set(-1, 0, 0);
            dir.applyMatrix4(this.object.matrix);
            dir.normalize();
            raycaster = new THREE.Raycaster(pos, dir);

            let intersect = raycaster.intersectObjects(colliders);
            if (intersect.length > 0)
                if (intersect[0].distance < 50)
                    this.object.translateX(100 - intersect[0].distance);

            //cast right
            dir.set(1, 0, 0);
            dir.applyMatrix4(this.object.matrix);
            dir.normalize();
            raycaster = new THREE.Raycaster(pos, dir);

            intersect = raycaster.intersectObjects(colliders);
            if (intersect.length > 0)
                if (intersect[0].distance < 50)
                    this.object.translateX(intersect[0].distance - 100);

            //cast down
            dir.set(0, -1, 0);
            pos.y += 200;
            raycaster = new THREE.Raycaster(pos, dir);
            const gravity = 30;

            intersect = raycaster.intersectObjects(colliders);
            if (intersect.length > 0) {
                const targetY = pos.y - intersect[0].distance;
                if (targetY > this.object.position.y) {
                    //Going up
                    this.object.position.y = 0.8 * this.object.position.y + 0.2 * targetY;
                    this.velocityY = 0;
                } else if (targetY < this.object.position.y) {
                    //Falling
                    if (!this.velocityY) this.velocityY = 0;
                    this.velocityY += delta * gravity;
                    this.object.position.y -= this.velocityY;
                    if (this.object.position.y < targetY) {
                        this.velocityY = 0;
                        this.object.position.y = targetY;
                    }
                }
            } else if (this.object.position.y > 0) {
                if (!this.velocityY) this.velocityY = 0;
                this.velocityY += delta * gravity;
                this.object.position.y -= this.velocityY;
                if (this.object.position.y < 0) {
                    this.velocityY = 0;
                    this.object.position.y = 0;
                }
            }
        }

        this.object.rotateY(this.move.turn * delta);
    }

    update(delta) {
        if (this.mixer) this.mixer.update(delta);
        if (this.actions && !chatSocketId) this.control();
        if (this.object) this.makeMovement(delta);
        if (this.object && this.socket) this.socketUpdate();
    }
}

class SpeechBubble {
    constructor(scene, msg, size = 1) {
        this.character = null;
        this.msg = msg;
        this.config = { font: 'Calibri', size: 20, padding: 10, color: '#222', width: 256, height: 256 };

        const planeGeometry = new THREE.PlaneGeometry(size, size);
        const planeMaterial = new THREE.MeshBasicMaterial()
        this.mesh = new THREE.Mesh(planeGeometry, planeMaterial);
        // scene.add(this.mesh);
    }

    async initialize() {
        await this.loadTexture();
    }

    async loadTexture() {
        const loader = new THREE.TextureLoader();
        this.texture = await loader.loadAsync("image/speech.png");
        this.img = this.texture.image;
        this.mesh.material.map = this.texture;
        this.mesh.material.transparent = true;
        this.mesh.material.needsUpdate = true;
        if (this.msg) this.update(this.msg);
    }

    update(msg) {
        if (!this.mesh || !this.img) return;

        if (!this.context) {
            const canvas = this.createOffscreenCanvas(this.config.width, this.config.height);
            this.context = canvas.getContext("2d");
            this.context.font = `${this.config.size}pt ${this.config.font}`;
            this.context.fillStyle = this.config.color;
            this.context.textAlign = 'center';
            this.mesh.material.map = new THREE.CanvasTexture(canvas);
        }

        const bg = this.img;
        this.context.clearRect(0, 0, this.config.width, this.config.height);
        this.context.drawImage(bg, 0, 0, bg.width, bg.height, 0, 0, this.config.width, this.config.height);
        this.wrapText(msg, this.context);

        this.mesh.material.map.needsUpdate = true;
    }

    createOffscreenCanvas(w, h) {
        const canvas = document.createElement('canvas');
        canvas.width = w;
        canvas.height = h;
        return canvas;
    }

    wrapText(text, context) {
        const words = text.split(' ');
        let line = '';
        const lines = [];
        const maxWidth = this.config.width - 2 * this.config.padding;
        const lineHeight = this.config.size + 8;

        words.forEach((word) => {
            const testLine = `${line}${word} `;
            const metrics = context.measureText(testLine);
            const testWidth = metrics.width;
            if (testWidth > maxWidth) {
                lines.push(line);
                line = `${word} `;
            } else {
                line = testLine;
            }
        });
        if (line) lines.push(line);

        let y = (this.config.height - lines.length * lineHeight) / 2;
        lines.forEach((line) => {
            context.fillText(line, this.config.width / 2, y);
            y += lineHeight;
        });
    }

    show(pos) {
        if (this.mesh && this.character) {
            this.mesh.position.set(
                this.character.object.position.x,
                this.character.object.position.y + 380,
                this.character.object.position.z,
            );
            this.mesh.lookAt(pos);
        }
    }
}

const clock = new THREE.Clock();

const scene = new THREE.Scene();
scene.background = new THREE.Color(0xa0a0a0);

const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 5000);
camera.position.set(0, 400, -900);

const hemisphereLight = new THREE.HemisphereLight(0xffffff, 0x444444);
hemisphereLight.position.set(0, 200, 0);
scene.add(hemisphereLight);

const groundGeometry = new THREE.PlaneGeometry(5000, 5000);
const groundMaterial = new THREE.MeshStandardMaterial({ color: 0x999999, depthWrite: false })
const ground = new THREE.Mesh(groundGeometry, groundMaterial);
ground.rotation.x = - Math.PI / 2;
ground.receiveShadow = true;
scene.add(ground);

const grid = new THREE.GridHelper(5000, 40, 0x000000, 0x000000);
grid.material.opacity = 0.2;
grid.material.transparent = true;
scene.add(grid);

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.shadowMap.enabled = true;
document.body.appendChild(renderer.domElement);

const people = ['BeachBabe', 'BusinessMan', 'Doctor', 'FireFighter', 'HouseWife', 'Policeman', 'Prostitute', 'Punk', 'RiotCop', 'RoadWorker', 'Robber', 'Sheriff', 'StreetMan', 'Waitress'];
const color = ["Black", "Brown", "White"];

const keys = { w: 0, a: 0, s: 0, d: 0 };
const onKeydown = (event) => {
    if (event.key in keys && keys[event.key] === 0) {
        keys[event.key] = Date.now();
    } else if (event.key === 'c') {
        // Find the nearest remote character within 500 units.
        let minDistance = Infinity;
        let selectedCharacter = null;
        Object.values(remoteCharacters).forEach(remoteCharacter => {
            const distance = localCharacter.object.position.distanceTo(remoteCharacter.object.position);
            if (distance < 500 && distance < minDistance) {
                minDistance = distance;
                selectedCharacter = remoteCharacter;
            }
        });

        if (selectedCharacter) {
            localCharacter.socket.emit("startChat", { socketId: selectedCharacter.socketId });
            console.log(`Start chat with player ${selectedCharacter.socketId}`);
            speechBubble.character = selectedCharacter;
            speechBubble.update("");
            scene.add(speechBubble.mesh);
            chatSocketId = selectedCharacter.socketId;
            document.getElementById('chat').style.bottom = "0px";
            removeKeyboardListeners();
            localCharacter.setAction(ActionType.Talk);
        }
    }
}
const onKeyup = (event) => {
    if (event.key in keys) {
        keys[event.key] = 0;
    }
}
const addKeyboardListeners = () => {
    document.addEventListener('keydown', onKeydown);
    document.addEventListener('keyup', onKeyup);
}
const removeKeyboardListeners = () => {
    document.removeEventListener("keydown", onKeydown);
    document.removeEventListener("keyup", onKeyup);
}

const localCharacter = new LocalCharacter(
    scene,
    people[Math.floor(Math.random() * people.length)],
    color[Math.floor(Math.random() * color.length)]
);
let remoteData;
const initializingCharacters = {};
const remoteCharacters = {};

const speechBubble = new SpeechBubble(scene, "", 150);

const updateRemoteCharacters = (delta) => {
    if (remoteData && localCharacter && localCharacter.socketId) {
        for (let [socketId, data] of Object.entries(remoteData)) {
            if (socketId === localCharacter.socketId) continue;
            if (socketId in initializingCharacters) continue;
            if (!(socketId in remoteCharacters)) {
                initializingCharacters[socketId] = new Character(scene, socketId, data.model, data.color);
                initializingCharacters[socketId].initialize();
            }
        }

        Object.values(remoteCharacters).forEach((character) => character.update(delta));
    }
}

const animate = () => {
    requestAnimationFrame(animate);

    const delta = clock.getDelta();
    localCharacter.update(delta);
    updateRemoteCharacters(delta);

    // Update the position of camera.
    if (localCharacter.object && localCharacter.cameraObject) {
        const characterPos = localCharacter.object.position.clone();
        camera.position.lerp(localCharacter.cameraObject.getWorldPosition(new THREE.Vector3()), 0.1);
        characterPos.y += 200;
        camera.lookAt(characterPos);
    }

    if (speechBubble) speechBubble.show(camera.position);

    renderer.render(scene, camera);
}

const animations = {};
const loadAnimations = async () => {
    const loader = new FBXLoader();
    for (let key of Object.keys(ActionType)) {
        let anim = await loader.loadAsync(`animation/${key}.fbx`);
        animations[key] = anim.animations[0];
    }
}

const colliders = [];
const createColliders = () => {
    const geometry = new THREE.BoxGeometry(500, 400, 500);
    const material = new THREE.MeshBasicMaterial({ color: 0x222222, wireframe: true });

    for (let x = -5000; x < 5000; x += 1000) {
        for (let z = -5000; z < 5000; z += 1000) {
            if (x == 0 && z == 0) continue;
            const box = new THREE.Mesh(geometry, material);
            box.position.set(x, 250, z);
            scene.add(box);
            colliders.push(box);
        }
    }

    const geometry2 = new THREE.BoxGeometry(1000, 40, 1000);
    const stage = new THREE.Mesh(geometry2, material);
    stage.position.set(0, 20, 0);
    colliders.push(stage);
    scene.add(stage);
}

const initializeGame = async () => {
    await loadAnimations();
    await localCharacter.initialize();
    await speechBubble.initialize();
    addKeyboardListeners();
    createColliders();
    animate();
}

window.addEventListener("resize", () => {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(window.innerWidth, window.innerHeight);
});

document.body.addEventListener('click', () => {
    document.body.requestPointerLock();
});

document.addEventListener('pointerlockchange', () => {
    if (document.pointerLockElement === document.body) {
        document.addEventListener('mousemove', onMouseMove, false);
    } else {
        document.removeEventListener('mousemove', onMouseMove, false);
    }
});

const onMouseMove = (event) => {
    const movementX = event.movementX || 0;
    localCharacter.cameraGroup.rotateY(- movementX / 900);
}

let chatSocketId = null;
const message = ref("");
const sendChatMessage = () => {
    if (message.value !== "") {
        localCharacter.socket.emit("chatMessage", { socketId: chatSocketId, message: message.value });
        message.value = "";
    }
}
const endChat = () => {
    localCharacter.socket.emit("endChat", { socketId: chatSocketId });
    console.log(`End chat with player ${chatSocketId}`);
    document.getElementById('chat').style.bottom = '-55px';
    speechBubble.character = null;
    chatSocketId = null;
    scene.remove(speechBubble.mesh);
    addKeyboardListeners();
}

initializeGame();
</script>

<style scoped>
#chat {
    display: flex;
    justify-content: center;
    position: fixed;
    width: 100vw;
    z-index: 5;
    height: 40px;
    bottom: -55px;
    /* bottom: 0px; */
    transition: all 0.5s;
    padding-bottom: 15px;
}

#chat #sendBtn {
    margin-left: 12px;
}
</style>
