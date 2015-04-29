/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.List;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.term.Term;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractAtom implements Atom {
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getPredicate() == null) ? 0 : this.getPredicate().hashCode());
        for(Term t : this.getTerms()) {
            result = prime*result + t.hashCode();
        }
        return result;
    }

    /**
     * Verifies if two atoms are equivalent or not.
     */
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof Atom)) { return false; }
        Atom other = (Atom) obj;
        if (this.getPredicate() == null) {
            if (other.getPredicate() != null) { return false; }
        }
        else if (!this.getPredicate().equals(other.getPredicate())) { return false; }
        if (this.getTerms() == null) {
            if (other.getTerms() != null) { return false; }
        }
        else {
            if (!(this.getTerms().size() == other.getTerms().size())) { return false;  }
            for (int i=0;i<this.getTerms().size();i++){
                if (!this.getTerm(i).equals(other.getTerm(i))) { return false; }
            }
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Atom other) {
        int cmpVal = this.getPredicate().compareTo(other.getPredicate());
        if(cmpVal == 0) 
        {
            List<Term> thisTerms = this.getTerms();
            List<Term> otherTerms = other.getTerms();
            
            cmpVal = (thisTerms.size() < otherTerms.size())? -1 : ((thisTerms.size() == otherTerms.size())? 0 : 1);
            int i = 0;
            while(cmpVal == 0 && i < thisTerms.size()) {
                cmpVal = thisTerms.get(i).compareTo(otherTerms.get(i));
                ++i;
            }
        }
        
        return cmpVal;
    }
    
    /**
	 * Returns a string in the form p(t1,...,tk)
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.toString(sb);
		return sb.toString();
	}
	
	/**
	 * @param sb
	 */
	public void toString(StringBuilder sb) {
		sb.append(this.getPredicate().toString());
		sb.append('(');
		boolean bool = false;
		for (Term term : this.getTerms()) {
			if (bool)
				sb.append(',');
			sb.append(term);
			bool = true;
		}
		sb.append(')');
	}

    @Override
	public Iterator<Term> iterator() {
		return getTerms().iterator();
	}
}
