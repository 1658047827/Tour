<template>
    <!-- 最外层的大盒子 -->
    <div class="bigBox">

        <div class="box" ref="box">

            <!-- 滑动盒子 -->
            <div class="pre-box">
                <h1>WELCOME</h1>
                <p>JOIN US!</p>
                <div class="img-box">
                    <img src="../assets/images/login-pic.jpg" alt="" id="avatar" @dragstart.prevent />
                </div>
            </div>

            <!-- 注册盒子 -->
            <div class="register-form">
                <!-- 标题 -->
                <div class="title-box">
                    <h1>用户注册</h1>
                </div>
                <!-- 输入框盒子 -->
                <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-with="5px">
                    <el-form-item prop="username" label=" ">
                        <el-input type="text" placeholder="用户名" suffix-icon="User" v-model="registerForm.username" />
                    </el-form-item>
                    <el-form-item prop="email" label=" ">
                        <el-input type="email" placeholder="邮箱" suffix-icon="Message" v-model="registerForm.email" />
                    </el-form-item>
                    <el-form-item prop="password" label=" ">
                        <el-input type="password" placeholder="密码" suffix-icon="Lock" v-model="registerForm.password" />
                    </el-form-item>
                    <el-form-item prop="confirmPassword" label=" ">
                        <el-input type="password" placeholder="确认密码" suffix-icon="Lock"
                            v-model="registerForm.confirmPassword" />
                    </el-form-item>
                    <el-form-item prop="model" label=" ">
                        <el-select v-model="registerForm.model" placeholder="选择形象">
                            <el-option v-for="item in models" :key="item" :label="item" :value="item" />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="color" label=" ">
                        <el-select v-model="registerForm.color" placeholder="选择肤色">
                            <el-option v-for="item in colors" :key="item" :label="item" :value="item" />
                        </el-select>
                    </el-form-item>
                </el-form>
                <!-- 按钮盒子 -->
                <div class="btn-box">
                    <el-button type="primary" @click="register" :loading="register_loading" loadIcon="Loading">
                        注册
                    </el-button>
                    <!-- 绑定点击事件 -->
                    <p @click="slide">已有账号？去登录</p>
                </div>
            </div>

            <!-- 登录盒子 -->
            <div class="login-form">
                <!-- 标题 -->
                <div class="title-box">
                    <h1>用户登录</h1>
                </div>
                <!-- 输入框盒子 -->
                <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-with="5px">
                    <el-form-item prop="username" label=" ">
                        <el-input type="text" placeholder="用户名" suffix-icon="User" v-model="loginForm.username" />
                    </el-form-item>
                    <el-form-item prop="password" label=" " @keyup.enter.native="login">
                        <el-input type="password" placeholder="密码" suffix-icon="Lock" v-model="loginForm.password"
                            show-password />
                    </el-form-item>
                </el-form>
                <!-- 按钮盒子 -->
                <div class="btn-box">
                    <el-button type="primary" @click="login" :loading="login_loading" loadIcon="Loading">
                        登录
                    </el-button>
                    <!-- 绑定点击事件 -->
                    <p @click="slide">没有账号？去注册</p>
                </div>
            </div>

        </div>
    </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import md5 from "js-md5";

const models = ["BeachBabe", "BusinessMan", "Doctor", "FireFighter", "HouseWife", "Policeman", "Prostitute", "Punk", "RiotCop", "RoadWorker", "Robber", "Sheriff", "StreetMan", "Waitress"];
const colors = ["Black", "Brown", "White"];

const username = (rule, value, callback) => {
    // \w 等价于 [A-Za-z0-9_]
    if (/^\w*$/.test(value) === false) {
        callback(new Error("仅能出现英文字符、数字与下划线"));
    } else {
        callback();
    }
}

const password1 = (rule, value, callback) => {
    if (/^[\w\-]*$/.test(value) === false) {
        callback(new Error("不能使用字母、数字、特殊字符（-_）以外的字符"));
    } else {
        callback();
    }
}

const password2 = (rule, value, callback) => {
    if (/^(?!\d+$)(?!^[a-zA-Z]+$)(?!^[_\-]+$).{6,32}$/.test(value) === false) {
        callback(new Error("字母、数字、特殊字符（-_）应至少包含两种"));
    } else {
        callback();
    }
}

