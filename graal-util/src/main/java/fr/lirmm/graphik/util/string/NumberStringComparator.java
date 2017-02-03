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
package fr.lirmm.graphik.util.string;

import java.io.Serializable;
import java.util.Comparator;

/**
 * find on {@literal http://stackoverflow.com/questions/7270447/java-string-number-comparator}
 */
public class NumberStringComparator implements Comparator<String>, Serializable {
	
	private static final long serialVersionUID = -775069701717974426L;

	@Override
	public int compare(String a, String b) {
	    int la = a.length();
	    int lb = b.length();
	    int ka = 0;
	    int kb = 0;
	    while (true) {
	        if (ka == la)
	            return kb == lb ? 0 : -1;
	        if (kb == lb)
	            return 1;
	        if (a.charAt(ka) >= '0' && a.charAt(ka) <= '9' && b.charAt(kb) >= '0' && b.charAt(kb) <= '9') {
	            int na = 0;
	            int nb = 0;
	            while (ka < la && a.charAt(ka) == '0')
	                ka++;
	            while (ka + na < la && a.charAt(ka + na) >= '0' && a.charAt(ka + na) <= '9')
	                na++;
	            while (kb < lb && b.charAt(kb) == '0')
	                kb++;
	            while (kb + nb < lb && b.charAt(kb + nb) >= '0' && b.charAt(kb + nb) <= '9')
	                nb++;
	            if (na > nb)
	                return 1;
	            if (nb > na)
	                return -1;
	            if (ka == la)
	                return kb == lb ? 0 : -1;
	            if (kb == lb)
	                return 1;

	        }
	        if (a.charAt(ka) != b.charAt(kb))
	            return a.charAt(ka) - b.charAt(kb);
	        ka++;
	        kb++;
	    }
	}

}
