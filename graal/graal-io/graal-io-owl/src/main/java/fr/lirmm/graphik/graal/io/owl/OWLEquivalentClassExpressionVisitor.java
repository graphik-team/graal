/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
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
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
abstract class OWLEquivalentClassExpressionVisitor implements
		OWLClassExpressionVisitorEx<InMemoryAtomSet> {

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLObjectMinCardinality arg) {
		if (arg.getCardinality() == 0) {
			return this.objectMinCardinality0(arg);
		} else if (arg.getCardinality() == 1) {
			return this.objectMinCardinality1(arg);
		}
		throw new UnsupportedConstructor(arg);
	}
	
	@Override
	public InMemoryAtomSet visit(OWLDataMinCardinality arg) {
		if (arg.getCardinality() == 0) {
			return this.dataMinCardinality0(arg);
		} else if (arg.getCardinality() == 1) {
			return this.dataMinCardinality1(arg);
		}
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectOneOf arg) {
		Set<OWLIndividual> individuals = arg.getIndividuals();
		if (individuals.size() == 1) {
			return this.objectOneOf1(individuals.iterator().next());
		}
		throw new UnsupportedConstructor(arg);
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public abstract InMemoryAtomSet visit(OWLClass arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectIntersectionOf arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectSomeValuesFrom arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLDataSomeValuesFrom arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectHasValue arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLDataHasValue arg);


	@Override
	public abstract InMemoryAtomSet visit(OWLObjectHasSelf arg);

	public abstract InMemoryAtomSet objectMinCardinality0(
			OWLObjectMinCardinality arg);

	public abstract InMemoryAtomSet dataMinCardinality0(
			OWLDataMinCardinality arg);

	public abstract InMemoryAtomSet objectMinCardinality1(
			OWLObjectMinCardinality arg);
	
	public abstract InMemoryAtomSet dataMinCardinality1(
			OWLDataMinCardinality arg);

	public abstract InMemoryAtomSet objectOneOf1(OWLIndividual owlIndividual);

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLObjectUnionOf arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectComplementOf arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectAllValuesFrom arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataAllValuesFrom arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectExactCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}
	
	@Override
	public InMemoryAtomSet visit(OWLDataExactCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectMaxCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataMaxCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

}
