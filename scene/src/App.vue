<template>
    <Scene />
</template>

<script setup>
import Scene from "./views/Scene.vue";
</script>

<style>
body {
    margin: 0;
}

canvas {
    position: fixed;
    left: 0;
    top: 0;
    width: 100vw;
    height: 100vh;
}
</style>
