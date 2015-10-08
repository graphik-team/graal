#!/bin/sh

GRAAL_DIR="."
GRAAL_CLI_FILE="${GRAAL_DIR}/apps/graal-cli.sh"
GRAAL_KIABORA_FILE="${GRAAL_DIR}/apps/graal-kiabora.sh"
GRAAL_FGH_FILE="${GRAAL_DIR}/apps/graal-fgh.sh"
GRAAL_PURE_FILE="${GRAAL_DIR}/apps/graal-pure.sh"
GRAAL_RULES_FILE="${GRAAL_DIR}/apps/graal-labeler.sh"
GRAAL_CONVERTER_FILE="${GRAAL_DIR}/apps/graal-converter.sh"
GRAAL_RULETOOLS_FILE="${GRAAL_DIR}/apps/graal-tools-rules.sh"

# compute library files
LIBS=$(ls -l ${GRAAL_DIR}/lib | head -n 2 | tail -n 1 | perl -n -e '/.* (.*)/ && print "\${GRAAL_DIR}/lib/$1"')
LIBS=${LIBS}$(ls -l ${GRAAL_DIR}/lib | tail -n +2 | perl -n -e '/.* (.*)/ && print ":\${GRAAL_DIR}/lib/$1"')

if [[ ! -d ${GRAAL_DIR}/apps ]]; then
	mkdir ${GRAAL_DIR}/apps;
fi;

echo "#!/bin/sh" > "${GRAAL_CLI_FILE}";
echo "" >> "${GRAAL_CLI_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_CLI_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_CLI_FILE}";
echo "" >> "${GRAAL_CLI_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_CLI_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.CLI \"\$@\"" >> "${GRAAL_CLI_FILE}";
chmod +x "${GRAAL_CLI_FILE}"

echo "#!/bin/sh" > "${GRAAL_KIABORA_FILE}";
echo "" >> "${GRAAL_KIABORA_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_KIABORA_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_KIABORA_FILE}";
echo "" >> "${GRAAL_KIABORA_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_KIABORA_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.Kiabora \"\$@\"" >> "${GRAAL_KIABORA_FILE}";
chmod +x "${GRAAL_KIABORA_FILE}"

echo "#!/bin/sh" > "${GRAAL_FGH_FILE}";
echo "" >> "${GRAAL_FGH_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_FGH_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_FGH_FILE}";
echo "" >> "${GRAAL_FGH_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_FGH_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.CLI_FGH \"\$@\"" >> "${GRAAL_FGH_FILE}";
chmod +x "${GRAAL_FGH_FILE}"

echo "#!/bin/sh" > "${GRAAL_PURE_FILE}";
echo "" >> "${GRAAL_PURE_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_PURE_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_PURE_FILE}";
echo "" >> "${GRAAL_PURE_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_PURE_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.PureRewriter \"\$@\"" >> "${GRAAL_PURE_FILE}";
chmod +x "${GRAAL_PURE_FILE}"

echo "#!/bin/sh" > "${GRAAL_RULES_FILE}";
echo "" >> "${GRAAL_RULES_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_RULES_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_RULES_FILE}";
echo "" >> "${GRAAL_RULES_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_RULES_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.RuleLabeler \"\$@\"" >> "${GRAAL_RULES_FILE}";
chmod +x "${GRAAL_RULES_FILE}"

echo "#!/bin/sh" > "${GRAAL_CONVERTER_FILE}";
echo "" >> "${GRAAL_CONVERTER_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_CONVERTER_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_CONVERTER_FILE}";
echo "" >> "${GRAAL_CONVERTER_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_CONVERTER_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.GraalConverter \"\$@\"" >> "${GRAAL_CONVERTER_FILE}";
chmod +x "${GRAAL_CONVERTER_FILE}"

echo "#!/bin/sh" > "${GRAAL_RULETOOLS_FILE}";
echo "" >> "${GRAAL_RULETOOLS_FILE}";
echo "GRAAL_DIR=\"${GRAAL_DIR}\"" >> "${GRAAL_RULETOOLS_FILE}";
echo "GRAAL_RELEASE=\"\${GRAAL_DIR}/target/release/graal.jar/\"" >> "${GRAAL_RULETOOLS_FILE}";
echo "" >> "${GRAAL_RULETOOLS_FILE}";
echo "GRAAL_CP=\"${LIBS}\"" >> "${GRAAL_RULETOOLS_FILE}";
echo "java -cp \${GRAAL_CP}:\${GRAAL_RELEASE} fr.lirmm.graphik.graal.apps.GraalTools \"\$@\"" >> "${GRAAL_RULETOOLS_FILE}";
chmod +x "${GRAAL_RULETOOLS_FILE}"



