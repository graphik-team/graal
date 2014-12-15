/**
 * 
 */
package fr.lirmm.graphik.graal.logic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

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
	
	// /////////////////////////////////////////////////////////////////////////
	// TEST METHODS - TODO move into unit test
	// /////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		LogicalFormulaDisjunctiveForm f = new LogicalFormulaDisjunctiveForm();
		LogicalFormulaDisjunctiveForm f1 = new LogicalFormulaDisjunctiveForm();
		LogicalFormulaDisjunctiveForm f2 = new LogicalFormulaDisjunctiveForm();
		LogicalFormulaDisjunctiveForm f3 = new LogicalFormulaDisjunctiveForm();
		
		f1.and(new Literal(DlgpParser.parseAtom("a1(X)."), true));
		f1.and(new Literal(DlgpParser.parseAtom("a2(X)."), false));
		f1.and(new Literal(DlgpParser.parseAtom("a3(X)."), true));
		
		f2.and(new Literal(DlgpParser.parseAtom("b1(X)."), true));
		f2.and(new Literal(DlgpParser.parseAtom("b2(X)."), false));
		f2.and(new Literal(DlgpParser.parseAtom("b3(X)."), true));
		
		f3.and(new Literal(DlgpParser.parseAtom("c1(X)."), true));
		f3.and(new Literal(DlgpParser.parseAtom("c2(X)."), false));
		f3.and(new Literal(DlgpParser.parseAtom("c3(X)."), true));
		System.out.println(f3);
		
		System.out.println("=================");
		f.or(f1);
		System.out.println(f);
		f.or(f2);
		System.out.println(f);
		f.or(f3);
		System.out.println(f);
		f.not();
		System.out.println("===================");
		System.out.println(f);
	}
}
