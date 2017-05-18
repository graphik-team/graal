/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.api.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractAtom implements Atom {
	
	@Override
	public Set<Constant> getConstants() {
		Set<Constant> typedTerms = new HashSet<Constant>();
		for (Term term : this.getTerms())
			if (term.isConstant())
				typedTerms.add((Constant) term);

		return typedTerms;
	}
	
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> typedTerms = new HashSet<Variable>();
		for (Term term : this.getTerms())
			if (term.isVariable())
				typedTerms.add((Variable)term);

		return typedTerms;
	}
	
	@Override
	public Set<Literal> getLiterals() {
		Set<Literal> typedTerms = new HashSet<Literal>();
		for (Term term : this.getTerms())
			if (term.isLiteral())
				typedTerms.add((Literal)term);

		return typedTerms;
	}
    
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
		this.appendTo(sb);
		return sb.toString();
	}
	
	/**
	 * @param sb
	 */
	public void appendTo(StringBuilder sb) {
		sb.append(this.getPredicate().toString());
		sb.append('(');
		boolean bool = false;
		for (Term term : this) {
			if (bool)
				sb.append(',');
			sb.append(term.toString());
			bool = true;
		}
		sb.append(')');
	}

    @Override
	public Iterator<Term> iterator() {
		return getTerms().iterator();
	}
}
