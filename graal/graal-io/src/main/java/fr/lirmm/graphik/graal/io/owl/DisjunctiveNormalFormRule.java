/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.util.Collection;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author clement
 *
 */
public class DisjunctiveNormalFormRule implements Rule {

	// not A v not B v not C
	private NegativeDisjunction body = new NegativeDisjunction();
 	
	// A ^ B ^ C 
	private PositiveConjunction head = new PositiveConjunction();
	
	@Override
	public int compareTo(Rule arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public String getLabel() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void setLabel(String label) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public AtomSet getBody() {
		return this.body;
	}

	@Override
	public AtomSet getHead() {
		return this.head;
	}

	@Override
	public Set<Term> getFrontier() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Set<Term> getExistentials() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Set<Term> getTerms() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Collection<AtomSet> getPieces() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	
	private static class NegativeDisjunction extends PositiveConjunction {
		
		public PositiveConjunction getComplementOf() {
			return this;
		}
	}
	
	private static class PositiveConjunction extends LinkedListAtomSet {
		
		public AtomSet toAtomSet() {
			return this;
		}
	}
}
