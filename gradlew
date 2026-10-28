#!/bin/sh
#
# Gradle start up script for UN*X
#
set -e
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
APP_NAME="Gradle"
APP_BASE_NAME="$(basename "$0")"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
JAVACMD="${JAVA_HOME:-}/bin/java"
if [ ! -x "$JAVACMD" ]; then JAVACMD=java; fi
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
