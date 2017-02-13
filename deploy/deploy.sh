#!/bin/bash
echo "RUNNING: deployment.sh"
echo "Name: $1"
echo "Domain: $2"
echo "Email: $3"

#exit the script if any errors occur
set -e
if [ -z "$1" ]
    then
        echo "Name was not supplied and cannot be assigned to a default value"
        exit 1
    else
        name=$1
fi

if [ -z "$2" ]
    then
        echo "Domain was not supplied, defaulting to krashidbuilt.net"
        domain=krashidbuilt.net
    else
        domain=$2
fi

if [ -z "$3" ]
    then
        echo "Email was not supplied, defaulting to ben@krashidbuilt.com"
        email=ben@krashidbuilt.com
    else
        email=$3
fi

echo "Assigning the DNS record to the current server IP address"
./assign-domain.sh $domain $name

cd ../

host=$name.$domain

# Build image for
docker build --no-cache=true -t $name --file Dockerfile .

# Kill and delete the current docker container
set +e
docker rm -f $name
set -e

docker run -d \
  --name $name \
  -P \
  -e "VIRTUAL_HOST=$host" \
  -e "LETSENCRYPT_HOST=$host" \
  -e "LETSENCRYPT_EMAIL=$email" \
  --restart always \
  -v /home/ec2-user/logs/$name:/usr/dev/ble-java-api/logs \
  $name

set +e
echo CLEAN UP ALL THE DANGLING DOCKER IMAGES
docker rmi $(docker images -q -f dangling=true)
set -e

# curl and get status code to make sure it's good
for i in 1 2 3 4 5 6 7 8 9 10 ; do
    echo HEALTH CHECK ATTEMPT - $i
    sleep 10s

    status=$(curl -s -o /dev/null -w "%{http_code}\n" https://$host/api/health)
    if [ $status = "200" ] ; then
        echo SUCCESSFUL HEALTCHECK!
        exit 0
    fi
done

echo HEALTCHECK FAILED! STATUS CODE $status
exit 1