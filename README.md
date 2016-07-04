# Vagrant Server Proxy for Artifactory Repositories

Artifactory has built-in support for Vagrant repositories, but
- It cannot serve multi-provider boxes ([RTFACT-7536](https://www.jfrog.com/jira/browse/RTFACT-7536)).
- Available in commercial version only.

This service works on top of Artifactory instance.
It lists files, and presents them in Vagrant-compatible format.

Open-source edition of Artifactory can be used, because all boxes are stored as Maven artifacts.

# Usage

Prepare a repository:
- Run Artifactory server (example for [docker-compose](https://github.com/mkuzmin/vagrant-proxy/blob/master/docs/artifactory.yml)).
- Create Maven repository:
    - *Repository layout*: `maven-2-default` (default setting).
    - *Handle Releases*: enabled (default setting).
    - *Handle Snapshots*: disabled.
- Create user account and grant it *Deploy/Cache* permission.
- Publish boxes as Maven artifacts (example for [Gradle](https://github.com/mkuzmin/vagrant-proxy/blob/master/docs/upload.gradle)).


Run the service with docker-compose:
```
version: '2'
services:
  vagrant_proxy:
    image: mkuzmin/vagrant-proxy:latest
    environment:
      artifactoryUrl: http://artifactory.local/
      repository: vagrant
      organization: org
      redirectUrl: http://wiki.local/
    ports:
      - "80:8080"
```

The service is configured via environment variables:
- `artifactoryUrl`: an address of artifactory server
- `repository`: a repository name in Artifactory
- `organization`: a prefix for box names
- `redirectUrl` (optional): redirect to external documentation page, if users open the URL in a browser.

Add `box_server_url` option to Vagrantfiles:
```
Vagrant.configure(2) { |config|
  config.vm.box = 'org/windows'
  config.vm.box_server_url = 'http://vagrant.local'
}
```

# Health Checks

For external monitoring tools the service exposes `/health` resource.
It also validates availability of Artifactory server.
