#!/usr/bin/env bash

mvn clean package
projectdir=$(pwd)
warname="twitterRest"

warfile="$projectdir/target/twitter-keywordAPI-1.0-SNAPSHOT.war"
warfolders="$projectdir/target/twitter-keywordAPI-1.0-SNAPSHOT/"
webxml="$projectdir/src/main/webapp/WEB-INF/web.xml"

webappsFolder="/var/lib/tomcat7/webapps"
tomcatProjectFolder="$webappsFolder/$warname"

# clean up first
sudo  rm -rf $tomcatProjectFolder ${webappsFolder}/${warname}.war
sudo  mkdir -p $tomcatProjectFolder

# copy
echo "$warfile -> ${webappsFolder}/$warname"
sudo  cp $warfile 		 ${webappsFolder}/${warname}.war
echo "$(ls $warfolders) -> $tomcatProjectFolder"
sudo  cp $warfolders/* -r $tomcatProjectFolder/
echo "$webxml -> $tomcatProjectFolder/WEB-INF"
sudo  cp $webxml -r $tomcatProjectFolder/WEB-INF

# make auxiliary files & folders
configFilesFolder="$tomcatProjectFolder/WEB-INF"
keywordsFile="$configFilesFolder/keywords.txt"
idsFile="$configFilesFolder/tweetids.txt"
logFile="/var/log/tomcat7/twitterRest/logfile.log"
propertiesFile="$configFilesFolder/twitterrest.properties"
sudo  mkdir -p /var/log/tomcat7/twitterRest


sudo  touch "$propertiesFile" "$keywordsFile" "$idsFile" "$logFile"

sudo chmod 777 "$keywordsFile" "$idsFile" "$logFile" "$propertiesFile"
sudo ./populate_props.sh "$keywordsFile" "$idsFile" "$logFile" "$propertiesFile" 
#sudo chown -R tomcat7:tomcat7 $tomcatProjectFolder
sudo chown -R tomcat7:tomcat7 /var/log/tomcat7/twitterRest/
