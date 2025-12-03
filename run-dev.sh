#!/bin/bash
./gradlew -t bootRun -Dvaadin.ignoreVersionChecks=true --args='--spring.profiles.active=dev'
