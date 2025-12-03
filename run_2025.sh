#!/bin/bash

# Define the days that have input files available
DAYS_TO_RUN=(01 02 03)

for DAY in "${DAYS_TO_RUN[@]}"
do
  echo "Running Day $DAY"
  ./gradlew run -PmainClass=com.sphericalchickens.aoc2025.day$DAY.Day${DAY}Kt
done