let flag = ref(true);
const slide = () => {
    const pre_box = document.querySelector(".pre-box");
    const img = document.querySelector("#avatar");
    if (flag.value) {
        pre_box.style.transform = "translateX(100%)";
        pre_box.style.backgroundColor = "#65686b";
        img.src = new URL("../assets/images/login-pic.jpg", import.meta.url).href;
    }
    else {
        pre_box.style.transform = "translateX(0%)";
        pre_box.style.backgroundColor = "#817a6e";
        img.src = new URL("../assets/images/login-pic.jpg", import.meta.url).href;
    }
    flag.value = !flag.value;
}

let register_loading = ref(false);
let login_loading = ref(false);

const { proxy } = getCurrentInstance();

const router = useRouter();
const store = useStore();

const loginForm = reactive({
    username: "",
    password: ""
});

const registerForm = reactive({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    model: "",
    color: "",
});

const loginFormRef = ref();

const registerFormRef = ref();

// 表单的校验规则
const rules = reactive({
    username: [
        { required: true, message: "请输入用户名", trigger: "blur" },
        { min: 3, max: 10, message: "长度应该在3~10个字符之间", trigger: "blur" },
        {
            validator: username, trigger: "blur"
        }
    ],
    email: [
        { required: true, message: "请输入电子邮箱", trigger: "blur" },
        { type: "email", message: "请输入正确的邮箱", trigger: "blur" },
    ],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 6, max: 32, message: "长度应该在6~32个字符之间", trigger: "blur" },
        { validator: password1, trigger: "blur" },
        { validator: password2, trigger: "blur" },
    ],
    confirmPassword: [
        { required: true, message: "请确认密码", trigger: "blur" },
        {
            validator: (rule, value, callback) => {
                if (registerForm.password !== value) {
                    callback(new Error("确认密码应和密码一致"));
                } else {
                    callback();
                }
            },
            trigger: "blur"
        },
    ],
    model: [
        { required: true, message: "请选择形象", trigger: "blur" },
    ],
    color: [
        { required: true, message: "请选择肤色", trigger: "blur" },
    ],
})

const login = async () => {
    let valid = await loginFormRef.value.validate();
    if (valid) {
        // 开启登录按钮loading
        login_loading.value = true;
        // 使用深拷贝，避免表单中的密码转成md5
        let res = await proxy.$api.loginApi({
            username: loginForm.username,
            password: md5(loginForm.password)
        }).catch(error => {
            // 关闭登录按钮loading
            login_loading.value = false;
            console.log("error: ", error);
        })
        // 关闭登录按钮loading
        login_loading.value = false;
        console.log(res);
        if (res.status === 200) {
            ElMessage.success({ showClose: true, message: "登陆成功，欢迎你" });
            // 存储用户信息
            store.commit("updateUserId", res.data.userId);
            store.commit("updateModel", res.data.model);
            store.commit("updateColor", res.data.color);
            router.push("/scene");
        }
    } else {
        console.log("表单信息不合法");
    }
}

const register = async () => {
    let valid = await registerFormRef.value.validate();
    if (valid) {
        // 开启注册按钮loading
        register_loading.value = true;
        // 使用深拷贝，避免表单中的密码转成md5
        let res = await proxy.$api.registerApi({
            username: registerForm.username,
            email: registerForm.email,
            password: md5(registerForm.password),
            confirmPassword: registerForm.confirmPassword,
        }).catch(error => {
            // 关闭注册按钮loading
            register_loading.value = false;
            console.log("error: ", error);
        })
        // 关闭注册按钮loading
        register_loading.value = false;
        if (res.status === 200) {
            ElMessage.success({
                showClose: true,
                message: "注册成功"
            })
            slide();
        }
    } else {
        console.log("表单信息不合法")
    }
}
</script>

<style scoped>
/* 去除input的轮廓 */
input {
    outline: none;
}

.bigBox {
    /* 溢出隐藏 */
    height: 100vh;
    overflow-x: hidden;
    display: flex;
    /* 渐变方向从左到右 */
    background-image: url("../assets/images/login-bg.png");
    background-size: cover;

    .return-index {
        position: absolute;
        left: -95px;
        font-size: 18px;
        margin-top: 20px;
        padding-top: 11px;
        color: #333333;
        display: block;
        height: 45px;
        width: 150px;
        background: #BBCCE1AD;
        backdrop-filter: saturate(180%) blur(10px);
        border: 3px solid rgba(150, 158, 175, 0.68);
        border-radius: 0 7px 7px 0;
        transition: 0.5s ease-in-out;
    }

    .return-index:hover {
        left: -3px;
        transition: 0.5s ease-in-out;
    }
}

