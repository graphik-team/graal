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
 package fr.lirmm.graphik.graal.core.term;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;

public class AtomTest {

    @Test
    public void constructorTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
		terms[0] = new DefaultVariable("X");
		terms[1] = new DefaultConstant("a");
		terms[2] = new DefaultConstant("b");

        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

        Assert.assertTrue(atom.getPredicate().equals(predicate));
        Assert.assertTrue(atom.getTerm(0).equals(terms[0]));
        Assert.assertTrue(atom.getTerm(1).equals(terms[1]));
        Assert.assertTrue(atom.getTerm(2).equals(terms[2]));

        Assert.assertTrue("The list eiuae ",
                atom.getTerms().equals(Arrays.asList(terms)));
    }

    @Test
    public void setterTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
		terms[0] = new DefaultVariable("X");
		terms[1] = new DefaultConstant("a");
		terms[2] = new DefaultConstant("b");
        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

		Term newTerm = new DefaultConstant("new");
        Predicate newPredicate = new Predicate("newPred", 3);

        atom.setPredicate(newPredicate);
        Assert.assertTrue(atom.getPredicate().equals(newPredicate));

        atom.setTerm(2, newTerm);
        Assert.assertTrue(atom.getTerm(2).equals(newTerm));
    }

    @Test
    public void equalsTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
		terms[0] = new DefaultVariable("X");
		terms[1] = new DefaultConstant("a");
		terms[2] = new DefaultConstant("b");
        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

        Assert.assertTrue("Atom not equals itself", atom.equals(atom));
        Assert.assertTrue("Atom not equals it clone",
                atom.equals(new DefaultAtom(atom)));

        Predicate otherPred = new Predicate("otherPred", 3);
        Term[] otherTerms = new Term[3];
		otherTerms[0] = new DefaultVariable("Y");
		otherTerms[1] = new DefaultConstant("b");
		otherTerms[2] = new DefaultConstant("b");

        Atom other = new DefaultAtom(otherPred, Arrays.asList(terms));
        Assert.assertFalse("Atom equals an other atom with other predicate",
                atom.equals(other));

        other = new DefaultAtom(predicate, Arrays.asList(otherTerms));
        Assert.assertFalse("Atom equals an other atom with other terms",
                atom.equals(other));

        other = new DefaultAtom(atom);
        other.setPredicate(otherPred);
        Assert.assertFalse("Atom equals a copy with modified predicate",
                predicate.equals(other));

        other = new DefaultAtom(atom);
        other.setTerm(2, terms[0]);
        Assert.assertFalse("Atom equals a copy with modified terms",
                atom.equals(other));

    }

}
