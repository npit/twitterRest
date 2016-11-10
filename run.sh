#!/usr/bin/env bash

# Execution script for twitterREST service.
# Only really makes sense if run from a dockerfile

echo "Starting the apache tomcat7 web server."
service tomcat7 start
echo "Done."