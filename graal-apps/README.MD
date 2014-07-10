GRAAL-CLI
=========

This entrypoint allows one to communicate with a database atomset through GRAAL.
For now only SQLite database are handled.


# Features
	- SQLite atomset
	- Chase
	- Querying (union of conjunctive queries)

# Chase
	- The chase variant used by GRAAL-CLI is the *restricted chase*
	- Note that while there is an argument that allows to bound the number of chase steps,
	the way it is implemented in GRAAL does not ensure exactly this bound

# Input
	- One can use a dlgp file as input to build the knowledge base
	- For now, only the fact is stored in persistent memory, therefore, 
	rules must be given to GRAAL-CLI at each call which may need them

# Query
	- All queries in the dlgp file will be considered as different conjunctive queries
	- One can also specify some set of conjunctive queries to be considered as a union
	- If some query is given to GRAAL-CLI (either from a file, or from the ucq option),
	all answers will be printed to stdout

# Options
	- help: will print a short description of all options
	- verbose: will enable verbose mode, note that to get debug logs one should also configure
	the logback.xml file
	- file: specify a dlgp file to be parsed, any fact will be add to the database atomset, while any rule
	or query will be stored temporarily to allow querying / saturating ; one can also use '-' as a file to 
	read from stdin
	- database: specify the database file to use to store the atomset 
	(if not specified, will default to '_default_graal.db')
	- ucq: allow the use of union of conjunctive queries, which can either be specified in a file, or in a string
	(both in dlgp format)
	- saturate: will saturate the atomset with the given rules, one can specify a bound for the number of saturation
	steps (the bound is not completely handled by GRAAL, but will still allow one to use it for non FES rules)

