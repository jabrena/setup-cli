#!/bin/bash

# Check if the correct number of arguments is provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 [c|r] [agile|java|tasks]"
    exit 1
fi

# Assign the arguments to variables
ACTION=$1
SUBMODULE_NAME=$2

# Define submodule properties based on the name
case $SUBMODULE_NAME in
    agile)
        SUBMODULE_URL="https://github.com/jabrena/cursor-rules-agile"
        SUBMODULE_PATH="templates/cursor-rules-agile"
        ;;
    java)
        SUBMODULE_URL="https://github.com/jabrena/cursor-rules-java"
        SUBMODULE_PATH="templates/cursor-rules-java"
        ;;
    tasks)
        SUBMODULE_URL="https://github.com/jabrena/cursor-rules-tasks"
        SUBMODULE_PATH="templates/cursor-rules-tasks"
        ;;
    *)
        echo "Invalid submodule name: Use 'agile', 'java', or 'tasks'."
        exit 1
        ;;
esac

# Perform the action based on the first argument
case $ACTION in
    c)
        # Clean up any leftover .git files from previous attempts
        if [ -d "$SUBMODULE_PATH/.git" ] || [ -f "$SUBMODULE_PATH/.git" ]; then
            rm -rf "$SUBMODULE_PATH"
        fi

        # Clean up any previous entries in .git/modules
        if [ -d ".git/modules/$SUBMODULE_PATH" ]; then
            rm -rf ".git/modules/$SUBMODULE_PATH"
        fi

        # Add the submodule with force option
        echo "Adding submodule $SUBMODULE_NAME from $SUBMODULE_URL to $SUBMODULE_PATH"
        git submodule add --force $SUBMODULE_URL $SUBMODULE_PATH
        ;;
    r)
        echo "Removing submodule $SUBMODULE_NAME from $SUBMODULE_PATH"
        # Deinitialize the submodule
        git submodule deinit -f $SUBMODULE_PATH

        # Remove the submodule directory
        rm -rf $SUBMODULE_PATH

        # Remove from git
        git rm -f $SUBMODULE_PATH

        # Clean up .git/modules
        if [ -d ".git/modules/$SUBMODULE_PATH" ]; then
            rm -rf ".git/modules/$SUBMODULE_PATH"
        fi

        # Remove the corresponding section from .gitmodules
        if [ -f .gitmodules ]; then
            # Use sed in a way compatible with both GNU and BSD (macOS) sed
            if [[ "$OSTYPE" == "darwin"* ]]; then
                # macOS requires an extension argument with -i
                sed -i '' "/\[submodule \"$SUBMODULE_PATH\"\]/,/^$/d" .gitmodules
            else
                # Linux version
                sed -i "/\[submodule \"$SUBMODULE_PATH\"\]/,/^$/d" .gitmodules
            fi

            # If .gitmodules is empty after the removal, delete it
            if [ ! -s .gitmodules ]; then
                rm .gitmodules
                git add .gitmodules # Stage the deletion of .gitmodules
            else
                git add .gitmodules # Stage the changes to .gitmodules
            fi
        fi
        ;;
    *)
        echo "Invalid action: Use 'c' to create or 'r' to remove."
        exit 1
        ;;
esac

echo "Action '$ACTION' for submodule '$SUBMODULE_NAME' completed successfully."
