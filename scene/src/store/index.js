import { createStore } from "vuex";

const store = createStore({
    state: {
        userId: null,
        userName: null,
        model: null,
        color: null,
    },
    mutations: {
        updateUserId(state, val) {
            state.userId = val;
        },
        updateUserName(state, val) {
            state.userName = val;
        },
        updateModel(state, val) {
            state.model = val;
        },
        updateColor(state, val) {
            state.color = val;
        },
    },
});

export default store;
