#!/bin/sh

VERSION=0.3.dev

ID=`docker images -q mkuzmin/vagrant-proxy:latest`
docker tag $ID mkuzmin/vagrant-proxy:$VERSION
docker push mkuzmin/vagrant-proxy:$VERSION

# docker tag mkuzmin/vagrant-proxy:$VERSION mkuzmin/vagrant-proxy:latest
# docker push mkuzmin/vagrant-proxy:latest
