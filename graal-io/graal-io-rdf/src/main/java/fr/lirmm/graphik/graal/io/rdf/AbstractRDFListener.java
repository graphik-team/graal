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

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URIUtils;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractRDFListener extends AbstractRDFHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRDFListener.class);

	@Override
	public void handleStatement(Statement st) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(st.toString());
		}
		Predicate predicate = new Predicate(new DefaultURI(st.getPredicate().toString()), 2);
		Term subject = DefaultTermFactory.instance().createConstant(new DefaultURI(st.getSubject().toString()));
		Term object;
		fr.lirmm.graphik.util.URI datatype;

		if (st.getObject() instanceof Literal) {
			Literal l = (Literal) st.getObject();
			if (l.getDatatype() == null) {
				datatype = URIUtils.RDF_LANG_STRING;
			} else {
				datatype = new fr.lirmm.graphik.util.DefaultURI(l.getDatatype().getNamespace(),
						l.getDatatype().getLocalName());
			}
			String value = l.getLabel();
			if(datatype.equals(URIUtils.RDF_LANG_STRING)) {
				value += "@" + l.getLanguage();
			}
			object = DefaultTermFactory.instance().createLiteral(datatype, value);
		} else {
			object = DefaultTermFactory.instance().createConstant(new DefaultURI(st.getObject().toString()));
		}
		DefaultAtom a = new DefaultAtom(predicate, subject, object);

		this.createAtom(a);
	}

	/**
	 * @param a
	 */
	protected abstract void createAtom(DefaultAtom a);

}
