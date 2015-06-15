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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * An unifier describe how to unify a piece of a fact with a part of an head
 * rule in order to rewrite the fact according to the rule
 * 
 * @author Mélanie KÖNIG
 * 
 */
class QueryUnifier {
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
	private TermPartition partition;

	private Substitution associatedSubstitution;

	public QueryUnifier(InMemoryAtomSet piece, TermPartition partition, Rule rule,
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
	public void setSubstitution(TermPartition partition) {
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
	public TermPartition getPartition() {
		return partition;
	}

	/**
	 * Return the image of a given fact by the substitution of this
	 * 
	 * @return the image of a given fact,
	 */
	public AtomSet getImageOf(InMemoryAtomSet f) {
		InMemoryAtomSet atomset = null;

		if (associatedSubstitution == null) {
			associatedSubstitution = partition.getAssociatedSubstitution(query);
		}

		if (associatedSubstitution != null) {
			atomset = associatedSubstitution.createImageOf(f);
		}

		return atomset;
	}

	@Override
	public String toString() {
		try {
			return "(Unifier|  P = " + piece + ", p = " + partition + ")";
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
	 * @throws Exception
	 */
	public QueryUnifier aggregate(QueryUnifier u) {
		// we create a piece that is the union of the two pieces
		InMemoryAtomSet pieces = new LinkedListAtomSet();
		pieces.addAll(getPiece());
		pieces.addAll(u.getPiece());


		// we create a rule that is the aggregation of the two rules
		InMemoryAtomSet b = new LinkedListAtomSet();
		InMemoryAtomSet h = new LinkedListAtomSet();
		for (Atom a : getRule().getBody())
			b.add(a);
		for (Atom a : getRule().getHead())
			h.add(a);

		for (Atom a : u.getRule().getBody()) {
			b.add(a);
		}
		for (Atom a : u.getRule().getHead()) {
			h.add(a);
		}
		Rule rule = new DefaultRule(b, h);
		// we create the partition which is the join of the two partitions
		TermPartition part = getPartition().join(u.getPartition());

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
		for (Atom a1 : u.getPiece()) {
			for (Atom a2 : this.getPiece()) {
				if (a1.equals(a2)) {
					return false;
				}
			}
		}
		return getPartition().join(u.getPartition()).getAssociatedSubstitution(
				null) != null;
	}

}
