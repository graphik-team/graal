/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.core.term;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;

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
