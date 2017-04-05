#!/bin/sh

# example of how to use curl to post a json file

read -p "Enter URL: " url
read -p "Enter username: " username
read -s -p "Enter password: " password
read -p "Source file: " source_file

curl -H "Content-type: application/json" -u $username:$password -d @$source_file $url
