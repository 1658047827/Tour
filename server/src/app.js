import express from "express";
import { createServer } from "node:http";
import { Server } from "socket.io";

const app = express();
const server = createServer(app);
const io = new Server(server, {
    cors: {
        origin: "http://localhost:5173", // address of Vue app
        methods: ["GET", "POST"],
    },
});

io.on("connection", (socket) => {
    console.log(`Player ${socket.id} connected`);
    socket.playerData = { x: 0, y: 0, z: 0, heading: 0 };
    socket.emit("setId", { id: socket.id });

    socket.on("disconnect", () => {
        console.log(`Player ${socket.id} disconnected`);
        socket.broadcast.emit("deletePlayer", { id: socket.id });
    });

    socket.on("init", (data) => {
        console.log(`Initialize player ${socket.id}`);
        socket.playerData.id = socket.id;
        socket.playerData.model = data.model;
        socket.playerData.color = data.color;
        socket.playerData.x = data.x;
        socket.playerData.y = data.y;
        socket.playerData.z = data.z;
        socket.playerData.heading = data.heading;
        socket.playerData.pitch = data.pitch;
        socket.playerData.bank = data.bank;
        socket.playerData.action = data.action;
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
        io.to(data.id).emit("startChat", { id: socket.id });
    });

    socket.on("chatMessage", (data) => {
        io.to(data.id).emit("chatMessage", {
            id: socket.id,
            message: data.message,
        });
    });

    socket.on("endChat", (data) => {
        io.to(data.id).emit("endChat", { id: socket.id });
    });

    socket.on("getRemoteData", () => {
        let remoteData = {};
        for (let [id, socket] of io.sockets.sockets) {
            if (socket.playerData) remoteData[id] = socket.playerData;
        }
        if (Object.keys(remoteData).length > 0)
            socket.emit("remoteData", remoteData);
    });
});

server.listen(3000, () => {
    console.log("server running at http://localhost:3000");
});
