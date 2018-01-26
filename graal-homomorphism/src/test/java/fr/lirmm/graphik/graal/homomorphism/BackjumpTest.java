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

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.AllDomainBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoForwardChecking;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.profiler.CPUTimeProfiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */

public class BackjumpTest {

	/*
	 * X1 --(p15)--> X5 --(p56)--> X6 \ (p12)--> X2 --(p24)--> X4 \ (p23)--> X3
	 * 
	 * solutions: X1 X2 X3 X4 X5 X6 
	 *            b  b  c  d  a  a 
	 *            b  b  d  d  a  a 
	 *            b  d  a  a  a  a
	 */
	@Test
	public void test1() throws HomomorphismException, IteratorException, ParseException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		
		data.addAll(DlgpParser.parseAtomSet("p12(a,a), p12(b,b), p12(b,c), p12(b,d)."));
		data.addAll(DlgpParser.parseAtomSet("p23(a,a), p23(b,c), p23(b,d), p23(c,a), p23(d,a)."));
		data.addAll(DlgpParser.parseAtomSet("p24(a,a), p24(b,d), p24(d,a)."));
		data.addAll(DlgpParser.parseAtomSet("p15(b,a), p15(b,b)."));
		data.addAll(DlgpParser.parseAtomSet("p56(a,a)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X1,X2,X3,X4,X5,X6) :- p12(X1,X2), p23(X2,X3), p24(X2,X4), p15(X1,X5), p56(X5,X6).");

		BCC bcc = new BCC();
		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(bcc.getBCCScheduler(), AllDomainBootstrapper.instance(),
		                                           NoForwardChecking.instance(), bcc.getBCCBackJumping());
		h.setProfiler(new CPUTimeProfiler());
		CloseableIterator<Substitution> results = h.execute(query, data);
		int i = 0;
		while (results.hasNext()) {
			results.next();
			++i;
		}
		Assert.assertEquals(3, i);
		Assert.assertEquals(49, h.getProfiler().get("#calls"));
	}
}
