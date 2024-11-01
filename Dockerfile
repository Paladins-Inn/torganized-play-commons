FROM docker.io/nginxinc/nginx-unprivileged:stable

LABEL maintainer="Paladins Inn Software Engineering <support@kaiserpfalz-edv.de>" \
    org.opencontainers.image.authors="Paladins Inn Software Engineering <support@kaiserpfalz-edv.de>" \
    org.opencontainers.image.description="Container for DCIS Commons" \
    org.opencontainers.image.title="dcis-commons" \
    org.opencontainers.image.source="https://github.com/paladinsinn/torganized-play-commons" \
    org.opencontainers.image.url="https://quay.io/paladinsinn/dcis/dcis-commons" \
    org.opencontainers.image.version="" \
    org.opencontainers.image.licenses="Apache-2.0, LGPL-3.0+"

USER 0
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get upgrade -y

USER 1001
WORKDIR /usr/share/nginx/html
COPY --chown=1001:1001 --chmod=755 src/main/html/. .