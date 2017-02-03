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
 package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;


/**
 * Unit test.
 */
public class TermTest 
{

	@SuppressWarnings("deprecation")
	@Test
	public void constantConstructorTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createConstant(label);
		
		Assert.assertTrue(Term.Type.CONSTANT.equals(term.getType()));
		Assert.assertTrue(term.isConstant());
		Assert.assertFalse(term.isLiteral());
		Assert.assertFalse(term.isVariable());
		Assert.assertTrue(term.getIdentifier().toString()
.equals(label));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void literalConstructorTest() {
		int value = 1;
		Literal term = DefaultTermFactory.instance().createLiteral(1);

		Assert.assertTrue(Term.Type.LITERAL.equals(term.getType()));
		Assert.assertTrue(term.isConstant());
		Assert.assertTrue(term.isLiteral());
		Assert.assertFalse(term.isVariable());
		Assert.assertTrue(term.getValue().equals(value));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void variableConstructorTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createVariable(label);

		Assert.assertTrue(Term.Type.VARIABLE.equals(term.getType()));
		Assert.assertFalse(term.isConstant());
		Assert.assertFalse(term.isLiteral());
		Assert.assertTrue(term.isVariable());
		Assert.assertTrue(term.getIdentifier().toString()
.equals(label));
	}
	
	@Test
	public void equalsTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createConstant(label);
		
		Assert.assertTrue("Term not equals itself", term.equals(term));
		
		Term other = DefaultTermFactory.instance().createConstant(label);
		Assert.assertTrue("Term not equals an other term", term.equals(other));
	}
}
