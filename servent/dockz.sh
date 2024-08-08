#!/usr/bin/env sh

set -e

# Variables
IMAGE_NAME="siester:local"


# Build the Docker image
echo "Building image $IMAGE_NAME"
docker build -t $IMAGE_NAME .

# Spin up Docker container
echo "Spinning up docker"

docker run --network desktop_p2p-network -e DISPLAY=host.docker.internal:0 -v /tmp/.X11-unix:/tmp/.X11-unix $IMAGE_NAME


echo "DONE"

