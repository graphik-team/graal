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
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class HomomorphismWithNegatedPartsTest {

	@Test
	public void test() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(b)."));

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, Collections.singletonList(negatedPart));
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertTrue(res.hasNext());
		res.next();
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test11() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		LinkedList<InMemoryAtomSet> parts = new LinkedList<InMemoryAtomSet>();
		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(a)."));
		parts.add(negatedPart);
		negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(b)."));
		parts.add(negatedPart);
		
		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, parts);BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertTrue(res.hasNext());
		res.next();
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test2() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b)."));
		
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(b)."));

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, Collections.singletonList(negatedPart));
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test3() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b)."));
		
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,b)."));
		LinkedList<InMemoryAtomSet> parts = new LinkedList<InMemoryAtomSet>();
		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(a)."));
		parts.add(negatedPart);
		negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(b)."));
		parts.add(negatedPart);
		
		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, parts);
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test4() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b)."));
		
		Variable x = DefaultTermFactory.instance().createVariable("X");
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,X)."));
		positivePart.iterator().next().setTerm(1, x);
		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(X)."));
		negatedPart.iterator().next().setTerm(0, x);

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, Collections.singletonList(negatedPart));
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test5() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), r(b,b), r(c,c), q(c), q(b)."));
		
		Variable x = DefaultTermFactory.instance().createVariable("X");
		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(a,X),r(W,Y),q(Y)."));
		positivePart.iterator().next().setTerm(1, x);

		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(X)."));
		negatedPart.iterator().next().setTerm(0, x);

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, Collections.singletonList(negatedPart), Collections.<Term>emptyList());
		BCC bcc = new BCC(new GraphBaseBackJumping(), true);
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts(bcc.getBCCScheduler(), StarBootstrapper.instance(), new NFC2(),
                bcc.getBCCBackJumping());
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	@Test
	public void test6() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b), p(a,c), r(a,d), q(d), r(a,e)."));
		
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Variable z = DefaultTermFactory.instance().createVariable("Z");

		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(X,Y),r(X,Z)."));
		CloseableIteratorWithoutException<Atom> it = positivePart.iterator();
		it.next().setTerm(1, y);
		it.next().setTerm(1, z);

		LinkedList<InMemoryAtomSet> parts = new LinkedList<InMemoryAtomSet>();

		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(Y)."));
		negatedPart.iterator().next().setTerm(0, y);
		parts.add(negatedPart);
		
		negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(Z)."));
		negatedPart.iterator().next().setTerm(0, z);
		parts.add(negatedPart);

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, parts);
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data);
		
		
		Assert.assertTrue(res.hasNext());
		res.next();
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	
	
	@Test
	public void test6Compilation() throws HomomorphismException, IteratorException, AtomSetException {
		InMemoryAtomSet data = new DefaultInMemoryGraphStore();
		
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("r(X,Y) :- s(X,Y)."));
		RulesCompilation comp = new IDCompilation();
		comp.compile(rules.iterator());
		
		data.addAll(DlgpParser.parseAtomSet("p(a,b), q(b), p(a,c), s(a,d), q(d), s(a,e)."));
		
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Variable z = DefaultTermFactory.instance().createVariable("Z");

		InMemoryAtomSet positivePart = new LinkedListAtomSet();
		positivePart.addAll(DlgpParser.parseAtomSet("p(X,Y),r(X,Z)."));
		CloseableIteratorWithoutException<Atom> it = positivePart.iterator();
		it.next().setTerm(1, y);
		it.next().setTerm(1, z);

		LinkedList<InMemoryAtomSet> parts = new LinkedList<InMemoryAtomSet>();

		InMemoryAtomSet negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(Y)."));
		negatedPart.iterator().next().setTerm(0, y);
		parts.add(negatedPart);
		
		negatedPart = new LinkedListAtomSet();
		negatedPart.addAll(DlgpParser.parseAtomSet("q(Z)."));
		negatedPart.iterator().next().setTerm(0, z);
		parts.add(negatedPart);

		DefaultConjunctiveQueryWithNegatedParts query = new DefaultConjunctiveQueryWithNegatedParts(positivePart, parts);
		BacktrackHomomorphismWithNegatedParts h = new BacktrackHomomorphismWithNegatedParts();
		CloseableIterator<Substitution> res = h.execute(query, data, comp);
		
		
		Assert.assertTrue(res.hasNext());
		res.next();
		Assert.assertFalse(res.hasNext());
		res.close();
	}
	


	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
