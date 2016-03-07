/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.bench.homomorphism;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.UCQHomomorphismWithCompilation;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.bench.core.AbstractGraalBench;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRD;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.DefaultUCQHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.SqlHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.util.DefaultProfiler;
import fr.lirmm.graphik.util.stream.AbstractIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.GIterator;
import fr.lirmm.graphik.util.stream.converter.Converter;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class InfHomomorphismBench extends AbstractGraalBench {

	private int               nbUniv   = 10;

	private LinkedList<Query> queries  = new LinkedList<Query>();
	private LinkedList<Rule>  onto     = new LinkedList<Rule>();
	private RulesCompilation  rc;

	DefaultProfiler           profiler = new DefaultProfiler(new PrintStream(this.getOutputStream()));

	private String            dataDir;

	enum Mode {
		SAT, UCQ, SEMI_SAT, INF_HOMO
	}

	enum Db {
		MEM, SQL
	}

	private Mode mode = Mode.UCQ;
	private Db   db   = Db.MEM;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * A CompilationBench with a seed equals to 0.
	 * 
	 * @throws FileNotFoundException
	 */
	protected InfHomomorphismBench() throws FileNotFoundException {
		this("./src/main/resources/data");
	}

	protected InfHomomorphismBench(String dataDir) throws FileNotFoundException {
		this.dataDir = dataDir;
	}

	@Override
	public void init() {
		try {
			initOnto();
			initQueries();
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private void initQueries() throws FileNotFoundException {
		DlgpParser parser = new DlgpParser(new File(this.dataDir, "lubm-ex-queries.dlp"));
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof ConjunctiveQuery) {
				if (mode.equals(Mode.SAT)) {
					queries.add((ConjunctiveQuery) o);
				} else {
					queries.add(rewrite((ConjunctiveQuery) o));
				}
			}
		}
		parser.close();
	}

	private DefaultUnionOfConjunctiveQueries rewrite(ConjunctiveQuery q) {

		Rewriter r = new Rewriter(q, onto, rc);
		Thread thread = new Thread(r);
		thread.start();
		try {
			thread.join(3600000); // 10min
		} catch (InterruptedException e1) {

		}
		if (thread.isAlive()) {
			thread.stop();
			return null;
		} else {
			return r.getUCQ();
		}
		
	}

	private void initOnto() throws FileNotFoundException {
		DlgpParser parser = new DlgpParser(new File(this.dataDir, "lubm-ex-20.dlp"));
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule)
				onto.add((Rule) o);
		}
		parser.close();
		if (mode.equals(Mode.SAT)) {
			rc = NoCompilation.instance();
		} else {
			rc = new IDCompilation();
			rc.compile(getOntology());
		}
	}

	private void loadData(int from, int to, AtomSet data)
	    throws FileNotFoundException, OWL2ParserException, AtomSetException {
		System.out.println("load data from " + from + " to " + to);
		profiler.start("load-data");
		int i;
		File dir = new File(dataDir);
		for (int univ = from; univ < to; ++univ) {
			System.out.print(univ + " ");
			i = -1;
			while (true) {
				++i;
				System.out.print("#");
				System.out.flush();

				File f = new File(dir, "University" + univ + "_" + i + ".owl");
				if (!f.exists()) {
					break;
				}

				OWL2Parser parser = new OWL2Parser(f);
				while (parser.hasNext()) {
					Object o = parser.next();
					if (o instanceof AtomSet) {
						if (data instanceof DefaultRdbmsStore) {
							DefaultRdbmsStore store = (DefaultRdbmsStore) data;
							for (Atom a : (AtomSet) o) {
								store.addUnbatched(a);
							}
						} else {
							data.addAll((AtomSet) o);
						}
					}
				}
				if (data instanceof DefaultRdbmsStore) {
					DefaultRdbmsStore store = (DefaultRdbmsStore) data;
					store.commitAtoms();
				}

				parser.close();

			}
			System.out.println();

		}

		profiler.stop("load-data");
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	public void setNbUniv(int nbUniv) {
		this.nbUniv = nbUniv;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Map.Entry<String, Query>> getQueries() {
		return new ConverterCloseableIterator<Query, Map.Entry<String, Query>>(
		                                                                       new CloseableIteratorAdapter<Query>(
		                                                                                                           queries.iterator()),
		                                                                       new Converter<Query, Map.Entry<String, Query>>() {

			                                                                       @Override
			                                                                       public Entry<String, Query> convert(
			                                                                           Query object) {
				                                                                       if (object instanceof DefaultUnionOfConjunctiveQueries) {
					                                                                       return new ImmutablePair<String, Query>(
				                                                                                                               ((DefaultUnionOfConjunctiveQueries) object).getLabel(),
				                                                                                                               object);
				                                                                       } else if (object instanceof ConjunctiveQuery) {
					                                                                       return new ImmutablePair<String, Query>(
					                                                                                                               ((ConjunctiveQuery) object).getLabel(),
					                                                                                                               object);
				                                                                       } else {
					                                                                       return new ImmutablePair<String, Query>(
					                                                                                                               "",
					                                                                                                               object);
				                                                                       }
			                                                                       }

		                                                                       });
	}

	@Override
	public CloseableIterator<Rule> getOntology() {
		return new CloseableIteratorAdapter<Rule>(onto.iterator());
	}

	@Override
	public CloseableIterator<Map.Entry<String, AtomSet>> getInstances() {
		if (this.db.equals(Db.MEM)) {
			return new InstanceIterator(new DefaultInMemoryGraphAtomSet());
		} else {
			try {
				return new InstanceIterator(
				                            new DefaultRdbmsStore(new MysqlDriver("localhost", "bench", "root", "root")));
			} catch (AtomSetException e) {
				throw new Error("Untreated exception", e);
			}
		}
	}

	@Override
	public void run() {

		Query q = this.getQuery();
		AtomSet atomset = this.getAtomSet();
		Object o = this.getExtra();

		DefaultProfiler profiler = new DefaultProfiler();

		if (q != null) {
			BCC bcc = new BCC(new GraphBaseBackJumping(), false);

			// define homomorphism
			Homomorphism h = null;
			if (this.db.equals(Db.MEM)) {
				h = new BacktrackHomomorphism(bcc.getBCCScheduler(), StarBootstrapper.instance(), new NFC2(),
				                              bcc.getBCCBackJumping());
			} else if (this.db.equals(Db.SQL)) {
				h = SqlHomomorphism.instance();
			}
			h.setProfiler(profiler);

			try {
				DefaultUnionOfConjunctiveQueries ucq = (DefaultUnionOfConjunctiveQueries) q;
				System.out.println(ucq.getLabel());

				// run
				profiler.start("totalTime");
				CloseableIterator<Substitution> it;

				if (mode.equals(Mode.INF_HOMO)) {
					UCQHomomorphismWithCompilation<AtomSet> ucqH = new DefaultUCQHomomorphism(
					                                                                                                   h);
					it = ucqH.execute(ucq, atomset, this.rc);
				} else if (mode.equals(Mode.SEMI_SAT) || mode.equals(Mode.UCQ)) {
					UCQHomomorphismWithCompilation<AtomSet> ucqH = new DefaultUCQHomomorphism(
					                                                                                                   h);
					it = ucqH.execute(ucq, atomset);
				} else {
					it = h.execute(ucq, atomset);
				}

				int i = 0;
				while (it.hasNext()) {
					it.next();
					++i;
				}
				it.close();
				profiler.stop("totalTime");
				profiler.put("nbResults", i);

			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setResults(profiler.entrySet().iterator());
		} else {
			profiler.put("totalTime", "TO");
			profiler.put("nbResults", "TO");
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private class Rewriter implements Runnable {

		private ConjunctiveQuery query;
		private LinkedList<Rule> onto;
		private RulesCompilation rc;
		private DefaultUnionOfConjunctiveQueries ucq;

		Rewriter(ConjunctiveQuery q, LinkedList<Rule> onto, RulesCompilation rc) {
			this.query = q;
			this.onto = onto;
			this.rc = rc;
		}

		@Override
		public void run() {
			PureRewriter pure = new PureRewriter(mode.equals(Mode.UCQ));
			pure.enableVerbose(true);
			pure.setProfiler(profiler);
			GIterator<ConjunctiveQuery> it = pure.execute(this.query, this.onto, this.rc);

			DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(it);
			ucq.setLabel(this.query.getLabel());

			this.ucq = ucq;
		}

		public DefaultUnionOfConjunctiveQueries getUCQ() {
			return this.ucq;

		}
	}

	private class InstanceIterator extends AbstractIterator<Map.Entry<String, AtomSet>>
	                                                                                   implements
	                                                                                   CloseableIterator<Map.Entry<String, AtomSet>> {

		/**
         * 
         */
		public InstanceIterator(AtomSet store) {
			this.instance = store;
		}

		AtomSet instance;
		int     currentInstance = 0;

		@Override
		public boolean hasNext() {
			if (nbUniv < 0) {
				if (instance instanceof Store) {
					((Store) instance).close();
				}
				return false;
			}
			return true;
		}

		@Override
		public Entry<String, AtomSet> next() {
			++currentInstance;
			try {
				loadData(0, nbUniv, instance);
				nbUniv = 0; // no more instance
			} catch (Exception e) {
				throw new Error(e);
			}

			if (mode.equals(Mode.SEMI_SAT)) {
				NaiveChase chase = new NaiveChase(rc.getSaturation(), instance);
				chase.enableVerbose(true);
				chase.setProfiler(profiler);
				try {
					chase.next();
				} catch (ChaseException e) {
					throw new Error("Untreated exception");
				}
			} else if (mode.equals(Mode.SAT)) {
				GraphOfRuleDependencies grd = new GraphOfRuleDependencies(onto);
				ChaseWithGRD chase = new ChaseWithGRD(grd, instance);
				chase.setProfiler(profiler);
				try {
					chase.execute();
				} catch (ChaseException e) {
					throw new Error("Untreated exception");
				}
			}
			return new ImmutablePair<String, AtomSet>(Integer.toString(nbUniv), instance);

		}

		@Override
		public void close() {
		}

	}

	/**
	 * @param mode
	 */
	public void setMode(String mode) {
		profiler.put("mode", mode);
		if (mode.equals("INF"))
			this.mode = Mode.INF_HOMO;
		else if (mode.equals("SEMI"))
			this.mode = Mode.SEMI_SAT;
		else if (mode.equals("UCQ"))
			this.mode = Mode.UCQ;
		else if (mode.equals("SAT")) 
			this.mode = Mode.SAT;
	}

	/**
	 * @param db
	 */
	public void setDb(String db) {
		this.db = Db.valueOf(db);
	}

}
