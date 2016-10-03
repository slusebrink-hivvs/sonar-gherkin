#!/bin/bash

if ! docker inspect --format="{{ .State.Running }}" sq-develop > /dev/null 
then
  echo "sq-develop container doesn't exist"
  docker run -d --name=sq-develop -p 9000:9000 sonarqube:alpine
fi

docker stop sq-develop

count=`ls -1 /vagrant/*.jar 2>/dev/null | wc -l`

if [ $count != 0 ]; then 
	echo updating JAR plugins
	docker cp /vagrant/*.jar sq-develop:/opt/sonarqube/extensions/plugins/
	rm /vagrant/*.jar
fi

docker start sq-develop
