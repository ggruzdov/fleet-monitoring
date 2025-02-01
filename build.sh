#!/bin/bash

cd feeder &&
./mvnw clean package -DskipTests=true &&
docker build --no-cache -t ggruzdov/fleet-monitoring-feeder:1.0 . &&

cd ../processor &&
./mvnw clean package -DskipTests=true &&
docker build --no-cache -t ggruzdov/fleet-monitoring-processor:1.0 . &&

cd ../api &&
./mvnw clean package -DskipTests=true &&
docker build --no-cache -t ggruzdov/fleet-monitoring-api:1.0 .