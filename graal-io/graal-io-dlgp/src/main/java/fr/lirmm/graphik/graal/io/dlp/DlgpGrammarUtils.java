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

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DlgpGrammarUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private DlgpGrammarUtils() {

	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Check if the string fulfill u-ident condition
	 * 
	 * @param s
	 * @return true if the specified string fulfill u-ident condition
	 */
	public static boolean checkUIdent(String s) {
		return !s.isEmpty() && isUpperAlpha(s.charAt(0)) && containsOnlySimpleChar(s);
	}
	
	/**
	 * Check if the string fulfill l-ident condition
	 * 
	 * @param s
	 * @return true if the specified string fulfill l-ident condition
	 */
	public static boolean checkLIdent(String s) {
		return !s.isEmpty() && isLowerAlpha(s.charAt(0)) && containsOnlySimpleChar(s);
	}
	
	public static final int FIRST_CHAR = 0;
	public static final int CHARS_BASE = 1;
	public static final int PERCENT_1 = 2;
	public static final int PERCENT_2 = 3;
	public static final int LOCAL_ESC = 4;
	/**
	 * Check if the string fulfill LocalName condition (see
	 *         turtle grammar https://www.w3.org/TR/turtle/)
	 * @param localname
	 * @return true iff the specified string fulfill localName condition
	 */
	public static boolean checkLocalName(String localname) {
		int mode = FIRST_CHAR;
		int last_mode = mode;
		char last = '\0';
		for (char c : localname.toCharArray()) {
			last = c;
			last_mode = mode;
			switch (mode) {
			case PERCENT_1:
				if (!isHex(c)) {
					return false;
				}
				mode = PERCENT_2;
				break;
			case PERCENT_2:
				if (!isHex(c)) {
					return false;
				}
				mode = CHARS_BASE;
				break;
			case LOCAL_ESC:
				if (!isLocalEscape(c)) {
					return false;
				}
				mode = CHARS_BASE;
				break;
			case FIRST_CHAR:
				if (c == '%') {
					mode = PERCENT_1;
				} else if (c == '\\') {
					mode = LOCAL_ESC;
				} else if (c == ':' || isCharsU(c) || isDigit(c)) {
					mode = CHARS_BASE;
				} else {
					return false;
				}
				break;
			case CHARS_BASE:
				if (c == '%') {
					mode = PERCENT_1;
				} else if (c == '\\') {
					mode = LOCAL_ESC;
				} else if (c != ':' && c != '.' && !isChars(c)) {
					return false;
				}
				break;
			default:
				assert false;
				break;
			}
		}

		return (last_mode == FIRST_CHAR && (last == ':' || isCharsU(last) || isDigit(last)))
				|| (last_mode == CHARS_BASE && (last == ':' || isChars(last)))
				|| (last_mode == PERCENT_2 && isHex(last)) || (last_mode == LOCAL_ESC && isLocalEscape(last));

	}
	
	/**
	 * Check if the string contains only simple char (a-z A-Z 0-9 _)
	 * 
	 * @param s
	 * @return true if the string contains only simple characters, false otherwise.
	 */
	private static boolean containsOnlySimpleChar(String s) {
		for(char c : s.toCharArray()) {
			if(!isSimpleChar(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isCharsBase(char c) {
		return !((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '\u00C0' || c > '\u00D6')
				&& (c < '\u00D8' || c > '\u00F6') && (c < '\u00F8' || c > '\u02FF') && (c < '\u0370' || c > '\u037D')
				&& (c < '\u037F' || c > '\u1FFF') && (c < '\u200C' || c > '\u200D') && (c < '\u2070' || c > '\u218F')
				&& (c < '\u2C00' || c > '\u2FEF') && (c < '\u3001' || c > '\uD7FF') && (c < '\uF900' || c > '\uFDCF')
				&& (c < '\uFDF0' || c > '\uFDF0') /* TODO [#x10000-#xEFFFF] */ );
	}

	public static boolean isCharsU(char c) {
		return c == '_' || isCharsBase(c);
	}

	public static boolean isChars(char c) {
		return c == '-' || isCharsU(c) || isDigit(c) || c == '\u00B7' || (c >= '\u0300' && c <= '\u036F')
				|| (c >= '\u203F' && c <= '\u2040');
	}

	public static boolean isHex(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	}

	public static boolean isLocalEscape(char c) {
		return c == '_' || c == '~' || c == '.' || c == '-' || c == '!' || c == '$' || c == '&' || c == '\'' || c == '('
				|| c == ')' || c == '*' || c == '+' || c == ',' || c == ';' || c == '=' || c == '/' || c == '?'
				|| c == '#' || c == '@' || c == '%';
	}
	
	public static boolean isSimpleChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
		|| (c >= '0' && c <= '9') || (c == '_');
	}
	
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isUpperAlpha(char c) {
		return (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isLowerAlpha(char c) {
		return (c >= 'a' && c <= 'z');
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
