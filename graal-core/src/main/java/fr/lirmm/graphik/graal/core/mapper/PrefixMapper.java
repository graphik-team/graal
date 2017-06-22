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
package fr.lirmm.graphik.graal.core.mapper;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.util.URI;

/**
 * This class allows to map a predicate name to an other predicate with the same
 * name but prefixed.
 * 
 * {@code
 * Predicate p = new Predicate("p",1);
 * Predicate prefixedP = new Predicate("prefix#p",1);
 * 
 * Mapper mapper = new PrefixMapper("prefix#");
 * 
 * assert mapper.map(p).equals(prefixedP);
 * assert mapper.inverse.map(prefixedP).equals(p);
 * assert mapper.unmap(prefixedP).equals(p);
 * assert mapper.inverse.map(p).equals(prefixedP);
 * 
 * }
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PrefixMapper extends AbstractMapper {

	private String prefix;
	private String format;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public PrefixMapper(String prefix) {
		this.prefix = prefix;
		this.format = prefix + "%s";
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Predicate map(Predicate predicate) {
		return new Predicate(String.format(format, predicate.getIdentifier()), predicate.getArity());
	}

	@Override
	public Predicate unmap(Predicate predicate) {
		Object identifier = predicate.getIdentifier();
		String id = null;
		if (identifier instanceof String) {
			id = (String) identifier;
		} else if (identifier instanceof URI) {
			id = ((URI) identifier).toString();
		} else {
			return predicate;
		}

		if (id.startsWith(prefix)) {
			return new Predicate(id.substring(prefix.length()), predicate.getArity());
		} else {
			return predicate;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
