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
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.rdf;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractRDFListener extends RDFHandlerBase {

	@Override
	public void handleStatement(Statement st) {
		Predicate predicate = new Predicate(st.getPredicate().toString(), 2);
		Term subject = DefaultTermFactory.instance().createConstant(
				st.getSubject().toString());
		Term object;
		if (st.getObject() instanceof Literal) {
			Literal l = (Literal) st.getObject();
			fr.lirmm.graphik.util.URI datatype = new fr.lirmm.graphik.util.DefaultURI(
					l.getDatatype().getNamespace(), l.getDatatype()
							.getLocalName());
			object = DefaultTermFactory.instance().createLiteral(datatype,
					l.getLabel());
		} else {
			object = DefaultTermFactory.instance().createConstant(
					st.getObject().toString());
		}
		DefaultAtom a = new DefaultAtom(predicate, subject, object);
		
		this.createAtom(a);
	}

	/**
	 * @param a
	 */
	protected abstract void createAtom(DefaultAtom a);
}
