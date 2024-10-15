FROM docker.io/nginxinc/nginx-unprivileged:stable

USER 0
RUN apt-get update -y && apt-get upgrade -y

USER 1001
WORKDIR /usr/share/nginx/html
COPY --chown=1001:1001 --chmod=755 src/main/html/. .