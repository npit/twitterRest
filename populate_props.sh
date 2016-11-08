#!/usr/bin/env bash

keywordsFile="$1"
idsFile="$2"
logFile="$3"
propertiesFile="$4"

echo "Populating properties file: [$propertiesFile]"
echo ""

echo "keywords_file_path=$keywordsFile" >> $propertiesFile
echo "log_file_path=$logFile" >>   $propertiesFile
echo "twitter_ids_file=$idsFile" >> $propertiesFile
echo "delimiter=***" >> $propertiesFile
cat "$propertiesFile"
echo ""