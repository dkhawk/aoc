#!/bin/bash

# Default to today's date (December 1, 2025)
DEFAULT_YEAR=$(date +"%Y")
DEFAULT_DAY=$(date +"%-d")

# Check if year and day are provided
YEAR=${2:-$DEFAULT_YEAR}
DAY=${1:-$DEFAULT_DAY}

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

BASE_DIR="app/src/main/resources/aoc${YEAR}"
# Create the base directory if it doesn't exist
mkdir -p "${BASE_DIR}"

# Format day with leading zero if needed
DAY_PADDED=$(printf "%02d" ${DAY})
URL="https://adventofcode.com/${YEAR}/day/${DAY}/input"
OUTPUT_FILE="${BASE_DIR}/day${DAY_PADDED}_input.txt"

echo "Downloading input for ${YEAR}, Day ${DAY}..."

# Download the input using curl
curl -s -b "session=${AOC_SESSION_COOKIE}" "${URL}" -o "${OUTPUT_FILE}"

# Check if the download was successful
if [ $? -eq 0 ]; then
  echo "Input saved to ${OUTPUT_FILE}"
else
  echo "Failed to download input for ${YEAR}, Day ${DAY}"
fi
