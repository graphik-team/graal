package org.graal.store.dictionary;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;



public class DictionaryMappingTest  {


	public static CloseableIterator<Object> getDatas(){
		try {
			return new DlgpParser(new File("src/test/resources/animals.dlp"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void testOrder() throws IteratorException {
		
		DictionaryMapper treeMapDico = new TreeMapDictionaryMapper();
		DictionaryMapper trieDico = new TrieDictionaryMapper();
		
		CloseableIterator<Object> datasIt = getDatas();
		while(datasIt.hasNext()) {
			Object obj = datasIt.next();
			treeMapDico.add(obj);
			trieDico.add(obj);
		}
		treeMapDico.build();
		trieDico.build();
		
		
		Map<String, Integer> treeMapIds = treeMapDico.getIdentifierDictionary();
		ArrayList<String> treeMapTerms = treeMapDico.getIdentifiers();
		
		Map<String, Integer> trieIds =  trieDico.getIdentifierDictionary();
		ArrayList<String>  trieTerms =  trieDico.getIdentifiers();
		
		assertTrue(trieIds.size() == treeMapIds.size());
	
		for(int i=0;i<treeMapTerms.size()-1; i++) {
			
			String termTree=treeMapTerms.get(i);
			String termTree2=treeMapTerms.get(i+1);
			String termTrie=trieTerms.get(i);
			String termTrie2=trieTerms.get(i+1);
						
			assertTrue(treeMapIds.containsKey(termTree));
			assertTrue(treeMapIds.containsKey(termTree2));	
			assertTrue(treeMapIds.get(termTree) < treeMapIds.get(termTree2));
			
			assertTrue(treeMapIds.containsKey(termTrie));
			assertTrue(treeMapIds.containsKey(termTrie2));	
			assertTrue(treeMapIds.get(termTrie) < treeMapIds.get(termTrie2));
			
			assertTrue(trieIds.containsKey(termTrie));
			assertTrue(trieIds.containsKey(termTrie2));	
			assertTrue(trieIds.get(termTrie) < trieIds.get(termTrie2));
			
			assertTrue(trieIds.containsKey(termTree));
			assertTrue(trieIds.containsKey(termTree2));	
			assertTrue(trieIds.get(termTree) < trieIds.get(termTree2));
		}		
	}
	
	@Test
	public void testAllDicoMapping() throws IteratorException {

		CloseableIterator<Object> datasIt = getDatas();
		List<Object> datas = Iterators.toList(datasIt);
		datasIt.close();
		
		DictionaryMapper treeMapDico = new TreeMapDictionaryMapper();
		DictionaryMapper trieDico = new TrieDictionaryMapper();
		
		treeMapDico.addAll(datas);
		trieDico.addAll(datas);
		treeMapDico.build();
		trieDico.build();
		
		for(Object obj : datas) {
			if(obj instanceof Atom) {
				Atom newAtom1 = trieDico.map((Atom) obj);
				Atom newAtom2 = treeMapDico.map((Atom) obj);
				assertEquals(newAtom1, newAtom2);
				
			}
			else if(obj instanceof Rule) {
				Rule newRule1 = treeMapDico.map((Rule) obj);
				Rule newRule2 = trieDico.map((Rule) obj);
				assertEquals(newRule1, newRule2);
			}		
		}
	}
	
	@Test
	public void testUnMapping() throws IteratorException {

		DictionaryMapper trieDico = new TrieDictionaryMapper();
		Atom atom = DlgpParser.parseAtom("p(a,b).");
		Atom atom2 = DlgpParser.parseAtom("q(<rdf:type>,c).");
		Atom atom3 = DlgpParser.parseAtom("<rdf:type>(c,<rdfs:class>).");
		trieDico.add(atom); trieDico.add(atom2); trieDico.add(atom3);
		trieDico.build();
		
		Atom mappedAtom = trieDico.map(atom);
		Atom mappedAtom2 = trieDico.map(atom2);
		Atom mappedAtom3 = trieDico.map(atom3);
		
		Atom unmappedAtom = trieDico.unmap(mappedAtom);
		Atom unmappedAtom2 = trieDico.unmap(mappedAtom2);
		Atom unmappedAtom3 = trieDico.unmap(mappedAtom3);
		
		System.out.println(atom + "    -> " +mappedAtom + " : " +unmappedAtom);
		System.out.println(atom2 + "   -> " +mappedAtom2+ " : " +unmappedAtom2);
		System.out.println(atom3 + "   -> " +mappedAtom3+ " : " +unmappedAtom3);
		
		assert(atom.equals(unmappedAtom));
		assert(atom2.equals(unmappedAtom2));
		assert(atom3.equals(unmappedAtom3));
	}
	
	@Test
	public void testTermsAndPredicateBuild() throws ParseException {
		DictionaryMapper trieDico = new TrieDictionaryMapper();
		Atom atom = DlgpParser.parseAtom("p(a,b).");
		Atom atom2 = DlgpParser.parseAtom("q(<rdf:type>,c).");
		Atom atom3 = DlgpParser.parseAtom("<rdf:type>(c,<rdfs:class>).");
		trieDico.add(atom); trieDico.add(atom2); trieDico.add(atom3);
		trieDico.build();
		
		Collection<Predicate> predicates = trieDico.getAllMappedPredicates();
		System.out.println(predicates);
	}
	
	public void testKBSaturation() {
		
	}
	
}
