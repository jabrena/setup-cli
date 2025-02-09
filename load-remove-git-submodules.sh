#!/bin/bash

# Check if the correct number of arguments is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 [c|r]"
    exit 1
fi

# Assign the arguments to variables
ACTION=$1

# Define the submodule URL
SUBMODULE_URL="https://github.com/jabrena/java-cursor-rules"
SUBMODULE_PATH="java-cursor-rules"

# Perform the action based on the first argument
case $ACTION in
    c)
        git submodule add $SUBMODULE_URL $SUBMODULE_PATH
        ;;
    r)
        git submodule deinit -f $SUBMODULE_PATH
        rm -rf $SUBMODULE_PATH
        git rm -f $SUBMODULE_PATH
        # Remove the corresponding section from .gitmodules
        if [ -f .gitmodules ]; then
            sed -i "/\[submodule \"$SUBMODULE_PATH\"\]/,/^$/d" .gitmodules
        fi
        ;;
    *)
        echo "Invalid action: Use 'c' to create or 'r' to remove."
        exit 1
        ;;
esac

echo "Action completed."