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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Variable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLEquivalentClassExpressionVisitorImpl extends
		OWLEquivalentClassExpressionVisitor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLEquivalentClassExpressionVisitorImpl.class);

	private static final OWLDataFactory DF = new OWLDataFactoryImpl();

	private Term glueVariable;
	private SymbolGenerator varGen;
	private ShortFormProvider prefixManager;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public OWLEquivalentClassExpressionVisitorImpl(
			ShortFormProvider prefixManager, SymbolGenerator varGen,
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
		Atom a = GraalUtils.createAtom(p, glueVariable);
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
		Term newGlueVariable = varGen.getFreeVar();

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
		Variable newGlueVariable = varGen.getFreeVar();

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
						GraalUtils.createConstant(arg.getFiller()
								.asOWLNamedIndividual())));

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
		atomset.add(Atom.TOP);
		return atomset;
	}

	@Override
	public InMemoryAtomSet objectMinCardinality1(OWLObjectMinCardinality arg) {
		Term newGlueVariable = varGen.getFreeVar();

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
		Variable newGlueVariable = varGen.getFreeVar();

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
		atomset.add(GraalUtils.createAtom(Predicate.EQUALITY, glueVariable,
				GraalUtils.createConstant(i.asOWLNamedIndividual())));
		return atomset;
	}

}
