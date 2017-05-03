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
package fr.lirmm.graphik.graal.forward_chaining.halting_condition;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FrontierRestrictedChaseHaltingConditionTest {

	@Test
	public void test() throws IteratorException, HomomorphismFactoryException, HomomorphismException {
		InMemoryAtomSet atomset = DefaultAtomSetFactory.instance().create(DlgpParser.parseAtom("p(a,b)."));
		Rule rule = DlgpParser.parseRule("p(X,Z):-p(X,Y).");
		
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Constant a = DefaultTermFactory.instance().createConstant("a");
		Constant b = DefaultTermFactory.instance().createConstant("b");

		Substitution s = DefaultSubstitutionFactory.instance().createSubstitution();
		s.put(x,a);
		s.put(y,b);
		
		FrontierRestrictedChaseHaltingCondition condition = new FrontierRestrictedChaseHaltingCondition();
		CloseableIterator<Atom> toAdd = condition.apply(rule, s, atomset);
		
		Assert.assertTrue(toAdd.hasNext());
		Atom atom1 = toAdd.next();
		atomset.add(atom1);
		Assert.assertFalse(toAdd.hasNext());
		toAdd.close();
		
		s = DefaultSubstitutionFactory.instance().createSubstitution();
		s.put(x,a);
		s.put(y,atom1.getTerm(1));
		
		toAdd = condition.apply(rule, s, atomset);
		
		Assert.assertFalse(toAdd.hasNext());
		toAdd.close();
	}

	@Test
	public void test2() throws IteratorException, HomomorphismFactoryException, HomomorphismException {
		InMemoryAtomSet atomset = DefaultAtomSetFactory.instance().create(DlgpParser.parseAtomSet("p(a,b), p(b,a)."));
		Rule rule = DlgpParser.parseRule("p(X,Z):-p(X,Y).");
		
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Constant a = DefaultTermFactory.instance().createConstant("a");
		Constant b = DefaultTermFactory.instance().createConstant("b");

		Substitution s = DefaultSubstitutionFactory.instance().createSubstitution();
		s.put(x,a);
		s.put(y,b);
		
		FrontierRestrictedChaseHaltingCondition condition = new FrontierRestrictedChaseHaltingCondition();
		CloseableIterator<Atom> toAdd = condition.apply(rule, s, atomset);
		
		Assert.assertTrue(toAdd.hasNext());
		Atom atom1 = toAdd.next();
		atomset.add(atom1);
		Assert.assertFalse(toAdd.hasNext());
		toAdd.close();
		
		s = DefaultSubstitutionFactory.instance().createSubstitution();
		s.put(x,b);
		s.put(y,a);
		
		toAdd = condition.apply(rule, s, atomset);
		
		Assert.assertTrue(toAdd.hasNext());
		Atom atom2 = toAdd.next();
		atomset.add(atom2);
		Assert.assertFalse(toAdd.hasNext());
		toAdd.close();
		
		Assert.assertNotEquals(atom1.getTerm(1), atom2.getTerm(1));

	}
	

}
