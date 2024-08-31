#/usr/bin/env sh

set -e

# Variables
IMAGE_NAME="siester:local"
DISPLAY="host.docker.internal:0"

# Build the Docker image
echo "Building image $IMAGE_NAME"
docker build --no-cache -t $IMAGE_NAME .

# Spin up Docker container
echo "Spinning up docker"

docker run --network desktop_p2p-network -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v ~/Desktop/siester_download:/root/siester/shared/download $IMAGE_NAME


echo "DONE"

