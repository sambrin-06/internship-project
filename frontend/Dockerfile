# Step 1: Build frontend
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Step 2: Serve using nginx
FROM nginxinc/nginx-unprivileged:stable-alpine

# Update packages to fix vulnerabilities
USER root
RUN apk update && apk upgrade && rm -rf /var/cache/apk/*
USER 101

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]

