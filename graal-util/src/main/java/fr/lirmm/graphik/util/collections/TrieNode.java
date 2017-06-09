package fr.lirmm.graphik.util.collections;

import java.util.HashMap;
import java.util.Map;

class TrieNode<T, V> {
	
	private Map<T,TrieNode<T,V>> children;
	private boolean isLeaf; // quick way to check if any children exist
	private V value; // the associated value to this word, if exist.

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public TrieNode() {
		children = new HashMap<T,TrieNode<T,V>>();
		isLeaf = true;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Adds a word to this node. This method is called recursively and adds
	 * child nodes for each successive letter in the word, therefore recursive
	 * calls will be made with partial words.
	 * 
	 * @param word
	 *            the word to add
	 */
	@SafeVarargs
	public final V put(V value, int index, T... word) {
		if (word.length <= index) {
			V tmp = this.value;
			this.value = value;
			return tmp;
		} else {
			isLeaf = false;
			T charac = word[index];
			TrieNode<T,V> child = children.get(charac);
			if (child == null) {
				child = new TrieNode<T,V>();
				children.put(charac, child);
			}
			return child.put(value, index + 1, word);
		}
	}

	/**
	 * 
	 * @return the associated value to the specified word.
	 */
	@SafeVarargs
	public final V get(int index, T... word) {
		if (word.length <= index) {
			return this.value;
		} else {
			if(isLeaf) {
				return null;
			} else {
				T charac = word[index];
				TrieNode<T,V> child = children.get(charac);
				if(child == null) {
					return null;
				} else {
					return child.get(index + 1, word);
				}
			}
		}
	}


}