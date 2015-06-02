/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FreeVarSubstitution extends TreeMapSubstitution {
	
	private SymbolGenerator gen;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public FreeVarSubstitution() {
		this(new DefaultFreeVarGen("X"
				+ Integer.toString(FreeVarSubstitution.class.hashCode())));
	}
	
	public FreeVarSubstitution(SymbolGenerator gen) {
		this.gen = gen;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Term createImageOf(Term term) {
		Term substitut = term;
		if(Term.Type.VARIABLE.equals(term.getType())) {
			substitut = this.getMap().get(term);
			if(substitut == null) {
				substitut = gen.getFreeVar();
				this.put(term, substitut);
			}
		}
		return substitut;
	}
	

}
