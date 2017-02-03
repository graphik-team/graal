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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import uk.ac.manchester.cs.owl.owlapi.OWLDataAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataComplementOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataOneOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataUnionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectComplementOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectOneOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectUnionOfImpl;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class OWLAPIUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private OWLAPIUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Split an {@link OWLObjectOneOf} or an {@link OWLObjectUnionOf} then return operands of this union.
	 * @param c
	 * @return a Set of the operands if the specified {@link OWLClassExpression} is one of {@link OWLObjectOneOf} or {@link OWLObjectUnionOf}, 
	 * a singleton which encapsulates the specified {@link OWLClassExpression} otherwise.
	 */
	public static Set<OWLClassExpression> getObjectUnionOperands(
			OWLClassExpression c) {
		if (c instanceof OWLObjectOneOf
				&& ((OWLObjectOneOf) c).getIndividuals().size() > 1) {
			return ((OWLObjectUnionOf) ((OWLObjectOneOf) c).asObjectUnionOf())
					.getOperands();
		} else if (c instanceof OWLObjectUnionOf) {
			return ((OWLObjectUnionOf) c).getOperands();
		}

		return Collections.<OWLClassExpression> singleton(c);
	}

	public static Iterable<OWLClassExpression> getObjectIntersectionOperands(
			OWLClassExpression c) {
		if (c instanceof OWLObjectIntersectionOf) {
			return ((OWLObjectIntersectionOf) c).getOperands();
		}
		return Collections.<OWLClassExpression> singleton(c);
	}

	public static Iterable<OWLDataRange> getDataUnionOperands(
			OWLDataRange c) {
		if (c instanceof OWLDataOneOf
				&& ((OWLDataOneOf) c).getValues().size() > 1) {
			Set<OWLDataRange> union = new TreeSet<>();
			for (OWLLiteral l : ((OWLDataOneOf) c).getValues()) {
				union.add(new OWLDataOneOfImpl(Collections.singleton(l)));
			}
			return union;
		} else if (c instanceof OWLDataUnionOf) {
			return ((OWLDataUnionOf) c).getOperands();
		}

		return Collections.<OWLDataRange> singleton(c);
	}

	public static Iterable<OWLDataRange> getDataIntersectionOperands(
			OWLDataRange c) {
		if (c instanceof OWLObjectIntersectionOf) {
			return ((OWLDataIntersectionOf) c).getOperands();
		}

		return Collections.<OWLDataRange> singleton(c);
	}

	/**
	 * @param superClass
	 * @return true if the {@link OWLClassExpression} represents an logical intersection, false otherwise.
	 */
	public static boolean isIntersection(OWLClassExpression superClass) {
		return superClass.asConjunctSet().size() > 1;
	}

	/**
	 * disjunctive normal form
	 * 
	 * @param classExpression
	 * @return a {@link OWLClassExpression} in disjunctive normal form equivalents to the specified one 
	 */
	public static OWLClassExpression classExpressionDisjunctiveNormalForm(
			OWLClassExpression classExpression) {

		if (classExpression instanceof OWLObjectUnionOf) {
			Set<OWLClassExpression> union = new TreeSet<>();
			for (OWLClassExpression element : OWLAPIUtils
					.getObjectUnionOperands(classExpression)) {
				element = classExpressionDisjunctiveNormalForm(element);
				for (OWLClassExpression e : OWLAPIUtils
						.getObjectUnionOperands(element)) {
					union.add(e);
				}
			}
			return new OWLObjectUnionOfImpl(union);
		} else if (classExpression instanceof OWLObjectIntersectionOf) {
			List<Set<OWLClassExpression>> conjunctions = new LinkedList<>();
			conjunctions.add(new TreeSet<OWLClassExpression>());

			for (OWLClassExpression element : OWLAPIUtils
					.getObjectIntersectionOperands(classExpression)) {
				element = classExpressionDisjunctiveNormalForm(element);
				if (element instanceof OWLObjectUnionOf) {
					List<Set<OWLClassExpression>> tmp = new LinkedList<>();

					for (Set<OWLClassExpression> conj : conjunctions) {
						for (OWLClassExpression e : OWLAPIUtils
								.getObjectUnionOperands(element)) {
							Set<OWLClassExpression> newConj = new TreeSet<>(
									conj);
							newConj.add(classExpressionDisjunctiveNormalForm(e));
							tmp.add(newConj);
						}
					}
					conjunctions = tmp;
				} else {
					for (Set<OWLClassExpression> conj : conjunctions) {
						for (OWLClassExpression e : OWLAPIUtils
								.getObjectIntersectionOperands(element)) {
							conj.add(e);
						}
					}
				}
			}
			Set<OWLClassExpression> union = new TreeSet<>();
			if (conjunctions.size() > 1) {
				for (Set<OWLClassExpression> conj : conjunctions) {
					union.add(new OWLObjectIntersectionOfImpl(conj));
				}
				return new OWLObjectUnionOfImpl(union);
			} else {
				return new OWLObjectIntersectionOfImpl(conjunctions.get(0));
			}
		} else if (classExpression instanceof OWLObjectSomeValuesFrom) {
			OWLObjectSomeValuesFrom expr = (OWLObjectSomeValuesFrom) classExpression;
			OWLObjectPropertyExpression prop = expr.getProperty();
			OWLClassExpression filler = classExpressionDisjunctiveNormalForm(expr
					.getFiller());

			if (filler instanceof OWLObjectUnionOf) {
				Set<OWLClassExpression> union = new TreeSet<>();
				for (OWLClassExpression e : OWLAPIUtils
						.getObjectUnionOperands(filler)) {
					e = classExpressionDisjunctiveNormalForm(e);
					union.add(new OWLObjectSomeValuesFromImpl(prop, e));
				}
				return new OWLObjectUnionOfImpl(union);
			}

			return new OWLObjectSomeValuesFromImpl(prop, filler);
		} else if (classExpression instanceof OWLDataSomeValuesFrom) {
			OWLDataSomeValuesFrom expr = (OWLDataSomeValuesFrom) classExpression;
			OWLDataPropertyExpression prop = expr.getProperty();
			OWLDataRange filler = dataRangeDisjunctiveNormalForm(expr
					.getFiller());

			if (filler instanceof OWLDataUnionOf) {
				Set<OWLClassExpression> union = new TreeSet<>();
				for (OWLDataRange e : OWLAPIUtils
						.getDataUnionOperands(filler)) {
					e = dataRangeDisjunctiveNormalForm(e);
					union.add(new OWLDataSomeValuesFromImpl(prop, e));
				}
				return new OWLObjectUnionOfImpl(union);
			}

			return new OWLDataSomeValuesFromImpl(prop, filler);
		} else if (classExpression instanceof OWLObjectOneOf) {
			OWLObjectOneOf expr = (OWLObjectOneOf) classExpression;
			if (expr.getIndividuals().size() <= 1) {
				return expr;
			}
			Set<OWLClassExpression> union = new TreeSet<>();
			for (OWLIndividual i : expr.getIndividuals()) {
				Set<OWLIndividual> individuals = Collections.singleton(i);
				union.add(new OWLObjectOneOfImpl(individuals));
			}
			return new OWLObjectUnionOfImpl(union);
		} else if (classExpression instanceof OWLObjectAllValuesFrom) {
			OWLObjectAllValuesFrom expr = (OWLObjectAllValuesFrom) classExpression;
			return new OWLObjectAllValuesFromImpl(expr.getProperty(),
					classExpressionDisjunctiveNormalForm(expr.getFiller()));
		} else if (classExpression instanceof OWLDataAllValuesFrom) {
			OWLDataAllValuesFrom expr = (OWLDataAllValuesFrom) classExpression;
			return new OWLDataAllValuesFromImpl(expr.getProperty(),
					dataRangeDisjunctiveNormalForm(expr.getFiller()));
		} else if (classExpression instanceof OWLObjectComplementOf) {
			OWLObjectComplementOf expr = (OWLObjectComplementOf) classExpression;
			return new OWLObjectComplementOfImpl(
					classExpressionDisjunctiveNormalForm(expr.getOperand()));
		} else if (classExpression instanceof OWLObjectMinCardinality) {
			OWLObjectMinCardinality c = (OWLObjectMinCardinality) classExpression;
			return new OWLObjectMinCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					classExpressionDisjunctiveNormalForm(c.getFiller()));
		} else if (classExpression instanceof OWLDataMinCardinality) {
			OWLDataMinCardinality c = (OWLDataMinCardinality) classExpression;
			return new OWLDataMinCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					dataRangeDisjunctiveNormalForm(c.getFiller()));
		} else if (classExpression instanceof OWLObjectMaxCardinality) {
			OWLObjectMaxCardinality c = (OWLObjectMaxCardinality) classExpression;
			return new OWLObjectMaxCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					classExpressionDisjunctiveNormalForm(c.getFiller()));
		} else if (classExpression instanceof OWLDataMaxCardinality) {
			OWLDataMaxCardinality c = (OWLDataMaxCardinality) classExpression;
			return new OWLDataMaxCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					dataRangeDisjunctiveNormalForm(c.getFiller()));
		} else if (classExpression instanceof OWLObjectExactCardinality) {
			OWLObjectExactCardinality c = (OWLObjectExactCardinality) classExpression;
			return new OWLObjectExactCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					classExpressionDisjunctiveNormalForm(c.getFiller()));
		} else if (classExpression instanceof OWLDataExactCardinality) {
			OWLDataExactCardinality c = (OWLDataExactCardinality) classExpression;
			return new OWLDataExactCardinalityImpl(c.getProperty(),
					c.getCardinality(),
					dataRangeDisjunctiveNormalForm(c.getFiller()));
		}

		return classExpression;

	}

	/**
	 * disjunctive normal form
	 * 
	 * @param dataRange
	 * @return a {@link OWLDataRange} in disjunctive normal form equivalents to the specified one.
	 */
	public static OWLDataRange dataRangeDisjunctiveNormalForm(
			OWLDataRange dataRange) {

		if (dataRange instanceof OWLDataUnionOf) {
			Set<OWLDataRange> union = new TreeSet<>();
			for (OWLDataRange element : OWLAPIUtils
					.getDataUnionOperands(dataRange)) {
				element = dataRangeDisjunctiveNormalForm(element);
				for (OWLDataRange e : OWLAPIUtils
						.getDataUnionOperands(element)) {
					union.add(e);
				}
			}
			return new OWLDataUnionOfImpl(union);
		} else if (dataRange instanceof OWLDataIntersectionOf) {
			List<Set<OWLDataRange>> conjunctions = new LinkedList<>();
			conjunctions.add(new TreeSet<OWLDataRange>());

			for (OWLDataRange element : OWLAPIUtils
					.getDataIntersectionOperands(dataRange)) {
				element = dataRangeDisjunctiveNormalForm(element);
				if (element instanceof OWLDataUnionOf) {
					List<Set<OWLDataRange>> tmp = new LinkedList<>();

					for (Set<OWLDataRange> conj : conjunctions) {
						for (OWLDataRange e : OWLAPIUtils
								.getDataUnionOperands(element)) {
							Set<OWLDataRange> newConj = new TreeSet<>(conj);
							newConj.add(dataRangeDisjunctiveNormalForm(e));
							tmp.add(newConj);
						}
					}
					conjunctions = tmp;
				} else {
					for (Set<OWLDataRange> conj : conjunctions) {
						for (OWLDataRange e : OWLAPIUtils
								.getDataIntersectionOperands(element)) {
							conj.add(e);
						}
					}
				}
			}
			Set<OWLDataRange> union = new TreeSet<>();
			if (conjunctions.size() > 1) {
				for (Set<OWLDataRange> conj : conjunctions) {
					union.add(new OWLDataIntersectionOfImpl(conj));
				}
				return new OWLDataUnionOfImpl(union);
			} else {
				return new OWLDataIntersectionOfImpl(conjunctions.get(0));
			}
		} else if (dataRange instanceof OWLDataComplementOf) {
			OWLDataComplementOf expr = (OWLDataComplementOf) dataRange;
			return new OWLDataComplementOfImpl(
					dataRangeDisjunctiveNormalForm(expr.getDataRange()));
		} else if (dataRange instanceof OWLObjectOneOf) {
			OWLDataOneOf expr = (OWLDataOneOf) dataRange;
			if (expr.getValues().size() <= 1) {
				return expr;
			}
			Set<OWLDataRange> union = new TreeSet<>();
			for (OWLLiteral i : expr.getValues()) {
				Set<OWLLiteral> individuals = Collections.singleton(i);
				union.add(new OWLDataOneOfImpl(individuals));
			}
			return new OWLDataUnionOfImpl(union);
		}

		return dataRange;

	}

}
