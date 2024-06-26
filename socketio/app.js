import express from "express";
import { createServer } from "node:http";
import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";
import { Server } from "socket.io";

const app = express();
const server = createServer(app);
const io = new Server(server, {
    cors: {
        // origin: "http://localhost:5173", // address of Vue app
        origin: "http://120.26.137.179", // address of Vue app
        methods: ["GET", "POST"],
    },
});

const __dirname__ = dirname(fileURLToPath(import.meta.url));

app.get("/", (req, res) => {
    res.sendFile(join(__dirname__, "index.html"));
});

io.on("connection", (socket) => {
    console.log(`Player ${socket.id} connected`);
    socket.playerData = { x: 0, y: 0, z: 0, heading: 0 };
    socket.emit("setSocketId", { socketId: socket.id });

    socket.on("disconnect", () => {
        console.log(`Player ${socket.id} disconnected`);
        socket.broadcast.emit("deletePlayer", { socketId: socket.id });
    });

    socket.on("init", (data) => {
        console.log(`Initialize player ${socket.id}`);
        socket.playerData.socketId = socket.id;
        socket.playerData.model = data.model;
        socket.playerData.color = data.color;
        socket.playerData.x = data.x;
        socket.playerData.y = data.y;
        socket.playerData.z = data.z;
        socket.playerData.heading = data.heading;
        socket.playerData.pitch = data.pitch;
        socket.playerData.bank = data.bank;
        socket.playerData.action = data.action;

        console.log(
            `${socket.id} PlayerData: ${socket.playerData.model} ${socket.playerData.color}`
        );
    });

    socket.on("update", (data) => {
        socket.playerData.x = data.x;
        socket.playerData.y = data.y;
        socket.playerData.z = data.z;
        socket.playerData.heading = data.heading;
        socket.playerData.pitch = data.pitch;
        socket.playerData.bank = data.bank;
        socket.playerData.action = data.action;
    });

    socket.on("startChat", (data) => {
        io.to(data.socketId).emit("startChat", { socketId: socket.id });
    });

    socket.on("chatMessage", (data) => {
        io.to(data.socketId).emit("chatMessage", {
            socketId: socket.id,
            message: data.message,
        });
    });

    socket.on("endChat", (data) => {
        io.to(data.socketId).emit("endChat", { socketId: socket.id });
    });

    socket.on("getRemoteData", () => {
        let remoteData = {};
        for (let [socketId, socket] of io.sockets.sockets) {
            if (
                socket.playerData &&
                socket.playerData.model &&
                socket.playerData.color
            )
                remoteData[socketId] = socket.playerData;
        }
        if (Object.keys(remoteData).length > 0)
            socket.emit("remoteData", remoteData);
    });
});

server.listen(3000, () => {
    console.log("server running at http://120.26.137.179:3000");
});
