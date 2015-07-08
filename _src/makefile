FLAGS= 
DEBUG_FLAGS=-D__debug

ifeq (${debug},1)
	FLAGS+=${DEBUG_FLAGS}
endif

all: ../documentation.html  ../downloads.html ../index.html  ../owl2dlgp.html  ../experiments.html ../experiments1.html ../experiments2.html ../publications.html  ../pure.html

../%.html: %.html
	@ echo "gen $@"
	@ gcc -E -x c -P ${FLAGS} $< > $@

clean:
	rm ../*.html

