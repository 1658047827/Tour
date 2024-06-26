import axios from "axios";
import config from "../config";

const instance = axios.create({
    // baseURL: config.mockApi,
    baseURL: config.baseApi,
});

const api = {
    loginApi(params) {
        return new Promise((resolve, reject) => {
            instance.get("/login", { params: params }).then(
                (response) => resolve(response.data),
                (err) => reject(err)
            );
        });
    },
    registerApi(params) {
        return new Promise((resolve, reject) => {
            instance.post("/register", params).then(
                (response) => resolve(response.data),
                (err) => reject(err)
            );
        });
    },
    getUserInfoApi(userId) {
        return new Promise((resolve, reject) => {
            instance.get(`/user/${userId}`).then(
                (response) => resolve(response.data),
                (err) => reject(err)
            );
        });
    },
};

export default api;
