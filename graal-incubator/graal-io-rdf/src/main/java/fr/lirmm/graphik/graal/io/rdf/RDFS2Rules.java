/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.io.rdf;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RDFS2Rules implements Parser<Object> {

	public static final String RDFS_PREFIX = "http://www.w3.org/2000/01/rdf-schema#";

	/** range(p,c) => c(Y) :- p(X,Y) */
	public static final String RDFS_RANGE = RDFS_PREFIX + "range";

	/** domain(p,c) => c(X) :- p(X,Y) */
	public static final String RDFS_DOMAIN = RDFS_PREFIX + "domain";
	
	
	/** subClassOf(c1, c2) => c2(X) :- c1(X) */
	public static final String RDFS_SUB_CLASS_OF = RDFS_PREFIX + "subClassOf";
	
	/** subPropertyOf(p1, p2) => p2(X,Y) :- p1(X,Y); */
	public static final String RDFS_SUB_PROPERTY_OF = RDFS_PREFIX
													  + "subPropertyOf";
	
	/** */
	public static final String RDFS_LABEL = RDFS_PREFIX + "label";
	
	/** */
	public static final String RDFS_COMMENT = RDFS_PREFIX + "comment";

	protected static final Term X = DefaultTermFactory.instance()
			.createVariable("X");
	protected static final Term Y = DefaultTermFactory.instance()
			.createVariable("Y");
	protected static final Term Z = DefaultTermFactory.instance()
			.createVariable("Z");

	private Parser<Object>      reader;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDFS2Rules(RDFParser atomReader) {
		this.reader = atomReader;
	}
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Object next() throws IteratorException {
		Object o = this.reader.next();

		if (o instanceof Atom) {
			Atom a = (Atom) o;
			String predicateLabel = a.getPredicate().toString();
			if (RDFS_RANGE.equals(predicateLabel)) {
				Rule rule = DefaultRuleFactory.instance().create();
				Predicate p = new Predicate(a.getTerm(0).toString(), 2);
				rule.getBody().add(new DefaultAtom(p, X, Y));
				p = new Predicate(a.getTerm(1).toString(), 1);
				rule.getHead().add(new DefaultAtom(p, Y));
				o = rule;

			} else if (RDFS_DOMAIN.equals(predicateLabel)) {
				Rule rule = DefaultRuleFactory.instance().create();
				Predicate p = new Predicate(a.getTerm(0).toString(), 2);
				rule.getBody().add(new DefaultAtom(p, X, Y));
				p = new Predicate(a.getTerm(1).toString(), 1);
				rule.getHead().add(new DefaultAtom(p, X));
				o = rule;

			} else if (RDFS_SUB_CLASS_OF.equals(predicateLabel)) {
				Rule rule = DefaultRuleFactory.instance().create();
				Predicate p1 = new Predicate(a.getTerm(0).toString(), 1);
				Predicate p2 = new Predicate(a.getTerm(1).toString(), 1);
				rule.getBody().add(new DefaultAtom(p1, X));
				rule.getHead().add(new DefaultAtom(p2, X));
				o = rule;

			} else if (RDFS_SUB_PROPERTY_OF.equals(predicateLabel)) {
				Rule rule = DefaultRuleFactory.instance().create();
				Predicate p1 = new Predicate(a.getTerm(0).toString(), 2);
				Predicate p2 = new Predicate(a.getTerm(1).toString(), 2);
				rule.getBody().add(new DefaultAtom(p1, X, Y));
				rule.getHead().add(new DefaultAtom(p2, X, Y));
				o = rule;

			}
		}

		return o;
	}

	@Override
	public boolean hasNext() throws IteratorException {
		return this.reader.hasNext();
	}
	
	@Override
	public void close() {
		
	}
	
}
