#!/bin/bash

ALASKA_APPS_CP="../alaska/target/release/alaska.jar:../obda-kernel/target/release/obda-kernel.jar:../obda-parser/target/release/obda-parser.jar:lib/logback-classic.jar:lib/mysql-connector-java.jar:lib/slf4j-api.jar:lib/dlgp-parser.jar:lib/jcommander.jar:lib/logback-core.jar:lib/annotations.jar:lib/sqlite-jdbc.jar"

java -cp $ALASKA_APPS_CP:target/release/alaska-apps.jar fr.lirmm.graphik.alaska.apps.CLI "$@"
