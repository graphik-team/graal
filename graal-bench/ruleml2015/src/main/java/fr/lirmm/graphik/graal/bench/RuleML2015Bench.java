/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.bench;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.stream.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleML2015Bench {
	
	@Parameter(names = { "-q", "--queries" }, description = "DLP queries file")
	private String queries_file = "";
	
	@Parameter(names = { "-o", "--onto" }, description = "OWL onto file")
	private String onto_file = "";
	
	@Parameter(names = { "-d", "--data" }, description = "data directory (UniversityX_Y.owl)")
	private String data_dir = "";
	
	@Parameter(names = { "--basename" }, description = "basename for database naming")
	private String basename = "lubm-ex";
	
	@Parameter(names = { "--db" }, description = "MYSQL | SQLITE")
	private String dbType = "SQLITE";
	
	@Parameter(names = { "-u", "--univ" }, description = "number of university to load")
	private int nb_univ = 1;
	
	

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;


	private static final boolean showData = false;
	private static final boolean showUCQ = false;
	private static final boolean enableSaturation = false;
	private static final boolean showResults = false;

	static Profiler profiler = new Profiler(System.out);
	static DlgpWriter writer;
	
	public static RuleML2015Bench options;


	
	public static void main(String[] args) throws AtomSetException, OWL2ParserException, IOException, DriverException, HomomorphismFactoryException, ChaseException {
		
		options = new RuleML2015Bench();
		JCommander commander = new JCommander(options, args);
		
		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		int i = 0;
		writer = new DlgpWriter(System.out);
		
		Store store_unsat = BenchUtils.getStoreUnsat(options.dbType, options.basename);
		Store store_semisat = BenchUtils.getStoreSat(options.dbType, options.basename);

		Iterable<ConjunctiveQuery> queries = getQueries();
		Iterable<Rule> rules = getOntology();
		
//		// compilation
		RulesCompilation compilation = new IDCompilation();
		profiler.start("compilation time");
		compilation.compile(rules.iterator());
		profiler.stop("compilation time");
		
		// data
//		profiler.add("store size before load data", BenchUtils.sizeOfStoreUnsat());
		if(!options.data_dir.isEmpty()) {
			profiler.start("load data time");
			File dir = new File(options.data_dir);
			for(int univ = 0; univ < options.nb_univ; ++univ) {
				
				i = -1;
				while(true) {
					++i;
					
					File f = new File(dir, "University" + univ +"_" + i +".owl");
					if(!f.exists()) {
						break;
					}
					
					writer.write("load " + f.getName() + "\n");
					Parser<Object> parser = new OWL2Parser(f);
					store_unsat.addAll(new AtomFilterIterator(parser));
					parser.close();
					
				} 
				
				
			}
			profiler.stop("load data time");

			if(showData) {
				writer.write(store_unsat.iterator());
			}
			
			profiler.start("store semisat load Time");
			store_semisat.addAll(store_unsat.iterator());
			profiler.stop("store semisat load Time");
	
			// semi-saturation
			if(enableSaturation) {
				writer.write("############ CHASE ##############\n");
				Chase chase = new NaiveChase(compilation.getSaturation(), store_semisat);
				profiler.start("store semisaturation time");
				chase.next();
				profiler.start("store semisaturation time");
			}
		}
		
		// ucq pivots
		for(ConjunctiveQuery q : queries) {
			UnionConjunctiveQueries ucqPivot = null;
			try {
				writer.write(q);
				PureRewriter rewriter = new PureRewriter(q, rules, compilation);
				rewriter.enableUnfolding(false);
				profiler.start(q.getLabel() + " rewriting time");
				ucqPivot = new UnionConjunctiveQueries(rewriter);
				profiler.stop(q.getLabel() + " rewriting time");
				ucqPivot.setLabel(q.getLabel());
				
				int cq_count = 0;
				for(ConjunctiveQuery tmp : ucqPivot) {
					++cq_count;
				}
				profiler.add(q.getLabel() + " pivot size", cq_count);
				writer.write(ucqPivot);
				
				executeQueries(store_semisat, ucqPivot);
			} catch (HomomorphismException e) {
				writer.write(q.getLabel() + ": " + e.getMessage() + "\n");
			} catch (Error e) {
				writer.write(q.getLabel() + ": " + e.getMessage() + "\n");
			} 
			
			// UCQ
			if(ucqPivot != null) {
				try {
					profiler.start(ucqPivot.getLabel() + " unfold time");
					UnionConjunctiveQueries ucq = new UnionConjunctiveQueries(compilation.unfold(ucqPivot).iterator());
					profiler.stop(ucqPivot.getLabel() + " unfold time");
					ucq.setLabel(ucqPivot.getLabel() + " UCQ classic");
					int cq_count = 0;
					for(ConjunctiveQuery tmp : ucq) {
						if(showUCQ) {
							writer.write(tmp);
						}
						++cq_count;
					}
					profiler.add(ucqPivot.getLabel() + " UCQ classic size", cq_count);
					
					executeQueries(store_unsat, ucq);
				} catch (HomomorphismException e) {
					writer.write(ucqPivot.getLabel() + ": " + e.getMessage() + "\n");
				} catch (Error e) {
					writer.write(ucqPivot.getLabel() + ": " + e.getMessage() + "\n");
				}
			}
			writer.writeComment("---------------------------------------------------");
		}
			
	}
	
	public static void executeQueries(Store store, UnionConjunctiveQueries ucq) throws HomomorphismFactoryException, HomomorphismException, IOException {
		profiler.start(ucq.getLabel() + " answering time");
		Iterator<Substitution> results = StaticHomomorphism.executeQuery(ucq, store);
		profiler.stop(ucq.getLabel() + " answering time");
		int i = 0;
		while (results.hasNext()) {
			++i;
			if(showResults) {
				writer.write(results.next().toString());
			} else {
				results.next();
			}
			//writer.write(s.toString());
		}
		profiler.add(ucq.getLabel() + " nb results", i);
	}

	public static Iterable<ConjunctiveQuery> getQueries() throws IOException {
		File f = new File(options.queries_file);
		DlgpParser parser = new DlgpParser(f);
		LinkedList<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();
		while (parser.hasNext()) {
			Object o = parser.next();
			if(o instanceof ConjunctiveQuery) {
				// patch for dlp2
				ConjunctiveQuery query = (ConjunctiveQuery) o;
				for(Atom a : query) {
					List<Term> terms = a.getTerms();
					for(int i = 0; i < terms.size(); ++i) {
						Term t = terms.get(i);
						String s = t.getIdentifier().toString();
						if(s.charAt(0) == '<') {
							a.setTerm(
									i,
									DefaultTermFactory.instance()
											.createConstant(
													s.substring(1,
															s.length() - 1)));
						}
					}
					
				}
				queries.add(query);
			}
			
		}
		parser.close();
		return queries;
	}
	
	public static void writeData(Iterator<Atom> it) throws IOException {
		writer.write("########## DATA #############");
		int i = 0;
		while(it.hasNext()) {
			++i;
			writer.write(it.next());
		}
		writer.write("nb atoms: " + i);
		writer.write("########## END DATA #############");
	}
	
	public static Iterable<Rule> getOntology() throws FileNotFoundException, OWL2ParserException {
		File f = new File(options.onto_file);
		Parser parser = new OWL2Parser(f);
		List<Rule> rules = new LinkedList<Rule>();
		while (parser.hasNext()) {
			Object o = parser.next();
			if(o instanceof Rule) {
				Rule r = (Rule) o;
				for(Atom a : r.getHead()) {
					if(a.getPredicate().getIdentifier().equals("owl:Thing")) {
						r.getHead().remove(a);
					}
				}
				rules.add((Rule)o);
				//System.out.println(o);
			}
		}
		parser.close();
		return rules;
	}
	
	/*
	private static void chase(Store store, Iterable<Rule> rules) throws ChaseException {
		profiler.add("size before semi chase", BenchUtils.sizeOf(store));
		profiler.start("compute grd");
		GraphOfRuleDependencies grd = new GraphOfRuleDependencies(rules);
		profiler.stop("compute grd");
		profiler.start("semi chase time");
		Chase chase = new ChaseWithGRD(grd, store);
		profiler.stop("semi chase time");
		chase.execute();
		profiler.add("size after semi chase", BenchUtils.sizeOf(store));
	}*/

}
