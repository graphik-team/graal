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

import fr.lirmm.graphik.dlgp2.parser.Token;
import fr.lirmm.graphik.graal.api.io.ParseException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class DlgpParseException extends ParseException {

	private static final long serialVersionUID = -2533386859342390391L;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	DlgpParseException(Throwable t) {
		super(t);
	}
	
	DlgpParseException(String msg, Throwable t) {
		super(msg, t);
	}

	DlgpParseException(fr.lirmm.graphik.dlgp2.parser.ParseException e) {
		super(createMsg(e), (e.currentToken!=null)?e.currentToken.beginLine:-1, (e.currentToken!=null)?e.currentToken.beginColumn:-1);
	}
	
	DlgpParseException(String message, int line, int column) {
		super(message, line, column);
	}
	
	DlgpParseException(String message) {
		super(message);
	}
	
	private static final String createMsg(fr.lirmm.graphik.dlgp2.parser.ParseException e) {

		Token tok = e.currentToken;
		if(e.currentToken != null) { 
			String extract = "";
			do {
				if (tok.kind == 0) {
					extract += e.tokenImage[0];
					break;
				}
				extract += add_escapes(tok.image);
				tok = tok.next;
			} while (tok != null);
			
			return "a parse exception occured on \"" + extract + "\"";
		} else {
			return e.getMessage();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Used to convert raw characters to their escaped version when these raw
	 * version cannot be used as part of an ASCII string literal.
	 */
	static String add_escapes(String str) {
		StringBuffer retval = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case 0:
				continue;
			case '\b':
				retval.append("\\b");
				continue;
			case '\t':
				retval.append("\\t");
				continue;
			case '\n':
				retval.append("\\n");
				continue;
			case '\f':
				retval.append("\\f");
				continue;
			case '\r':
				retval.append("\\r");
				continue;
			case '\"':
				retval.append("\\\"");
				continue;
			case '\'':
				retval.append("\\\'");
				continue;
			case '\\':
				retval.append("\\\\");
				continue;
			default:
				if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
					String s = "0000" + Integer.toString(ch, 16);
					retval.append("\\u" + s.substring(s.length() - 4, s.length()));
				} else {
					retval.append(ch);
				}
				continue;
			}
		}
		return retval.toString();
	}

}
