/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.logic.Literal;
import fr.lirmm.graphik.graal.logic.LogicalFormula;

/**
 * @author clement
 *
 */
public class OWLPropertyExpressionVisitorImpl implements
		OWLPropertyExpressionVisitorEx<LogicalFormula> {

	private Term glueVariable1, glueVariable2;

	public OWLPropertyExpressionVisitorImpl(Term glueVariable1, Term glueVariable2) {
		this.glueVariable1 = glueVariable1;
		this.glueVariable2 = glueVariable2;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public LogicalFormula visit(OWLObjectProperty property) {
		Predicate p = this.createPredicate(property);
		Atom a = this.createAtom(p, glueVariable1, glueVariable2);
		return this.createLogicalFormula(a);
	}

	@Override
	public LogicalFormula visit(OWLObjectInverseOf property) {
		LogicalFormula f = property.getInverse().accept(this);
		f.not();
		return f;
	}

	@Override
	public LogicalFormula visit(OWLDataProperty property) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////

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
	
	private Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}
	
	private LogicalFormula createLogicalFormula(Atom a) {
		return new LogicalFormula(new Literal(a, true));
	}
}
