FROM docker.io/nginxinc/nginx-unprivileged:stable

WORKDIR /usr/share/nginx/html
COPY --chown=1001:1001 --chmod=755 src/main/html/. .