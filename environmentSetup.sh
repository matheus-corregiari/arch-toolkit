#
#           Circle CI & gradle.properties live in harmony
# 
# Android convention is to store your API keys in a local, non-versioned
# gradle.properties file. Circle CI doesn't allow users to upload pre-populated
# gradle.properties files to store this secret information, but instaed allows
# users to store such information as environment variables.
#
# This script creates a local gradle.properties file on current the Circle CI
# instance. It then reads environment variable TEST_API_KEY_ENV_VAR which a user
# has defined in their Circle CI project settings environment variables, and 
# writes this value to the Circle CI instance's gradle.properties file.
# 
# You must execute this script via your circle.yml as a pre-process dependency,
# so your gradle build process has access to all variables.
#
#   dependencies:
#       pre:
#        - source environmentSetup.sh && copyEnvVarsToGradleProperties

#!/usr/bin/env bash

function copyEnvVarsToGradleProperties {
    GRADLE_PROPERTIES=$HOME"/gradle.properties"
    export GRADLE_PROPERTIES
    echo "Gradle Properties should exist at $GRADLE_PROPERTIES"

    echo "Writing DEVELOPER_EMAIL to gradle.properties..."
    echo "DEVELOPER_EMAIL=$DEVELOPER_EMAIL" >> $GRADLE_PROPERTIES

    echo "Writing OSSRH_USERNAME to gradle.properties..."
    echo "OSSRH_USERNAME=$OSSRH_USERNAME" >> $GRADLE_PROPERTIES

    echo "Writing OSSRH_PASSWORD to gradle.properties..."
    echo "OSSRH_PASSWORD=$OSSRH_PASSWORD" >> $GRADLE_PROPERTIES
}