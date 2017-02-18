#!/bin/bash
echo "RUNNING: local.sh"
echo "Name: $1"

#exit the script if any errors occur
set -e
if [ -z "$1" ]
    then
        echo "Name was not supplied and cannot be assigned to a default value"
        exit 1
    else
        name=$1
fi
cd ../

# Build image for
docker build --no-cache=true -t $name --file Dockerfile .

# Kill and delete the current docker container
set +e
docker rm -f $name
set -e

echo $PWD

docker run -d \
  --name $name \
  -p 9999:8080 \
  --restart always \
  -v $PWD/logs/docker:/usr/dev/gradle-java-jetty-template/logs \
  $name

set +e
echo CLEAN UP ALL THE DANGLING DOCKER IMAGES
docker rmi $(docker images -q -f dangling=true)
set -e

# curl and get status code to make sure it's good
for i in 1 2 3 4 5 6 7 8 9 10 ; do
    echo HEALTH CHECK ATTEMPT - $i
    sleep 10s

    status=$(curl -s -o /dev/null -w "%{http_code}\n" http://localhost:9999/api/health)
    if [ $status = "200" ] ; then
        echo SUCCESSFUL HEALTCHECK!
        exit 0
    fi
done

echo HEALTCHECK FAILED! STATUS CODE $status
exit 1
