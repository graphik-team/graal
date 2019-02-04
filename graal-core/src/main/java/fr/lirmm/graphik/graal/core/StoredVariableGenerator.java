package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;

/**
 * 
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class StoredVariableGenerator extends DefaultVariableGenerator{
		
	/**
	 * List of all generated symbol
	 */
	private LinkedList<Term> generatedSymbols;
	
	/**
	 * SubList of @see{generatedSymbols}
	 * Store only generated symbol until resetGeneratedSymbol() is called
	 */
	private LinkedList<Term> newGeneratedSymbols;
	
	public StoredVariableGenerator(String prefix) {
		super(prefix);
		generatedSymbols=new LinkedList<>();
		newGeneratedSymbols = new LinkedList<>();
	}


	/**
	 * 
	 * @return the list of generated symbol
	 */
	public Collection<Term> getGeneratedSymbol(){
		return generatedSymbols;
	}
	
	/**
	 * Getting the list of new generated symbol is useful 
	 * for check at each chase step, if some special processes must be done or not
	 * @return the list of new generated symbol

	 */
	public Collection<Term> getNewGeneratedSymbol(){
		return newGeneratedSymbols;
	}
	
	/**
	 * Reset the list of newGeneratedSymbol
	 */
	public void resetNewGeneratedSymbol() {
		newGeneratedSymbols.clear();
	}
	
	@Override
	public Variable getFreshSymbol() {
		Variable var = super.getFreshSymbol();
		generatedSymbols.add(var);
		newGeneratedSymbols.add(var);
		return var;
	}
	
}
