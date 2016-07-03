#!/bin/sh

VERSION=0.2.dev

rm -rf build/
./gradlew build -Pversion=$VERSION
docker-compose build --pull --no-cache vagrant_proxy
