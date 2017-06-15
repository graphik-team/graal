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
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.util.ShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLEquivalentClassExpressionVisitorImpl extends
		OWLEquivalentClassExpressionVisitor {

	private static final OWLDataFactory DF = new OWLDataFactoryImpl();

	private Term glueVariable;
	private VariableGenerator varGen;
	private ShortFormProvider prefixManager;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public OWLEquivalentClassExpressionVisitorImpl(
			ShortFormProvider prefixManager, VariableGenerator varGen,
			Term glueVariable) {
		this.prefixManager = prefixManager;
		this.varGen = varGen;
		this.glueVariable = glueVariable;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLClass arg) {
		Predicate p = GraalUtils.createPredicate(arg);
		Atom a = DefaultAtomFactory.instance().create(p, glueVariable);
		return GraalUtils.createAtomSet(a);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectIntersectionOf arg) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		for (OWLClassExpression c : arg.getOperands()) {
			atomset.addAll(c.accept(this));
		}
		return atomset;
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectSomeValuesFrom arg) {
		Term newGlueVariable = varGen.getFreshSymbol();

		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(glueVariable,
						newGlueVariable));

		if (!arg.getFiller().equals(DF.getOWLThing())) {
			atomset.addAll(arg.getFiller().accept(
					new OWLEquivalentClassExpressionVisitorImpl(
							this.prefixManager, varGen, newGlueVariable)));
		}

		return atomset;
	}

	@Override
	public InMemoryAtomSet visit(OWLDataSomeValuesFrom arg) {
		Variable newGlueVariable = varGen.getFreshSymbol();

		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(glueVariable,
						newGlueVariable));

		if (!arg.getFiller().equals(DF.getTopDatatype())) {
			atomset.addAll(arg.getFiller().accept(
					new OWLEquivalentDataRangeVisitorImpl(newGlueVariable)));
		}

		return atomset;
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectHasValue arg) {
		return arg.getProperty()
				.accept(new OWLPropertyExpressionVisitorImpl(glueVariable,
						GraalUtils.createTerm(arg.getFiller())));

	}

	@Override
	public InMemoryAtomSet visit(OWLDataHasValue arg) {
		return arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(glueVariable, GraalUtils
						.createLiteral(arg.getFiller())));
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectHasSelf arg) {
		return arg.getProperty()
				.accept(new OWLPropertyExpressionVisitorImpl(glueVariable,
						glueVariable));
	}

	@Override
	public InMemoryAtomSet objectMinCardinality0(OWLObjectMinCardinality arg) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		atomset.add(new DefaultAtom(Predicate.TOP, glueVariable));
		return atomset;
	}

	@Override
	public InMemoryAtomSet dataMinCardinality0(OWLDataMinCardinality arg) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		atomset.add(DefaultAtomFactory.instance().getTop());
		return atomset;
	}

	@Override
	public InMemoryAtomSet objectMinCardinality1(OWLObjectMinCardinality arg) {
		Term newGlueVariable = varGen.getFreshSymbol();

		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(glueVariable,
						newGlueVariable));

		atomset.addAll(arg.getFiller().accept(
				new OWLEquivalentClassExpressionVisitorImpl(this.prefixManager,
						varGen, newGlueVariable)));

		return atomset;
	}

	@Override
	public InMemoryAtomSet dataMinCardinality1(OWLDataMinCardinality arg) {
		Variable newGlueVariable = varGen.getFreshSymbol();

		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(glueVariable,
						newGlueVariable));

		atomset.addAll(arg.getFiller().accept(
				new OWLEquivalentDataRangeVisitorImpl(newGlueVariable)));

		return atomset;
	}

	@Override
	public InMemoryAtomSet objectOneOf1(OWLIndividual i) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		atomset.add(DefaultAtomFactory.instance().create(Predicate.EQUALITY, glueVariable,
				GraalUtils.createTerm(i)));
		return atomset;
	}

}
