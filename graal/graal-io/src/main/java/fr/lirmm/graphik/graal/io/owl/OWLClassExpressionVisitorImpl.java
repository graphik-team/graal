/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class OWLClassExpressionVisitorImpl implements
		OWLClassExpressionVisitorEx<AtomSet> {

	private Term glueVariable;

	/**
		 * 
		 */
	public OWLClassExpressionVisitorImpl(Term glueVariable) {
		this.glueVariable = glueVariable;
	}

	private AtomSet createAtomSet() {
		return new LinkedListAtomSet();
	}

	/**
	 * 
	 * @param owlClass
	 * @return
	 */
	private Predicate createPredicate(OWLClassExpression owlClass) {
		Predicate predicate = null;
		if (!owlClass.isAnonymous()) {
			predicate = new Predicate(
					owlClass.asOWLClass().getIRI().toString(), 1);
		} else {
			System.out.println("###" + owlClass);
			// this.tmpManageOWLClass(owlClass);
		}
		return predicate;
	}

	private Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}

	/**
	 * @param property
	 * @return
	 */
	private Predicate createPredicate(OWLObjectPropertyExpression property) {
		Predicate predicate = null;
		if (!property.isAnonymous()) {
			predicate = new Predicate(property.asOWLObjectProperty().getIRI()
					.toString(), 2);
		} else {
			throw new Error("not yet implemented");
		}
		return predicate;
	}
	
	/**
	 * @param property
	 * @return
	 */
	private Predicate createPredicate(OWLDataPropertyExpression property) {
		Predicate predicate = null;
		if (!property.isAnonymous()) {
			predicate = new Predicate(property.asOWLDataProperty().getIRI()
					.toString(), 2);
		} else {
			throw new Error("not yet implemented");
		}
		return predicate;
	}
	
	/**
	 * @param value
	 * @return
	 */
	private Term createTerm(OWLIndividual value) {
		return new Term(value.toString(), Term.Type.CONSTANT);
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public AtomSet visit(OWLClass arg0) {
		AtomSet atomset = this.createAtomSet();
		Predicate p = this.createPredicate(arg0);
		atomset.add(this.createAtom(p, glueVariable));
		return atomset;
	}

	@Override
	public AtomSet visit(OWLObjectIntersectionOf arg0) {
		AtomSet atomset = this.createAtomSet();
		for (OWLClassExpression c : arg0.getOperands()) {
			try {
				atomset.addAll(c.accept(this));
			} catch (Exception e) {
			}
		}
		return atomset;
	}

	@Override
	public AtomSet visit(OWLObjectUnionOf arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectComplementOf arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectSomeValuesFrom arg) {
		Term newGlueVariable = DefaultFreeVarGen.genFreeVar();
		Predicate predicate = this.createPredicate(arg.getProperty());
		Atom a = this.createAtom(predicate, glueVariable, newGlueVariable);

		AtomSet atomset = arg.getFiller().accept(
				new OWLClassExpressionVisitorImpl(newGlueVariable));
		atomset.add(a);
		return atomset;

	}

	@Override
	public AtomSet visit(OWLObjectAllValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectHasValue arg) {
		AtomSet atomset = createAtomSet();
		Predicate property = createPredicate(arg.getProperty());
		atomset.add(createAtom(property, glueVariable, createTerm(arg.getValue())));
		return atomset;
	}

	@Override
	public AtomSet visit(OWLObjectMinCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectExactCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectMaxCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLObjectHasSelf arg) {
		AtomSet atomset = createAtomSet();
		Predicate property = createPredicate(arg.getProperty());
		atomset.add(createAtom(property, glueVariable, glueVariable));
		return atomset;
	}

	@Override
	public AtomSet visit(OWLObjectOneOf arg) {
		AtomSet atomset = this.createAtomSet();
		for (OWLIndividual i : arg.getIndividuals()) {
			// atomset.add(atom)
		}
		return atomset;
	}

	@Override
	public AtomSet visit(OWLDataSomeValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLDataAllValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLDataHasValue arg) {
		AtomSet atomset = createAtomSet();
		Predicate property = createPredicate(arg.getProperty());
		atomset.add(createAtom(property, glueVariable, new Term(arg.getValue().toString(), Term.Type.LITERAL)));
		return atomset;
	}

	@Override
	public AtomSet visit(OWLDataMinCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLDataExactCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet visit(OWLDataMaxCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
