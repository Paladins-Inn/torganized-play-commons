FROM docker.io/nginxinc/nginx-unprivileged:stable

LABEL maintainer="Kaiserpfalz EDV-Service <support@kaiserpfalz-edv.de>" \
    org.opencotnainers.image.description="Unprivileged NGINX Container for DCIS Commons" \
    org.opencontainers.image.title="dcis-commons" \
    org.opencontainers.image.url="https://quay.io/paladinsinn/dcis/dcis-commons" \
    org.opencontainers.image.version=""

USER 0
RUN apt-get update -y && apt-get upgrade -y

USER 1001
WORKDIR /usr/share/nginx/html
COPY --chown=1001:1001 --chmod=755 src/main/html/. .