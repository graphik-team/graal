/**
 * 
 */
package fr.lirmm.graphik.kb.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class AtomComparator implements Comparator<Atom>, Serializable {

	private static final long serialVersionUID = 2044427079906743437L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Atom atom0, Atom atom1) {
		int cmpVal = atom0.getPredicate().compareTo(atom1.getPredicate());
        if(cmpVal == 0) 
        {
            List<Term> atom0Terms = atom0.getTerms();
            List<Term> atom1Terms = atom1.getTerms();
            
            cmpVal = (atom0Terms.size() < atom1Terms.size())? -1 : ((atom0Terms.size() == atom1Terms.size())? 0 : 1);
            int i = 0;
            Comparator<Term> cmp = new TermValueComparator();
            while(cmpVal == 0 && i < atom0Terms.size()) {
                cmpVal = cmp.compare(atom0Terms.get(i), atom1Terms.get(i));
                ++i;
            }
        }
        
        return cmpVal;
	}
	
}
