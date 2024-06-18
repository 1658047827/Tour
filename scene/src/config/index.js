const env = import.meta.env.MODE || "production";

const EnvConfig = {
    development: {
        baseApi: "/api",
        mockApi: "http://127.0.0.1:4523/m1/4592529-4241944-default",
    },
    test: {
        baseApi: "http://120.26.137.179/api",
        mockApi: "http://127.0.0.1:4523/m1/4592529-4241944-default",
    },
    production: {
        baseApi: "http://120.26.137.179/api",
        mockApi: "http://127.0.0.1:4523/m1/4592529-4241944-default",
    },
};

export default {
    env,
    mock: false,
    log: true,
    ...EnvConfig[env],
};
