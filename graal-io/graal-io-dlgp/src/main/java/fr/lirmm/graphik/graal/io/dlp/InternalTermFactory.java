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
package fr.lirmm.graphik.graal.io.dlp;

import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

import fr.lirmm.graphik.dlgp2.parser.TermFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;

class InternalTermFactory implements TermFactory {
	
	UnicodeUnescaper unescaper = new UnicodeUnescaper();

	@Override
	public Object createIRI(String s) {
		if (s.indexOf(':') == -1) {
			return decode(s);
		}
		return new DefaultURI(decode(s));
	}

	@Override
	public Object createLiteral(Object datatype, String stringValue,
			String langTag) {
		if (langTag != null) {
			stringValue += "@" + langTag;
		}
		return DefaultTermFactory.instance().createLiteral((URI) datatype,
				stringValue);
	}

	@Override
	public Object createVariable(String stringValue) {
		return DefaultTermFactory.instance().createVariable(stringValue);
	}
	
	
	 /**
     * Unescapes a string that contains unicode escape sequences \\uXXXX and \\UXXXXXXXX.
     * 
     * @param st A string optionally containing unicode escape sequences.
     * @return The translated string.
     */
    private String decode(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
               
                switch (nextChar) {
                // Hex Unicode: u????
                case 'u':
                    if (i >= st.length() - 5) {
                        ch = 'u';
                        break;
                    }
                    int code1 = Integer.parseInt(st.substring(i + 2, i + 6), 16);
                    sb.append(Character.toChars(code1));
                    i += 5;
                    continue;
                case 'U':
                    if (i >= st.length() - 9) {
                        ch = 'U';
                        break;
                    }
                    int code2 = Integer.parseInt(st.substring(i + 2, i + 10), 16);
                    sb.append(Character.toChars(code2));
                    i += 9;
                    continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}