#!/bin/bash

# Check if start year is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <start_year> [end_year] [delay_in_seconds]"
  echo "  end_year defaults to start_year if not provided."
  echo "  delay_in_seconds defaults to 3 if not provided."
  exit 1
fi

START_YEAR=$1
END_YEAR=${2:-$START_YEAR}
DELAY=${3:-3}

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

# Loop from start year to end year
for year in $(seq ${START_YEAR} ${END_YEAR}); do
  BASE_DIR="app/src/main/resources/aoc${year}"
  # Create the base directory if it doesn't exist
  mkdir -p "${BASE_DIR}"

  # Loop from day 1 to 25
  for day in {1..25}; do
    # Format day with leading zero if needed
    DAY_PADDED=$(printf "%02d" ${day})
    URL="https://adventofcode.com/${year}/day/${day}/input"
    OUTPUT_FILE="${BASE_DIR}/day${DAY_PADDED}_input.txt"

    echo "Downloading input for ${year}, Day ${day}..."

    # Download the input using curl
    curl -s -b "session=${AOC_SESSION_COOKIE}" "${URL}" -o "${OUTPUT_FILE}"

    # Check if the download was successful
    if [ $? -eq 0 ]; then
      echo "Input saved to ${OUTPUT_FILE}"
    else
      echo "Failed to download input for ${year}, Day ${day}"
    fi

    # Pause between downloads
    echo "Pausing for ${DELAY} seconds..."
    sleep ${DELAY}
  done
done