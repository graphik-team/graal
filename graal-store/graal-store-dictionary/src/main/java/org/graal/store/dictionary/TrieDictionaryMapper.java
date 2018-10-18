package org.graal.store.dictionary;



import java.util.Map;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * DictionnaryMapper implementend with {@link PatriciaTrie}
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class TrieDictionaryMapper extends DictionaryMapper{
	
	/**
	 * termIds index implemented as patriciaTrie
	 */
	private PatriciaTrie<Integer> termIds;
	
	public TrieDictionaryMapper() {
		super();
		termIds = new PatriciaTrie<>();
	}

	@Override
	public Integer getIntegerIdOf(String termURI) {
		return termIds.get(termURI);
	}

	@Override
	public void buildDictionary() {
		int objIdx = 0;
		for(int i=0 ; i < identifiers.size() ; i++) {
			termIds.put(identifiers.get(i), dataTypes.get(i)); 
		}
		
		identifiers.clear();
		dataTypes.clear();
		
		MapIterator<String,Integer> it = termIds.mapIterator();
		while(it.hasNext()) {
			String key = it.next();
			Integer value = it.getValue();
			identifiers.add(key);
			dataTypes.add(value);
			it.setValue(objIdx++); 
		}
		existentialBeginIdx = termIds.size();
	}

	

	@Override
	public Map<String, Integer> getIdentifierDictionary() {
		return termIds;
	}

	
}