/* 最外层的大盒子 */
.box {
    width: 1050px;
    height: 600px;
    display: flex;
    /* 相对定位 */
    position: relative;
    z-index: 2;
    margin: auto;
    /* 设置圆角 */
    border-radius: 8px;
    /* 设置边框 */
    border: 1px solid rgba(170, 165, 165, 0.244);
    /* 设置盒子阴影 */
    box-shadow: 2px 1px 19px rgba(0, 0, 0, 0.1);
}

/* 滑动的盒子 */
.pre-box {
    /* 宽度为大盒子的一半 */
    width: 50%;
    height: 100%;
    /* 绝对定位 */
    position: absolute;
    /* 距离大盒子左侧为0 */
    left: 0;
    /* 距离大盒子顶部为0 */
    top: 0;
    z-index: 99;
    border-radius: 4px;
    background-color: #817a6e;
    box-shadow: 2px 1px 19px rgba(0, 0, 0, 0.1);
    /* 动画过渡，先加速再减速 */
    transition: 0.5s ease-in-out;

    /* 滑动盒子的标题 */
    h1 {
        margin-top: 150px;
        text-align: center;
        /* 文字间距 */
        letter-spacing: 5px;
        font-size: 30px;
        color: white;
        /* 禁止选中 */
        user-select: none;
        /* 文字阴影 */
        text-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
    }

    /* 滑动盒子的文字 */
    p {
        height: 30px;
        line-height: 30px;
        text-align: center;
        font-size: 20px;
        margin: 20px 0;
        /* 禁止选中 */
        user-select: none;
        font-weight: bold;
        color: white;
        text-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
    }

    /* 图片盒子 */
    .img-box {
        width: 200px;
        height: 200px;
        margin: 20px auto;
        /* 设置为圆形 */
        border-radius: 50%;
        /* 设置用户禁止选中 */
        user-select: none;
        overflow: hidden;
        box-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);

        /* 图片 */
        img {
            width: 100%;
            transition: 0.5s;
        }
    }
}

/* 登录盒子 */
.login-form {
    flex: 1;
    height: 100%;
    background: rgba(61, 59, 59, 0.632);
    border-radius: 0 7px 7px 0;
    backdrop-filter: blur(10px);

    .title-box {
        margin: 160px 0 40px 0;
        text-align: center;

        h1 {
            color: white;
            font-size: 40px;
            text-shadow: 4px 4px 3px rgba(255, 255, 255, 0.1);
        }
    }
}

/* 注册盒子 */
.register-form {
    flex: 1;
    height: 100%;
    background: rgba(61, 59, 59, 0.632);
    border-radius: 7px 0 0 7px;
    backdrop-filter: blur(10px);

    .title-box {
        margin: 73px 0 40px 0;
        text-align: center;

        h1 {
            color: white;
            font-size: 40px;
            text-shadow: 4px 4px 3px rgba(255, 255, 255, 0.1);
        }
    }
}

/* 输入框盒子 */
.el-form {
    display: flex;
    /* 纵向布局 */
    flex-direction: column;
    /* 水平居中 */
    align-items: center;
}

.el-form-item {
    width: 65%;

    /* 选择用户类型span */
    span {
        color: white;
        margin-left: 20px;
        margin-right: 20px;
    }
}

/* 输入框 */
input {
    /* width: 60%; */
    height: 40px;
    margin-bottom: 20px;
    text-indent: 10px;
    border: 1px solid #fff;
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 120px;
    /* 增加磨砂质感 */
    backdrop-filter: blur(10px);
}

input:focus {
    /* 光标颜色 */
    color: #b0cfe9;
}

/* 聚焦时隐藏文字 */
input:focus::placeholder {
    opacity: 0;
}

/* 按钮盒子 */
.btn-box {
    display: flex;
    justify-content: center;
    /* align-items: center; */

    .el-button {
        margin-top: 14px;
        margin-right: 15px;
    }

    /* 链接文字 */
    p {
        height: 30px;
        line-height: 30px;
        /* 禁止选中 */
        user-select: none;
        font-size: 14px;
        color: white;
    }

    p:hover {
        cursor: pointer;
        border-bottom: 1px solid white;
    }
}
</style>