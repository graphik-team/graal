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
package fr.lirmm.graphik.graal.api.kb;

import java.io.Closeable;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public interface KnowledgeBase extends Profilable, Closeable {

	/**
	 * Get the ontology attached to this knowledge base.
	 * 
	 * @return a RuleSet representing the ontology.
	 */
	RuleSet getOntology();

	/**
	 * Get the facts attached to this knowledgeBase.
	 * 
	 * @return an AtomSet representing a conjunction of facts.
	 */
	AtomSet getFacts();
	
	Set<String> getRuleNames();
	Rule getRule(String name);
	
	boolean addQuery(Query query);
	Set<String> getQueryNames();
	Query getQuery(String name);

	/**
	 * Return true if this knowledge base is consistent, false otherwise.
	 * 
	 * @return
	 * @throws KnowledgeBaseException
	 */
	boolean isConsistent() throws KnowledgeBaseException;

	/**
	 * Saturate this knowledge base.
	 * 
	 * @throws KnowledgeBaseException
	 */
	void saturate() throws KnowledgeBaseException;

	/**
	 * Find an homomorphism of the query in the fact base associated with this knowledge base. 
	 * @param query
	 * @return An iterator over substitutions which represents founded homomorphisms.
	 * @throws KnowledgeBaseException
	 */
	CloseableIterator<Substitution> homomorphism(Query query) throws KnowledgeBaseException;

	/**
	 * Execute the query over this Knowledge Base. This method uses the graal-rules-analyser module (Kiabora) to 
	 * find a decidable way to answer.
	 * @param query
	 * @return
	 * @throws KnowledgeBaseException
	 */
	CloseableIterator<Substitution> query(Query query) throws KnowledgeBaseException;
	
	/**
	 * 
	 * @param query
	 * @param timeout in seconds
	 * @return
	 * @throws KnowledgeBaseException
	 */
	CloseableIterator<Substitution> query(Query query, int timeout) throws KnowledgeBaseException;

	/**
	 * @throws ChaseException
	 * 
	 */
	void semiSaturate() throws KnowledgeBaseException;

	/**
	 * Returns the defined priority of this KnowledgeBase (i.e saturation or rewriting)
	 * @return
	 */
	Priority getPriority();

	void close();

}
