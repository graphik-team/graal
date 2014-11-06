/**
 * 
 */
package fr.lirmm.graphik.graal.core;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FreeVarSubstitution extends TreeMapSubstitution {
	
	private SymbolGenerator gen = new DefaultFreeVarGen("X" + Integer.toString(this.getClass().hashCode()));
	
	@Override
	public Term getSubstitute(Term term) {
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
