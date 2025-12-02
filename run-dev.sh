#!/bin/bash
./gradlew bootRun -Dvaadin.ignoreVersionChecks=true --args='--spring.profiles.active=dev'
