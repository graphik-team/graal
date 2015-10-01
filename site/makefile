FLAGS= 
DEBUG_FLAGS=-D__debug

ifeq (${debug},1)
	FLAGS+=${DEBUG_FLAGS}
endif

all:\
prepare\
dest/index.html\
dest/ruleclasses.html\
dest/documentation.html\
dest/downloads.html\
dest/owl2dlgp.html\
dest/dlgp2ruleml.html\
dest/experiments1.html\
dest/experiments2.html\
dest/publications.html\
dest/pure.html\
dest/kiabora.html\
dest/ruleclasses.html\
dest/getting-started.html\
dest/getting-started-maven.html\
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

prepare:
	mkdir -p dest/modules

page-modules:
	mkdir -p src/modules
	./make-site.sh


dest/%.html: src/%.tt src/header.tt src/footer.tt src/config.tt src/nav.tt
	@ echo "gen $@"
	@ tpage --include_path=src/ ${FLAGS} $< | sed '/^\s*$$/d' | sed 's/\s*$$//g' > $@

install:
	cp -r dest/* ~/dev/graphik/graal-website
	cp -r dl/* ~/dev/graphik/graal-website/dl
	cp -r versions/* ~/dev/graphik/graal-website
	cp -r resources/* ~/dev/graphik/graal-website

clean:
	rm dest/*.html
	rm dest/modules/*.html

