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
package fr.lirmm.graphik.graal.io.owl.logic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author clement
 *
 */
public class LogicalFormula implements Iterable<Collection<Literal>> {
	
	private Collection<Collection<Literal>> conjunctiveNormalForm = new LinkedList<Collection<Literal>>();
	
	public LogicalFormula() {
		this.conjunctiveNormalForm = new LinkedList<Collection<Literal>>();
	}
	
	public LogicalFormula(Literal l) {
		this();
		Collection<Literal> clause = new LinkedList<Literal>();
		clause.add(l);
		this.conjunctiveNormalForm.add(clause);
	}
	
	/**
	 * Copy constructor
	 * @param f
	 */
	public LogicalFormula(LogicalFormula f) {
		this();
		this.and(f);
	}

	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	public void and(LogicalFormula f) {
		for(Collection<Literal> conjunctiveClause : f) {
			this.conjunctiveNormalForm.add(conjunctiveClause);
		}
	}
	
	public void and(Literal l) {
		Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
		conjunctiveClause.add(l);
		this.conjunctiveNormalForm.add(conjunctiveClause);
	}
	
	public void or(LogicalFormula f) {
		Collection<Collection<Literal>> newDisjunctiveNormalForm = new LinkedList<Collection<Literal>>();

		if(this.conjunctiveNormalForm.isEmpty()) {
			for(Collection<Literal> conjClause : f) {
				Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
				conjunctiveClause.addAll(conjClause);
				newDisjunctiveNormalForm.add(conjunctiveClause);
			}
		} else if(f.conjunctiveNormalForm.isEmpty()) {
			for(Collection<Literal> conjClause : this) {
				Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
				conjunctiveClause.addAll(conjClause);
				newDisjunctiveNormalForm.add(conjunctiveClause);
			}
		} else {
			for(Collection<Literal> conjClauseIntern : this) {
				for(Collection<Literal> conjClauseExtern : f) {
					Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
					conjunctiveClause.addAll(conjClauseExtern);
					conjunctiveClause.addAll(conjClauseIntern);
					newDisjunctiveNormalForm.add(conjunctiveClause);
				}
			}
		}
		
		this.conjunctiveNormalForm = newDisjunctiveNormalForm;
	}
	
	public void or(Literal l) {
		if(this.conjunctiveNormalForm.isEmpty()) {
			Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
			conjunctiveClause.add(l);
			this.conjunctiveNormalForm.add(conjunctiveClause);
		} else {
			for(Collection<Literal> conjClauseIntern : this) {
				conjClauseIntern.add(l);
			}
		}
	}
	
	public void not() {
		LogicalFormula newFormula = new LogicalFormula();

		Iterator<Collection<Literal>> it = this.iterator();
		if(it.hasNext()) {
			for(Literal l : it.next()) {
				newFormula.and(new Literal(l, !l.isPositive));
			}
		}
		
		while(it.hasNext()) {
			LogicalFormula tmpFormula = new LogicalFormula();
			for(Literal l : it.next()) {
				tmpFormula.and(new Literal(l, !l.isPositive));
			}
			newFormula.or(tmpFormula);
		}
		
		this.conjunctiveNormalForm = newFormula.conjunctiveNormalForm;
	}

	@Override
	public Iterator<Collection<Literal>> iterator() {
		return this.conjunctiveNormalForm.iterator();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Collection<Literal> conjClause : this) {
			sb.append("(");
			for(Literal l : conjClause) {
				l.toString(sb);
				sb.append(" v ");
			}
			sb.append(") ^ ");
		}
		return sb.toString();
	}
	
}
