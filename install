#!/usr/bin/env bash
projectdir=$(pwd)
warname="twitterRest"

warfile="$projectdir/target/twitter-keywordAPI-1.0-SNAPSHOT.war"
warfolders="$projectdir/target/twitter-keywordAPI-1.0-SNAPSHOT/"
webxml="$projectdir/src/main/webapp/WEB-INF/web.xml"

webappsFolder="/var/lib/tomcat7/webapps"
projectFolder="$webappsFolder/$warname"

# clean up first
sudo rm -rf $projectFolder
sudo mkdir -p $projectFolder
echo "$warfile -> ${webappsFolder}/$warname"
sudo cp $warfile 		 ${webappsFolder}/${warname}.war
echo "$(ls $warfolders) -> $projectFolder"
sudo cp $warfolders/* -r $projectFolder/
echo "$webxml -> $projectFolder/WEB-INF"
sudo cp $webxml -r $projectFolder/WEB-INF

