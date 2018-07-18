/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.scheduler.DefaultScheduler;
import fr.lirmm.graphik.graal.homomorphism.scheduler.Scheduler;
import fr.lirmm.graphik.graal.homomorphism.utils.EqualityHandlerConverter;
import fr.lirmm.graphik.graal.homomorphism.utils.EqualityUtils;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * This Backtrack is inspired by the Baget Jean-François Thesis (Chapter 5)
 *
 * see also "Backtracking Through Biconnected Components of a Constraint Graph"
 * (Jean-François Baget, Yannic S. Tognetti IJCAI 2001)
 *
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BacktrackHomomorphism extends AbstractHomomorphismWithCompilation<ConjunctiveQuery, AtomSet>
                                   implements
                                                             HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>,
                                                             Profilable {

	private Scheduler       scheduler;
	private Bootstrapper    bootstrapper;
	private ForwardChecking fc;
	private BackJumping     bj;

	/**
	 * Constructs an instance with {@link BCC}, {@link StarBootstrapper},
	 * {@link NFC2} and {@link GraphBaseBackJumping}.
	 */
	public BacktrackHomomorphism() {
		this(true);
	}

	/**
	 * Constructs an instance with {@link NFC2}, {@link GraphBaseBackJumping}
	 * and a specified {@link Bootstrapper}.
	 * 
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 */
	public BacktrackHomomorphism(Bootstrapper bs) {
		this(true, bs, new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper},
	 * {@link GraphBaseBackJumping} and a specified {@link ForwardChecking}.
	 * 
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 */
	public BacktrackHomomorphism(ForwardChecking fc) {
		this(true, StarBootstrapper.instance(), fc, new GraphBaseBackJumping());
	}
	
	/**
	 * Constructs an instance with a specified {@link Bootstrapper},
	 * {@link ForwardChecking} and {@link BackJumping}.
	 * 
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 * @param bj
	 *            the {@link BackJumping back-jumping} to be used.
	 */
	public BacktrackHomomorphism(Bootstrapper bs, ForwardChecking fc,
	    BackJumping bj) {
		this(true, bs, fc, bj);
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper}, {@link NFC2} and
	 * {@link GraphBaseBackJumping}.
	 * 
	 * @param enableBCC
	 *            enable or disable {@link BCC}
	 */
	public BacktrackHomomorphism(boolean enableBCC) {
		this(enableBCC, StarBootstrapper.instance(), new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link NFC2}, {@link GraphBaseBackJumping}
	 * and a specified {@link Bootstrapper}.
	 * 
	 * @param enableBCC
	 *            enable or disable {@link BCC}
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 */
	public BacktrackHomomorphism(boolean enableBCC, Bootstrapper bs) {
		this(enableBCC, bs, new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper},
	 * {@link GraphBaseBackJumping} and a specified {@link ForwardChecking}.
	 * 
	 * @param enableBCC
	 *            enable or disable {@link BCC}
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 */
	public BacktrackHomomorphism(boolean enableBCC, ForwardChecking fc) {
		this(enableBCC, StarBootstrapper.instance(), fc, new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper}, {@link NFC2} and a
	 * specified {@link BackJumping}.
	 * 
	 * @param enableBCC
	 *            enable or disable {@link BCC}
	 * @param bj
	 *            the {@link BackJumping back-jumping} to be used.
	 */
	public BacktrackHomomorphism(boolean enableBCC, BackJumping bj) {
		this(enableBCC, StarBootstrapper.instance(), new NFC2(), bj);
	}

	/**
	 * Constructs an instance with a specified {@link Bootstrapper},
	 * {@link ForwardChecking} and {@link BackJumping}.
	 * 
	 * @param enableBCC
	 *            enable or disable {@link BCC}
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 * @param bj
	 *            the {@link BackJumping back-jumping} to be used.
	 */
	public BacktrackHomomorphism(boolean enableBCC, Bootstrapper bs, ForwardChecking fc,
	    BackJumping bj) {
		super();
		if (enableBCC) {
			BCC bcc = new BCC(bj, true);
			this.scheduler = bcc.getBCCScheduler();
			this.bj = bcc.getBCCBackJumping();
		} else {
			this.scheduler = DefaultScheduler.instance();
			this.bj = bj;
		}
		this.bootstrapper = bs;
		this.fc = fc;
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper}, {@link NFC2},
	 * {@link GraphBaseBackJumping} and a specified {@link BCC} instance.
	 * 
	 * @param BCC
	 *            the {@link BCC} to be used.
	 */
	public BacktrackHomomorphism(BCC bcc) {
		this(bcc, StarBootstrapper.instance(), new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link NFC2}, {@link GraphBaseBackJumping}
	 * and a specified {@link BCC} and {@link Bootstrapper}.
	 * 
	 * @param BCC
	 *            the {@link BCC} to be used.
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 */
	public BacktrackHomomorphism(BCC bcc, Bootstrapper bs) {
		this(bcc, bs, new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper},
	 * {@link GraphBaseBackJumping} and a specified {@link BCC} and
	 * {@link ForwardChecking}.
	 * 
	 * @param BCC
	 *            the {@link BCC} to be used.
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 */
	public BacktrackHomomorphism(BCC bcc, ForwardChecking fc) {
		this(bcc, StarBootstrapper.instance(), fc, new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with {@link StarBootstrapper}, {@link NFC2} and a
	 * specified {@link BCC} and {@link BackJumping}.
	 * 
	 * @param BCC
	 *            the {@link BCC} to be used.
	 * @param bj
	 *            the {@link BackJumping back-jumping} to be used.
	 */
	public BacktrackHomomorphism(BCC bcc, BackJumping bj) {
		this(bcc, StarBootstrapper.instance(), new NFC2(), bj);
	}

	/**
	 * Constructs an instance with a specified {@link BCC}, {@link Bootstrapper},
	 * {@link ForwardChecking} and {@link BackJumping}.
	 * 
	 * @param BCC
	 *            the {@link BCC} to be used.
	 * @param bs
	 *            the {@link Bootstrapper} bootstrapper to be used.
	 * @param fc
	 *            the {@link ForwardChecking forward-checking} to be used.
	 * @param bj
	 *            the {@link BackJumping back-jumping} to be used.
	 */
	public BacktrackHomomorphism(BCC bcc, Bootstrapper bs, ForwardChecking fc, BackJumping bj) {
		super();
		this.scheduler = bcc.getBCCScheduler();
		this.bj = bcc.getBCCBackJumping();
		this.bootstrapper = bs;
		this.fc = fc;
	}

	/**
	 * Constructs an instance with a specified {@link Scheduler scheduler}. Note
	 * that specifying the scheduler disables {@link BCC} optimizations.
	 * 
	 * @param s
	 *            the scheduler.
	 */
	public BacktrackHomomorphism(Scheduler s) {
		this(s, StarBootstrapper.instance(), new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with a specified {@link Scheduler scheduler} and
	 * {@link Boostrapper bootstrapper}. Note that specifying the scheduler
	 * disables {@link BCC} optimizations.
	 * 
	 * @param s
	 *            the scheduler.
	 */
	public BacktrackHomomorphism(Scheduler s, Bootstrapper bs) {
		this(s, bs, new NFC2(), new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with a specified {@link Scheduler scheduler} and
	 * {@link ForwardChecking forward-checking}. Note that specifying the
	 * scheduler disables {@link BCC} optimizations.
	 * 
	 * @param s
	 *            the scheduler.
	 */
	public BacktrackHomomorphism(Scheduler s, ForwardChecking fc) {
		this(s, StarBootstrapper.instance(), fc, new GraphBaseBackJumping());
	}

	/**
	 * Constructs an instance with a specified {@link Scheduler scheduler} and
	 * {@link BackJumping back-jumping}. Note that specifying the scheduler
	 * disables {@link BCC} optimizations.
	 * 
	 * @param s
	 *            the scheduler.
	 */
	public BacktrackHomomorphism(Scheduler s, BackJumping bj) {
		this(s, StarBootstrapper.instance(), new NFC2(), bj);
	}

	/**
	 * Constructs an instance with a specified {@link Scheduler scheduler},
	 * {@link Boostrapper bootstrapper}, {@link ForwardChecking
	 * forward-checking} and {@link BackJumping back-jumping}. Note that
	 * specifying the scheduler disables {@link BCC} optimizations.
	 * 
	 * @param s
	 *            the scheduler.
	 */
	public BacktrackHomomorphism(Scheduler s, Bootstrapper bs, ForwardChecking fc, BackJumping bj) {
		super();
		this.fc = fc;
		this.bj = bj;
		this.scheduler = s;
		this.bootstrapper = bs;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Substitution> execute(ConjunctiveQuery q, AtomSet a,
	    RulesCompilation compilation, Substitution s) throws HomomorphismException {
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		CloseableIterator<Substitution> results = new BacktrackIterator(pair.getLeft().getAtomSet(), Collections.<InMemoryAtomSet>emptyList(), a, pair.getLeft().getAnswerVariables(),
		                                                            this.scheduler, this.bootstrapper, this.fc,
		                                                            this.bj, compilation, s, this.getProfiler());
		
		if(!pair.getRight().getTerms().isEmpty()) {
			results =  new ConverterCloseableIterator<Substitution, Substitution>(results, new EqualityHandlerConverter(pair.getRight()));
		}
		return results;
	}

}
