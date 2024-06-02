FROM docker.io/nginxinc/nginx-unprivileged:stable

WORKDIR /usr/share/nginx/html
COPY src/main/html/. .