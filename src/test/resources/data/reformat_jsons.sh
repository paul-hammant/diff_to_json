#!/bin/sh

IFS=$'\n'; set -f
for f in $(find . -name '*.json'); do
  cat "$f" | jq --sort-keys '.' | sponge "$f"
done
unset IFS; set +f