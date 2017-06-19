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


import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.SimpleFC;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.profiler.CPUTimeProfiler;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ForwardCheckingTest {

	@Test
	public void test1() throws HomomorphismException, IteratorException, ParseException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();

		data.addAll(DlgpParser.parseAtomSet("p(a,b)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X,Y), p(X,Z).");

		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new NFC2());
		CloseableIterator<Substitution> results = h.execute(query, data);
		while (results.hasNext()) {
			results.next();
		}
		results.close();

	}

	@Test
	public void simpleFCTest1() throws HomomorphismException, IteratorException, ParseException, AtomSetException {
		Profiler profiler = new CPUTimeProfiler();

		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b,c)."));
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X,Y), q(Y,Z).");

		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new SimpleFC());
		h.setProfiler(profiler);

		CloseableIterator<Substitution> results = h.execute(query, data);
		while (results.hasNext()) {
			results.next();
		}
		results.close();
		Assert.assertEquals(7, profiler.get("#calls"));
	}

	@Test
	public void FCTest2() throws HomomorphismException, IteratorException, ParseException, AtomSetException {
		Profiler profiler = new CPUTimeProfiler();

		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), p(a,c), q(a,a), q(a,b)."));
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X,Z), q(Y,Z).");

		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new NFC2());
		h.setProfiler(profiler);

		CloseableIterator<Substitution> results = h.execute(query, data);
		while (results.hasNext()) {
			results.next();
		}
		results.close();
		Assert.assertEquals(7, profiler.get("#calls"));
	}

	@Test
	public void NFC2Test() throws HomomorphismException, IteratorException, ParseException {
		Profiler profiler = new CPUTimeProfiler();

		Predicate[] predicates = { new Predicate("p2", 2), new Predicate("p3", 3), new Predicate("p4", 4)};
		
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		TestUtil.addNAtoms(data, 13, predicates, 5, new Random(0));
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X5,X6,X7,X8) :- p4(X5,X6,X7,X8), p4(X8,X7,X6,X5), p3(X7,X8,X9), p2(X7,X11).");

		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new NFC2());
		h.setProfiler(profiler);

		CloseableIterator<Substitution> results = h.execute(query, data);
		while (results.hasNext()) {
			results.next();
		}
		results.close();
		Assert.assertEquals(1, profiler.get("#calls"));
	}

	@Test
	public void NFC2Test2() throws HomomorphismException, IteratorException, ParseException {
		Profiler profiler = new CPUTimeProfiler();

		Predicate[] predicates = { new Predicate("p", 2), new Predicate("q", 2), new Predicate("r", 2) };

		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		TestUtil.addNAtoms(data, 32, predicates, 5, new Random(0));
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X,Y), q(X,Z), r(Y,Z).");

		Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new NFC2());
		h.setProfiler(profiler);

		CloseableIterator<Substitution> results = h.execute(query, data);
		while (results.hasNext()) {
			results.next();
		}
		results.close();
	}


}
