#!/usr/bin/env sh
# Gradle wrapper script for Unix
APP_HOME=$(cd "${0%/*}" && pwd -P)
APP_BASE_NAME=$(basename "$0")
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        echo "ERROR: JAVA_HOME is invalid: $JAVA_HOME" >&2
        exit 1
    fi
else
    JAVACMD=java
    which java >/dev/null 2>&1 || { echo "ERROR: java not found in PATH"; exit 1; }
fi

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
  -Dorg.gradle.appname="$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain "$@"
