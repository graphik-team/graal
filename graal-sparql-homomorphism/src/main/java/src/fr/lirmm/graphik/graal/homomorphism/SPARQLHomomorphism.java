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

package src.fr.lirmm.graphik.graal.homomorphism;

import java.io.StringWriter;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.homomorphism.AbstractHomomorphism;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.store.triplestore.rdf4j.RDF4jStore;
import fr.lirmm.graphik.graal.store.triplestore.rdf4j.TupleQueryResult2SubstitutionConverter;
import fr.lirmm.graphik.graal.store.triplestore.rdf4j.TupleQueryResultCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.Iterators;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/*
 * @author Mathieu Dodard
 * @author Renaud Colin
 */
public class SPARQLHomomorphism extends AbstractHomomorphism<Query, RDF4jStore>
		implements Homomorphism<Query, RDF4jStore>, HomomorphismWithCompilation<Query, RDF4jStore> {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SPARQLHomomorphism.class);

	private static SPARQLHomomorphism instance;

	private SPARQLHomomorphism() {

	}

	///////////////////////////
	////// STATIC METHOD //////
	///////////////////////////

	public static SPARQLHomomorphism instance() {
		if (instance == null)
			instance = new SPARQLHomomorphism();

		return instance;
	}

	////////////////////////////
	////// PUBLIC METHODS //////
	////////////////////////////

	/**
	 * Executes a query and finds all the substitutions in the given store.
	 * 
	 * @param query the conjunctive query you want to execute
	 * @param store the store that contains all the facts
	 * 
	 * @return An iterator of substitutions
	 * 
	 * @throws HomomorphismException
	 **/
	@Override
	public CloseableIterator<Substitution> execute(Query query, RDF4jStore store) throws HomomorphismException {
		ConjunctiveQuery cquery = (ConjunctiveQuery) query;

		StringWriter stringWriter = new StringWriter();
		SparqlConjunctiveQueryWriter writer = new SparqlConjunctiveQueryWriter(stringWriter);

		try {
			writer.write((ConjunctiveQuery) cquery);
			String sparqlQuery = stringWriter.toString();
			RepositoryConnection conn = store.getConnection();

			if (cquery.getAtomSet().isEmpty()) { // empty query
				return Iterators.<Substitution>singletonIterator(Substitutions.emptySubstitution());
			}

			if (cquery.getAnswerVariables().isEmpty()) { // ask query
				BooleanQuery boolQuery = conn.prepareBooleanQuery(QueryLanguage.SPARQL, sparqlQuery);

				boolean resp = boolQuery.evaluate();
				if (resp)
					return Iterators.<Substitution>singletonIterator(Substitutions.emptySubstitution()); // true ->
																											// empty
																											// substitution
				else
					return Iterators.<Substitution>emptyIterator(); // false -> no substitution
			}

			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
			TupleQueryResult tupleQueryResult = tupleQuery.evaluate(); // get results of query

			CloseableIterator<TupleQueryResult> iterator = new TupleQueryResultCloseableIterator(tupleQueryResult);
			TupleQueryResult2SubstitutionConverter convertIt = new TupleQueryResult2SubstitutionConverter(
					cquery.getAnswerVariables(), store.getUtils());

			return new ConverterCloseableIterator<TupleQueryResult, Substitution>(iterator, convertIt); // translate

		} catch (Exception e) {
			throw new HomomorphismException(e.getMessage());
		}

	}

	/**
	 * Executes a query and finds all the substitutions in the given store
	 * considering that the rule got compiled. If no compilation is given, then the
	 * same method without compilation is called.
	 * 
	 * @param query       the conjunctive query you want to execute
	 * @param store       the store that contains all the facts
	 * @param compilation The compiled rules
	 * 
	 * @return An iterator of substitutions
	 * 
	 * @throws HomomorphismException
	 **/
	@Override
	public CloseableIterator<Substitution> execute(Query query, RDF4jStore store, RulesCompilation compilation)
			throws HomomorphismException {
		ConjunctiveQuery cquery = (ConjunctiveQuery) query;

		if (compilation == null)
			return execute(cquery, store);

		StringBuilder stringWriter = new StringBuilder();
		// get conjunctive query translation into SPARQL
//		SparqlQueryWriter writer = new SparqlQueryWriter(stringWriter);

		{
//			DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(cquery.getAnswerVariables(),
//					cquery);
//			CloseableIteratorWithoutException<Query> unfold = PureRewriter.unfold(ucq, compilation);
//			DefaultUnionOfConjunctiveQueries queriesUnion = new DefaultUnionOfConjunctiveQueries(
//					query.getAnswerVariables(), unfold);

//			writer.write(queriesUnion);

			String sparqlQuery = stringWriter.toString();
			RepositoryConnection conn = store.getConnection();

			// check if it is not an empty query
			if (cquery.getAtomSet().isEmpty())
				return Iterators.<Substitution>singletonIterator(Substitutions.emptySubstitution());

			// check if it is not a boolean query
			if (cquery.getAnswerVariables().isEmpty()) {

				BooleanQuery boolQuery = conn.prepareBooleanQuery(QueryLanguage.SPARQL, sparqlQuery);
				boolean resp = boolQuery.evaluate();

				if (resp) {
					// true -> just an empty substitution
					return Iterators.<Substitution>singletonIterator(Substitutions.emptySubstitution());
				} else
					return Iterators.<Substitution>emptyIterator(); // false -> no substitution
			}

			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
			TupleQueryResult tupleQueryResult = tupleQuery.evaluate(); // get results of query
			CloseableIterator<TupleQueryResult> iterator = new TupleQueryResultCloseableIterator(tupleQueryResult);

			// send answer variables and an RDF4J Utils instance
			TupleQueryResult2SubstitutionConverter convertIt = new TupleQueryResult2SubstitutionConverter(
					cquery.getAnswerVariables(), store.getUtils());

			// translate tupleQuery into closableIterator<Substitution>
			return new ConverterCloseableIterator<TupleQueryResult, Substitution>(iterator, convertIt);
		}
	}

	@Override
	public CloseableIterator<Substitution> execute(Query q, RDF4jStore a, Substitution s) throws HomomorphismException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(Query q, RDF4jStore a, RulesCompilation compilation) throws HomomorphismException {
		throw new NotImplementedException("Not yet implemented method");
	}

	@Override
	public boolean exist(Query q, RDF4jStore a, RulesCompilation compilation, Substitution s)
			throws HomomorphismException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CloseableIterator<Substitution> execute(Query q, RDF4jStore a, RulesCompilation compilation, Substitution s)
			throws HomomorphismException {
		// TODO Auto-generated method stub
		return null;
	}
}