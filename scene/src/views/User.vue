<template>
    <el-button style="margin-left: 10px; margin-top: 10px; margin-bottom: 60px;" @click="toScene">返回</el-button>
    <el-row :gutter="20">
        <el-col :span="16" :offset="4">
            <el-card>
                <el-row :gutter="20">
                    <el-col :span="3">
                        <el-image :src="url" @dragstart.prevent fit="cover"
                            style="width: 163px; height: 163px; border-radius: 80px; margin-left: 10px;" />
                    </el-col>
                    <el-col :span="4">
                        <div style="font-size: x-large; margin-top: 50px;">{{ username }}</div>
                        <div style="font-size: large; margin-top: 10px;">{{ email }}</div>
                    </el-col>
                </el-row>
            </el-card>
        </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="4" :offset="4">
            <el-card>
                <div style="font-size: x-large;">个人简介</div>
                <el-input v-model="textarea" :autosize="{ minRows: 28, maxRows: 28 }" type="textarea"
                    placeholder="Please input" style="margin-top: 20px;" />
            </el-card>
        </el-col>
        <el-col :span="12">
            <el-card>
                <el-row :gutter="20">
                    <el-col :span="7">
                        <div style="font-size: x-large;">
                            人物形象
                        </div>
                        <div style="font-size: x-large; margin-top: 30px;">{{ color }} {{ model }} </div>
                    </el-col>
                    <el-col :span="17">
                        <div ref="container"></div>
                    </el-col>
                </el-row>
            </el-card>
        </el-col>
    </el-row>

</template>

<script setup>
import { getCurrentInstance, onMounted, ref, render } from 'vue';
import * as THREE from 'three';
import { FBXLoader } from 'three/addons/loaders/FBXLoader';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const url = new URL("../assets/images/user_default.png", import.meta.url).href;
const username = ref("");
const email = ref("");
const textarea = ref("这个人很神秘，什么也没有说");
const model = ref("FireFighter");
const color = ref("Brown");

const container = ref();

const { proxy } = getCurrentInstance();
const router = useRouter();
const store = useStore();

const clock = new THREE.Clock();

const scene = new THREE.Scene();
scene.background = new THREE.Color(0xa0a0a0);

const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight / 1.5, 1, 10000);
camera.position.set(112, 100, 400);

const hemisphereLight = new THREE.HemisphereLight(0xffffff, 0x444444);
hemisphereLight.position.set(0, 200, 0);
scene.add(hemisphereLight);

const directionalLight = new THREE.DirectionalLight(0xffffff);
directionalLight.position.set(0, 200, 100);
directionalLight.castShadow = true;
directionalLight.shadow.camera.top = 180;
directionalLight.shadow.camera.bottom = -100;
directionalLight.shadow.camera.left = -120;
directionalLight.shadow.camera.right = 120;
scene.add(directionalLight);

const groundGeometry = new THREE.PlaneGeometry(10000, 10000);
const groundMaterial = new THREE.MeshStandardMaterial({ color: 0x999999, depthWrite: false })
const ground = new THREE.Mesh(groundGeometry, groundMaterial);
ground.rotation.x = - Math.PI / 2;
ground.receiveShadow = true;
scene.add(ground);

const grid = new THREE.GridHelper(10000, 40, 0x000000, 0x000000);
grid.material.opacity = 0.2;
grid.material.transparent = true;
scene.add(grid);

const loader = new FBXLoader();
let mixer = null;

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth / 3, window.innerHeight / 2 - 15);
renderer.shadowMap.enabled = true;

const controls = new OrbitControls(camera, renderer.domElement);
controls.target.set(0, 150, 0);
controls.update();

const toScene = () => {
    router.push("/scene");
};

const animate = () => {
    requestAnimationFrame(animate);
    const delta = clock.getDelta();
    if (mixer) mixer.update(delta);
    renderer.render(scene, camera);
}

const getUserInfo = async (userId) => {
    let res = await proxy.$api.getUserInfoApi(userId);
    username.value = res.data.userName;
    email.value = res.data.email;
    model.value = res.data.model;
    color.value = res.data.color;
};

onMounted(async () => {
    container.value.appendChild(renderer.domElement);
    renderer.domElement.style.borderRadius = "5px";
    await getUserInfo(store.userId);
    loader.load(`model/${model.value}.fbx`, (object) => {
        mixer = new THREE.AnimationMixer(object);
        scene.add(object);

        // Load texture and apply.
        const textureLoader = new THREE.TextureLoader();
        textureLoader.load(`texture/${model.value}_${color.value}.png`, (texture) => {
            texture.colorSpace = THREE.SRGBColorSpace;
            object.traverse((child) => {
                if (child.isMesh) {
                    const material = new THREE.MeshStandardMaterial({
                        map: texture,
                        metalness: 0.5,
                        roughness: 0.5,
                    });

                    texture.wrapS = THREE.RepeatWrapping;
                    texture.wrapT = THREE.RepeatWrapping;
                    texture.repeat.set(1, 1);
                    texture.offset.set(0, 0);

                    child.material = material;
                    child.material.needsUpdate = true;
                }
            });
        });

        mixer.clipAction(object.animations[0]).play();
        animate();
    });
});
</script>

<style scoped>
canvas {
    border-radius: 10px;
}
</style>