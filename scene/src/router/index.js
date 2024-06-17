import { createRouter, createWebHashHistory } from "vue-router";

const routes = [
    {
        path: "/",
        redirect: "/login",
    },
    {
        path: "/login",
        component: () => import("../views/Login.vue"),
    },
    {
        path: "/scene",
        component: () => import("../views/Scene.vue"),
    },
    {
        path: "/user",
        component: () => import("../views/User.vue"),
    },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

export default router;
