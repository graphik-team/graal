package fr.lirmm.graphik.util.collections;

public class Trie<T,V>
{
   private TrieNode<T,V> root;
   
   /**
    * Constructor
    */
   public Trie()
   {
      root = new TrieNode<T,V>();
   }
   
   /**
    * Associates a value to a word
    * 
    * @param value
    * @param word
    */
   @SafeVarargs
   public final V put(V value, T... word)
   {
      return root.put(value, 0, word);
   }
   
   /**
    * Get the value in the Trie with the given
    * word
    * 
    * @param word
    * @return the associated value, or null if there is no value associated.
    */
   @SafeVarargs
   public final V get(T... word)
   {
      return root.get(0, word);
   }
}