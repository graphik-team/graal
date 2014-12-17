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
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.io.owl.logic.Literal;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class OWLClassExpressionVisitorImpl implements
		OWLClassExpressionVisitorEx<LogicalFormula> {

	private Term glueVariable;
	private SymbolGenerator varGen;

	public OWLClassExpressionVisitorImpl(SymbolGenerator varGen, Term glueVariable) {
		this.glueVariable = glueVariable;
		this.varGen = varGen;
	}



	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public LogicalFormula visit(OWLClass arg0) {
		Predicate p = this.createPredicate(arg0);
		Atom a = this.createAtom(p, glueVariable);
		return this.createLogicalFormula(a);
	}

	@Override
	public LogicalFormula visit(OWLObjectIntersectionOf arg) {
		LogicalFormula f = this.createLogicalFormula();
		for (OWLClassExpression c : arg.getOperands()) {
			try {
				f.and(c.accept(this));
			} catch (Exception e) {
			}
		}

		return f;
	}

	@Override
	public LogicalFormula visit(OWLObjectUnionOf arg) {
		LogicalFormula f = this.createLogicalFormula();

		for (OWLClassExpression c : arg.getOperands()) {
			try {
				f.or(c.accept(this));
			} catch (Exception e) {
			}
		}

		return f;
	}

	@Override
	public LogicalFormula visit(OWLObjectComplementOf arg) {
		LogicalFormula f = arg.getOperand().accept(this);
		f.not();

		return f;
	}

	@Override
	public LogicalFormula visit(OWLObjectSomeValuesFrom arg) {
		Term newGlueVariable = varGen.getFreeVar();
		
		LogicalFormula f = arg.getProperty().accept(new OWLPropertyExpressionVisitorImpl(
				glueVariable, newGlueVariable));
		
		f.and(arg.getFiller().accept(
				new OWLClassExpressionVisitorImpl(varGen, newGlueVariable)));
		return f;

	}

	@Override
	public LogicalFormula visit(OWLObjectAllValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLObjectHasValue arg) {
		LogicalFormula f = arg.getProperty().accept(new OWLPropertyExpressionVisitorImpl(
				glueVariable, createTerm(arg.getFiller())));
		return f;
	}

	@Override
	public LogicalFormula visit(OWLObjectMinCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLObjectExactCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLObjectMaxCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLObjectHasSelf arg) {
		LogicalFormula f = arg.getProperty().accept(new OWLPropertyExpressionVisitorImpl(
				glueVariable, glueVariable));
		return f;
	}

	@Override
	public LogicalFormula visit(OWLObjectOneOf arg) {
		LogicalFormula atomset = this.createLogicalFormula();
		for (OWLIndividual i : arg.getIndividuals()) {
			// atomset.add(atom)
		}
		return atomset;
	}

	@Override
	public LogicalFormula visit(OWLDataSomeValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLDataAllValuesFrom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLDataHasValue arg) {
		LogicalFormula f = arg.getProperty().accept(new OWLPropertyExpressionVisitorImpl(
				glueVariable, new Term(arg.getFiller().toString(), Term.Type.LITERAL)));
		return f;
	}

	@Override
	public LogicalFormula visit(OWLDataMinCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLDataExactCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public LogicalFormula visit(OWLDataMaxCardinality arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	private LogicalFormula createLogicalFormula() {
		return new LogicalFormula();
	}
	
	private LogicalFormula createLogicalFormula(Atom a) {
		return new LogicalFormula(new Literal(a, true));
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
	 * @param value
	 * @return
	 */
	private Term createTerm(OWLIndividual value) {
		return new Term(value.toString(), Term.Type.CONSTANT);
	}

}
