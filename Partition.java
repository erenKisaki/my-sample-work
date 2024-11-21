#!/bin/bash

# Directory containing JSON files
JSON_DIR=
ENDPOINT=
AUTH_TOKEN=
HTTP_METHOD="PUT"

if [[ ! -d "$JSON_DIR" ]]; then
    echo "Error: Directory $JSON_DIR does not exist."
    exit 1
fi

if [[ -z $(ls "$JSON_DIR"/*.json 2>/dev/null) ]]; then
    echo "Error: No JSON files found in $JSON_DIR."
    exit 1
fi

for json_file in "$JSON_DIR"/*.json; do
    echo "Processing $json_file..."

    if ! json_data=$(cat "$json_file"); then
        echo "Error reading $json_file. Skipping."
        continue
    fi

    response=$(curl -s -o /dev/null -w "%{http_code}" -X "$HTTP_METHOD" "$ENDPOINT" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $AUTH_TOKEN" \
        --data "$json_data")

    if [[ "$response" -eq 200 ]]; then
        echo "Successfully updated: $json_file"
    else
        echo "Failed to update $json_file. HTTP Status: $response"
    fi
done

echo "All files processed."
