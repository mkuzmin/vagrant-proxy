FROM java:8-jre
MAINTAINER Michael Kuzmin "mkuzmin@gmail.com"
COPY ./build/libs/vagrant-proxy-*.jar /vagrant-proxy.jar
CMD ["java", "-jar", "/vagrant-proxy.jar"]
EXPOSE 8080
