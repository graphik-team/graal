#!/bin/bash

GRAAL_DIR="."
GRAAL_APPS_CP="${GRAAL_DIR}/lib/sesame-model.jar:${GRAAL_DIR}/lib/sesame-rio-api.jar:${GRAAL_DIR}/lib/sesame-rio-datatypes.jar:${GRAAL_DIR}/lib/sesame-rio-languages.jar:${GRAAL_DIR}/lib/sesame-util.jar:${GRAAL_DIR}/lib/logback-classic.jar:${GRAAL_DIR}/lib/mysql-connector-java.jar:${GRAAL_DIR}/lib/slf4j-api.jar:${GRAAL_DIR}/lib/dlgp-parser.jar:${GRAAL_DIR}/lib/jcommander.jar:${GRAAL_DIR}/lib/logback-core.jar:${GRAAL_DIR}/lib/annotations.jar:${GRAAL_DIR}/lib/sqlite-jdbc.jar"

java -cp $GRAAL_APPS_CP:${GRAAL_DIR}/target/release/graal.jar fr.lirmm.graphik.graal.apps.CLI "$@"
