#!/usr/bin/env bash

echo "keywords_file_path=/var/lib/tomcat7/webapps/twitterRest/WEB-INF/search_keywords.txt" >> $1
echo "log_file_path=/var/log/tomcat7/twitterRest/logfile.log" >>   $1
echo "twitter_queries_file=/home/nik/dummyfile" >> $1
echo "delimiter=***" >> $1