# dcis-commons

> What man is a man who does not make the world better.
>
> -- Balian, Kingdom of Heaven

[![OCI-Container](https://github.com/Paladins-Inn/torganized-play-commons/actions/workflows/docker-build.yaml/badge.svg)](https://github.com/Paladins-Inn/torganized-play-commons/actions/workflows/docker-build.yaml)
[![Helm Chart](https://github.com/Paladins-Inn/torganized-play-commons/actions/workflows/helm-push.yaml/badge.svg)](https://github.com/Paladins-Inn/torganized-play-commons/actions/workflows/helm-push.yaml)


## Abstract

This is the common part of the DCIS.
It contains three seperate artifacts:

1. A Java Library containing commonly needed utility or configuration classes for all other SCSes.
2. An OCI container containing the /commons branch of the SCSes containing the web assets used by all other containers.
   This reduces web traffic since they can be cached.
3. The Helm chart for deploying the /commons branch container and the central message broker.
   The helm chart depends on the Kaiserpfalz EDV-Service charts for microservices and for rabbitMQ broker (managed via Operator)

## License

The license for the software is LGPL 3.0 or newer.

The UI is an adoption of the [Start Bootstrap](https://github.com/StartBootstrap/startbootstrap-sb-admin) theme which is MIT licensed.

## Architecture

tl;dr (ok, only the bullshit bingo words):

* Immutable Objects
* Relying heavily on generated code
* 100 % test coverage of human generated code
* Every line of code not written is bug free!

Code test coverage for human generated code should be 100%, machine generated code is considered bugfree until proven wrong.
But every line that needs not be written is a bug free line without need to test it. So aim for not writing code.

## A note from the author

If someone is interested in getting it faster, we may team up.
I'm open for that.
But be warned: I want to do it _right_.
So no short cuts to get faster.
And be prepared for some basic discussions about the architecture or software design :-).

---
Berlin, 2024-06-01<br/>
Updated: Bensheim, 2024-11-01