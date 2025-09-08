#!/bin/bash

# This script scaffolds Kotlin files for Advent of Code challenges.
# It copies a template, creates a directory for each day,
# and replaces a placeholder with the correct day number.

# --- Configuration ---
# The template file to be copied.
TEMPLATE_FILE="day00/Day00.kt"
# The first day to generate files for.
START_DAY=5
# The last day to generate files for.
END_DAY=25

# --- Script Logic ---
# Check if the template file exists before starting.
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo "Error: Template file not found at '$TEMPLATE_FILE'"
    exit 1
fi

echo "Starting to generate files from day $START_DAY to $END_DAY..."

# Loop through the specified range of days.
# The `seq` command with the '-w' flag automatically pads the numbers with a leading zero.
for i in $(seq -w $START_DAY $END_DAY)
do
    # Define the directory and file names based on the current day number.
    DIR_NAME="day$i"
    FILE_NAME="Day$i.kt"
    DEST_PATH="$DIR_NAME/$FILE_NAME"

    # Create the directory for the day. The '-p' flag prevents errors if it already exists.
    mkdir -p "$DIR_NAME"

    # Use sed (stream editor) to replace all occurrences of __DAY__ with the current day number.
    # The output is then redirected to create the new file.
    sed "s/__DAY__/$i/g" "$TEMPLATE_FILE" > "$DEST_PATH"

    echo "Created $DEST_PATH"
done

echo "Script finished successfully!"
