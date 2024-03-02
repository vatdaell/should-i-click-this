#!/bin/bash

# Define the directory path
dir=~/app

mkdir "$dir"
echo "Directory $dir created."

echo "Starting Load"

# Load domains
curl https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-domains.tar.gz --output $dir/ALL-phishing-domains.tar.gz
tar -xvzf $dir/ALL-phishing-domains.tar.gz -C  $dir
echo "ALL-phishing-domains.txt loaded successfully at $dir/ALL-phishing-domains.txt"

# Load links
curl https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-links.tar.gz --output $dir/ALL-phishing-links.tar.gz
tar -xvzf $dir/ALL-phishing-links.tar.gz -C  $dir
echo "ALL-phishing-links.txt loaded successfully at $dir/ALL-phishing-links.txt"

