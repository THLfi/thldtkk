#!/bin/sh

# example of how to use curl to get a json file

read -p "Enter URL: " url
read -p "Enter username: " username
read -s -p "Enter password: " password
read -p "Target file: " target_file

curl -H "Content-type: application/json" -u $username:$password $url -o $target_file
