import { createApp } from "vue";
import App from "./App.vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import api from "./api";
import router from "./router";
import store from "./store";

const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}
app.config.globalProperties.$api = api;
app.use(router).use(store);
app.mount("#app");
