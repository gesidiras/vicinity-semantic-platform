#!/bin/bash

SERVER_PORT=9004
MAIN_CLASS=sk.intersoft.vicinity.platform.semantic.service.SemanticRepositoryServer

RESTLET_LOG_CONFIG=/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-semantic-platform/semantic-repository/bin/build/config/logging/restlet.properties
LOGBACK_CONFIG=/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-semantic-platform/semantic-repository/bin/build/config/logging/logback.xml

GRAPHDB_ENDPOINT=http://localhost:9003/repositories/vicinity-test
JSONLD_SCHEMA_LOCATION=file:///home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-semantic-platform/semantic-repository/config/json-ld/thing.jsonld

COMMAND=$1

PID=$(ps -eaf | grep $MAIN_CLASS | grep server.port=$SERVER_PORT | grep -v grep | awk '{print $2}')

echo "command: $COMMAND"
echo "pid: $PID"

if [[ $COMMAND ==  "stop" ]]; then
  echo "stopping semantic repository"

    if [[ "" !=  "$PID" ]]; then
      echo "killing: $PID"
      kill -15 $PID
    else
      echo "process not found"
    fi


else
  echo "starting semantic repository"

    if [[ "" !=  "$PID" ]]; then
      echo "semantic repository is running"
    else
        rm nohup.out;
        nohup java -cp "lib/*" \
            -Dserver.port=$SERVER_PORT \
            -Dgraphdb.endpoint=$GRAPHDB_ENDPOINT \
            -Djsonld.schema.location=$JSONLD_SCHEMA_LOCATION \
            -Dlogback.configurationFile=$LOGBACK_CONFIG \
            -Djava.util.logging.config.file=$RESTLET_LOG_CONFIG \
            $MAIN_CLASS &
        echo "semantic repository started"

    fi


fi



