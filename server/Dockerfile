FROM node:20-slim
WORKDIR /usr/src/socketio
COPY . .
RUN npm install
CMD cd /usr/src/socketio/src
CMD node app.js