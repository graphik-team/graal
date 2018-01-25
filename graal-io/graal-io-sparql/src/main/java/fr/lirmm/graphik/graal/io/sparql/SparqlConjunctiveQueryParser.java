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
package fr.lirmm.graphik.graal.io.sparql;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.io.ParseError;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlConjunctiveQueryParser {

	private ConjunctiveQuery query;
	private List<Prefix>     prefixes;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public SparqlConjunctiveQueryParser(String sparqlQuery) {
		super();
		this.execute(sparqlQuery);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public List<Prefix> getPrefixes() {
		return this.prefixes;
	}

	public ConjunctiveQuery getConjunctiveQuery() {
		return this.query;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void execute(String queryString) {

		this.prefixes = new LinkedList<Prefix>();
		List<Term> ans = new LinkedList<Term>();
		Query sparql = QueryFactory.create(queryString);

		for (Map.Entry<String, String> e : sparql.getPrefixMapping().getNsPrefixMap().entrySet()) {
			this.prefixes.add(new Prefix(e.getKey(), e.getValue()));
		}

		if (sparql.isSelectType()) {
			for (String v : sparql.getResultVars()) {
				ans.add(DefaultTermFactory.instance().createVariable(v));
			}
		}

		ElementVisitorImpl visitor = new ElementVisitorImpl(DefaultAtomSetFactory.instance().create());
		sparql.getQueryPattern().visit(visitor);
		
		// check if answer variables appear in the query body
		Set<Variable> bodyVars = visitor.getAtomSet().getVariables();
		for(Term t : ans) {
			if(t.isVariable() && !bodyVars.contains(t)) {
				throw new ParseError("The variable ["+ t +"] of the answer list does not appear in the query body.");
			}
		}

		this.query = DefaultConjunctiveQueryFactory.instance().create(visitor.getAtomSet(), ans);
	}

}
