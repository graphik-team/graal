package org.graal.store.dictionary;

import java.io.IOException;
import java.util.Collection;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.core.StoredVariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * Extends of DefaultInMemoryGraphStore with use of {@link DictionaryMapper}
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class DictionaryInMemoryGraphStore extends DefaultInMemoryGraphStore{
	
	public final static String EXISTENTIAL_VAR_PREFIX = "EE";
	
	/**
	 * 
	 */
	private DictionaryMapper dictionnaryMapper;
	
	/**
	 * 
	 */
	private StoredVariableGenerator variableGenerator = new StoredVariableGenerator(EXISTENTIAL_VAR_PREFIX);
	
	public DictionaryInMemoryGraphStore(DictionaryMapper dictionnaryMapper) {
		super();
		this.dictionnaryMapper=dictionnaryMapper;		
	}
	
	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return variableGenerator;
	}
	
	@Override
	public boolean addAll(CloseableIterator<? extends Atom> stream) throws AtomSetException {
		Collection<Term> newExistentialTerms = variableGenerator.getNewGeneratedSymbol();		
		if(! newExistentialTerms.isEmpty()){
			dictionnaryMapper.addAllExistentialVariables(newExistentialTerms);  // update the dictionary
			variableGenerator.resetNewGeneratedSymbol();
			
			try { // new existential term found, then map them before insertion
				while(stream.hasNext()) {
					Atom atom = stream.next();
					for(int i=0 ; i<atom.getPredicate().getArity() ; i++ ) {
						Term term = atom.getTerm(i);
						if(term.getIdentifier().toString().startsWith(EXISTENTIAL_VAR_PREFIX)){ // if term is existential then map it before add
							Term newTerm = dictionnaryMapper.mapExistentialVar(term);
							atom.setTerm(i, newTerm);
						}
					}						
					add(atom);
				}
			} catch (IOException e) {
				throw new AtomSetException(e);
			}
			return true;
		}
	
		return super.addAll(stream);  // no new existential term found them simply addAll atom 
	}

}
