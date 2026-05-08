#!/bin/sh
export CLASSPATH=$PWD/gradle/wrapper/gradle-wrapper.jar
java -Xmx1024m -Dorg.gradle.appname=gradlew -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
