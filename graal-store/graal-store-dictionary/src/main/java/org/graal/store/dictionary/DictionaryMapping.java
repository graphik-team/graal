package org.graal.store.dictionary;


import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;

/**
 * Dictionary mapping interface
 * @author renaud  colin
 * @author mathieu dodard
 *
 */
public interface DictionaryMapping {
	
	
	/**
	 * @param termURI
	 * @return the integer id associated to a termURI
	 */
	Integer getIntegerIdOf(String termURI);
	
	/**
	 * 
	 * @param termId
	 * @return the termURI associated to integer id
	 */
	public String getStringIdOf(int termId);
	
	
	/**
	 * 
	 * @param predicate
	 * @return the integer id associated to predicate
	 * @throws DictionnaryMappingException if no integer id is associated to predicate
	 */
	public Integer getPredicateId(Predicate predicate) throws DictionnaryMappingException;
	
	/**
	 * 
	 * @param term
	 * @return the integer id associated to term
	 * @throws DictionnaryMappingException if no integer id is associated to term
	 */
	public Integer getTermId(Term term) throws DictionnaryMappingException ;
	
	
	
}
