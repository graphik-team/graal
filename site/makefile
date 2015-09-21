FLAGS= 
DEBUG_FLAGS=-D__debug

ifeq (${debug},1)
	FLAGS+=${DEBUG_FLAGS}
endif

all:\
dest/index.html\
dest/documentation.html\
dest/downloads.html\
dest/owl2dlgp.html\
dest/dlgp2ruleml.html\
dest/experiments.html\
dest/experiments1.html\
dest/experiments2.html\
dest/publications.html\
dest/pure.html\
dest/getting-started.html\
dest/getting-started1.html\
dest/modules/graal-util.html\
dest/modules/graal-core.html\
dest/modules/graal-backward-chaining.html\
dest/modules/graal-forward-chaining.html\
dest/modules/graal-homomorphism.html\
dest/modules/graal-io-api.html\
dest/modules/graal-io-dlgp.html\
dest/modules/graal-io-owl.html\
dest/modules/graal-io-ruleml.html\
dest/modules/graal-io-sparql.html\
dest/modules/graal-store-api.html\
dest/modules/graal-store-rdbms.html

page-modules:
	./make-site.sh

dest/%.html: src/%.html
	@ echo "gen $@"
	@ gcc -E -x c -P ${FLAGS} $< > $@  & true

install:
	cp -r dest/* ~/dev/graphik/graal-website
clean:
	rm dest/*.html
	rm dest/modules/*.html

