#!/bin/bash
for var in "$@"
do
  magick "$var" -resize 960x540\> "$var"
  NEW=$(echo "$var" | sed s/.png//)
  magick "$var" "$NEW.webp"
done