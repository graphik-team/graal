/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.util.string;

import java.util.Comparator;

/**
 * find on {@literal http://stackoverflow.com/questions/7270447/java-string-number-comparator}
 */
public class NumberStringComparator implements Comparator<String> {
	
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
