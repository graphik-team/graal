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
 /**
 * 
 */
package fr.lirmm.graphik.graal.api.forward_chaining;


import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.util.TimeoutException;
import fr.lirmm.graphik.util.profiler.NoProfiler;
import fr.lirmm.graphik.util.profiler.Profiler;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractChase<T1 extends Rule, T2 extends AtomSet> implements Chase {

	private RuleApplier<T1, ? super T2> ruleApplier;
	private Profiler    profiler = NoProfiler.instance();

	protected AbstractChase(RuleApplier<T1, ? super T2> ruleApplier) {
		this.ruleApplier = ruleApplier;
	}

	@Override
	public void execute() throws ChaseException {
		this.getProfiler().start("saturation");
		while (!Thread.currentThread().isInterrupted() && this.hasNext()) {
			this.next();
		}
		this.getProfiler().stop("saturation");
	}
	
	@Override
	public void execute(long timeout) throws ChaseException, TimeoutException {
		Executor exec = new Executor(this);
		
		Thread thread = new Thread(exec);
		thread.start();
		
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			throw new ChaseException("The chase was interrupted", e);
		}
		
		if (thread.isAlive()) {
			thread.interrupt();
			try {
				thread.join();
			} catch (InterruptedException e) {
				throw new ChaseException("The chase was interrupted", e);
			}
			throw new TimeoutException(timeout);
		} else {
			if(exec.getException() != null) {
				throw exec.getException();
			}
		}
	}

	protected RuleApplier<T1, ? super T2> getRuleApplier() {
		return this.ruleApplier;
	}

	protected void setRuleApplier(RuleApplier<T1, ? super T2> applier) {
		this.ruleApplier = applier;
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}
	
	private static final class Executor implements Runnable {
		
			private ChaseException e = null;
			private Chase chase;
			
			public Executor(AbstractChase<?,?> chase) {
				this.chase = chase;
			}
			
			@Override
			public void run() {
				try {
					this.chase.execute();
				} catch (ChaseException ex) {
					e = ex;
				}
			}
			
			ChaseException getException() {
				return this.e;
			}
		
	}
};
