#!/bin/sh

KIABORA="./apps/graal-kiabora.sh"
COMBINER="./tools/kiabora_to_combine.pl"

INPUT=$1
if [ ${INPUT} -eq ""]; then
	INPUT="-"
fi
${KIABORA} -p \* -r -s -c -b -f ${INPUT} 2> /dev/null | ${COMBINER} $2 2> /dev/null

