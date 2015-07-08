FLAGS= 
DEBUG_FLAGS=-D__debug

ifeq (${debug},1)
	FLAGS+=${DEBUG_FLAGS}
endif

all:../index.html\
../documentation.html\
../downloads.html\
../owl2dlgp.html\
../dlgp2ruleml.html\
../experiments.html\
../experiments1.html\
../experiments2.html\
../publications.html\
../pure.html

../%.html: %.html
	@ echo "gen $@"
	@ gcc -E -x c -P ${FLAGS} $< > $@

clean:
	rm ../*.html

