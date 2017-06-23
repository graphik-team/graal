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
 package fr.lirmm.graphik.graal.core.unifier;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.Partition;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * An unifier describe how to unify a piece of a fact with a part of an head
 * rule in order to rewrite the fact according to the rule
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class QueryUnifier {
	/**
	 * the rule that are unified with the query
	 */
	private Rule rule;
	/**
	 * the query that are unified with the rule head
	 */
	private ConjunctiveQuery query;
	/**
	 * the part of the query that are unified
	 */
	private InMemoryAtomSet piece;
	/**
	 * the partition that unify the piece and a part of the head rule
	 */
	private Partition<Term>  partition;

	private Substitution associatedSubstitution;

	public QueryUnifier(InMemoryAtomSet piece, Partition<Term> partition, Rule rule,
			ConjunctiveQuery query) {
		super();
		this.rule = rule;
		this.query = query;
		this.piece = piece;
		this.partition = partition;
	}

	/**
	 * Return the rule where the unifier apply
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Change the rule where the unifier apply by the given one
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	/**
	 * Change the piece of the fact that are unified by this unificateur
	 */
	public void setPiece(InMemoryAtomSet piece) {
		this.piece = piece;
	}

	/**
	 * Change the substitution that unify the piece and a part of the head rule
	 */
	public void setSubstitution(Partition<Term> partition) {
		this.partition = partition;
	}

	/**
	 * Return the piece of the fact that are unified by this unificateur
	 */
	public InMemoryAtomSet getPiece() {
		return piece;
	}

	/**
	 * @return the query unified
	 */
	public ConjunctiveQuery getQuery() {
		return query;
	}

	/**
	 * Return the partition that unify the piece and a part of the head rule
	 */
	public Partition<Term> getPartition() {
		return partition;
	}

	/**
	 * Return the image of a given fact by the substitution of this
	 * 
	 * @return the image of a given fact,
	 */
	public InMemoryAtomSet getImageOf(InMemoryAtomSet f) {
		InMemoryAtomSet atomset = null;

		if (associatedSubstitution == null) {
			associatedSubstitution = TermPartitionUtils.getAssociatedSubstitution(partition, query);
		}

		if (associatedSubstitution != null) {
			atomset = associatedSubstitution.createImageOf(f);
		}

		return atomset;
	}
	
	public Substitution getAssociatedSubstitution() {
		if (associatedSubstitution == null) {
			associatedSubstitution = TermPartitionUtils.getAssociatedSubstitution(partition, query);
		}
		return DefaultSubstitutionFactory.instance().createSubstitution(associatedSubstitution);
	}

	@Override
	public String toString() {
		try {
			return "(QueryUnifier |  " + piece + " <=>" + rule.getHead() + " | = " + partition + ")";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the aggregation of the given unifier and the receiving unifier
	 * 
	 * @param u
	 *            an unifier
	 * @return unifier
	 */
	public QueryUnifier aggregate(QueryUnifier u) {
		// we create a piece that is the union of the two pieces
		InMemoryAtomSet pieces = new LinkedListAtomSet();
		pieces.addAll(getPiece());
		pieces.addAll(u.getPiece());


		// we create a rule that is the aggregation of the two rules
		InMemoryAtomSet b = new LinkedListAtomSet();
		InMemoryAtomSet h = new LinkedListAtomSet();

		CloseableIteratorWithoutException<Atom> it = getRule().getBody().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			b.add(a);
		}

		it = getRule().getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			h.add(a);
		}

		it = u.getRule().getBody().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			b.add(a);
		}

		it = u.getRule().getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			h.add(a);
		}

		Rule rule = DefaultRuleFactory.instance().create(b, h);
		// we create the partition which is the join of the two partitions
		Partition<Term> part = getPartition().join(u.getPartition());

		return new QueryUnifier(pieces, part, rule, getQuery());
	}

	/**
	 * Returns true if the given unifier is compatible with the receiving
	 * unifier
	 * 
	 * @param u
	 *            a unifier
	 * @return boolean
	 */
	public boolean isCompatible(QueryUnifier u) {
		// if the pieces of the two unifiers have atom in common the unifiers
		// are not compatible
		CloseableIteratorWithoutException<Atom> it1 = u.getPiece().iterator();
		CloseableIteratorWithoutException<Atom> it2 = this.getPiece().iterator();
		while (it1.hasNext()) {
			Atom a1 = it1.next();
			while (it2.hasNext()) {
				Atom a2 = it2.next();
				if (a1.equals(a2)) {
					return false;
				}
			}
		}
		return TermPartitionUtils.getAssociatedSubstitution(this.getPartition().join(u.getPartition()),
				null) != null;
	}

}
