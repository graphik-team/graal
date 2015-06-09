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
public class LogicalFormulaDisjunctiveForm implements Iterable<Collection<Literal>> {
	
	private Collection<Collection<Literal>> disjunctiveNormalForm = new LinkedList<Collection<Literal>>();
	
	public LogicalFormulaDisjunctiveForm() {
		this.disjunctiveNormalForm = new LinkedList<Collection<Literal>>();
	}
	
	public LogicalFormulaDisjunctiveForm(Literal l) {
		this();
		Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
		conjunctiveClause.add(l);
		this.disjunctiveNormalForm.add(conjunctiveClause);
	}

	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	public void or(LogicalFormulaDisjunctiveForm f) {
		for(Collection<Literal> conjunctiveClause : f) {
			this.disjunctiveNormalForm.add(conjunctiveClause);
		}
	}
	
	public void or(Literal l) {
		Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
		conjunctiveClause.add(l);
		this.disjunctiveNormalForm.add(conjunctiveClause);
	}
	
	public void and(LogicalFormulaDisjunctiveForm f) {
		Collection<Collection<Literal>> newDisjunctiveNormalForm = new LinkedList<Collection<Literal>>();

		if(this.disjunctiveNormalForm.isEmpty()) {
			for(Collection<Literal> conjClause : f) {
				Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
				conjunctiveClause.addAll(conjClause);
				newDisjunctiveNormalForm.add(conjunctiveClause);
			}
		} else if(f.disjunctiveNormalForm.isEmpty()) {
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
		
		this.disjunctiveNormalForm = newDisjunctiveNormalForm;
	}
	
	public void and(Literal l) {
		if(this.disjunctiveNormalForm.isEmpty()) {
			Collection<Literal> conjunctiveClause = new LinkedList<Literal>();
			conjunctiveClause.add(l);
			this.disjunctiveNormalForm.add(conjunctiveClause);
		} else {
			for(Collection<Literal> conjClauseIntern : this) {
				conjClauseIntern.add(l);
			}
		}
	}
	
	public void not() {
		LogicalFormulaDisjunctiveForm newFormula = new LogicalFormulaDisjunctiveForm();

		Iterator<Collection<Literal>> it = this.iterator();
		if(it.hasNext()) {
			for(Literal l : it.next()) {
				newFormula.or(new Literal(l, !l.isPositive));
			}
		}
		
		while(it.hasNext()) {
			LogicalFormulaDisjunctiveForm tmpFormula = new LogicalFormulaDisjunctiveForm();
			for(Literal l : it.next()) {
				tmpFormula.or(new Literal(l, !l.isPositive));
			}
			newFormula.and(tmpFormula);
		}
		
		this.disjunctiveNormalForm = newFormula.disjunctiveNormalForm;
	}

	@Override
	public Iterator<Collection<Literal>> iterator() {
		return this.disjunctiveNormalForm.iterator();
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
				sb.append(" ^ ");
			}
			sb.append(") v");
		}
		return sb.toString();
	}
	
}
