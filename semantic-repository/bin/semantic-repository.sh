#!/bin/bash

SERVER_PORT=9004
JAR=semantic-repository-1.0-SNAPSHOT.jar

GRAPHDB_ENDPOINT=http://localhost:7200/repositories/vicinity-test
JSONLD_SCHEMA_LOCATION=file:///home/kostelni/work/eu-projekty/vicinity/idea-workspace/vicinity-semantic-platform/semantic-repository/src/test/resources/json-ld/thing.jsonld

COMMAND=$1

PID=$(ps -eaf | grep $JAR | grep server.port=$SERVER_PORT | grep -v grep | awk '{print $2}')

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
  echo "xstarting semantic repository"

    if [[ "" !=  "$PID" ]]; then
      echo "semantic repository is running"
    else
        rm nohup.out; nohup java -cp "../target/dependency/*:../target/$JAR" -Dserver.port=$SERVER_PORT -Dgraphdb.endpoint=$GRAPHDB_ENDPOINT -Djsonld.schema.location=$JSONLD_SCHEMA_LOCATION sk.intersoft.vicinity.platform.semantic.service.SemanticRepositoryServer &
        echo "semantic repository started"

    fi


fi



