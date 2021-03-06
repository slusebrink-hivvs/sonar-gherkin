#!/usr/bin/env bash
echo "use latest docker greater than 1.12 on Windows 10 with HyperV support"
echo "use maven in your host system with PATH support"
echo "on MSWIN use msgit with linux extension sed,grep and others"

mvn clean test package
 
docker run -d --name=sq-develop -p 9000:9000 sonarqube:alpine || docker stop sq-develop
docker cp ./target/*SNAPSHOT.jar sq-develop:/opt/sonarqube/extensions/plugins/
docker start sq-develop
