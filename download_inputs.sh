#!/bin/bash

# Check if year is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <year>"
  exit 1
fi

YEAR=$1
BASE_DIR="app/src/main/resources/aoc${YEAR}"

# Check if .env file exists
if [ ! -f ".env" ]; then
  echo "Error: .env file not found."
  echo "Please create a .env file with your Advent of Code session cookie."
  echo "Example: AOC_SESSION_COOKIE=<your_cookie>"
  exit 1
fi

# Read session cookie from .env file
source .env

# Check if AOC_SESSION_COOKIE is set
if [ -z "$AOC_SESSION_COOKIE" ]; then
  echo "Error: AOC_SESSION_COOKIE not set in .env file."
  echo "Please make sure your .env file contains a line like: AOC_SESSION_COOKIE=<your_cookie>"
  exit 1
fi

# Create the base directory if it doesn't exist
mkdir -p "${BASE_DIR}"

# Loop from day 1 to 25
for day in {1..25}; do
  # Format day with leading zero if needed
  DAY_PADDED=$(printf "%02d" ${day})
  URL="https://adventofcode.com/${YEAR}/day/${day}/input"
  OUTPUT_FILE="${BASE_DIR}/day${DAY_PADDED}_input.txt"

  echo "Downloading input for ${YEAR}, Day ${day}..."

  # Download the input using curl
  curl -s -b "session=${AOC_SESSION_COOKIE}" "${URL}" -o "${OUTPUT_FILE}"

  # Check if the download was successful
  if [ $? -eq 0 ]; then
    echo "Input saved to ${OUTPUT_FILE}"
  else
    echo "Failed to download input for ${YEAR}, Day ${day}"
  fi
done
