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
package fr.lirmm.graphik.util;


import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class URIUtils {
	
	public static final URI RDF_TYPE = URIUtils.createURI(Prefix.RDF, "type");
	public static final URI RDF_LANG_STRING = URIUtils.createURI(Prefix.RDF, "langString");

	// PRIMITIVE XSD DATATYPES
	public static final URI XSD_STRING = URIUtils.createURI(Prefix.XSD, "string");
	public static final URI XSD_BOOLEAN = URIUtils.createURI(Prefix.XSD, "boolean");
	public static final URI XSD_DECIMAL = URIUtils.createURI(Prefix.XSD, "decimal");
	public static final URI XSD_FLOAT = URIUtils.createURI(Prefix.XSD, "float");
	public static final URI XSD_DOUBLE = URIUtils.createURI(Prefix.XSD, "double");
	public static final URI XSD_DURATION = URIUtils.createURI(Prefix.XSD, "duration");
	public static final URI XSD_DATE_TIME = URIUtils.createURI(Prefix.XSD, "dateTime");
	public static final URI XSD_TIME = URIUtils.createURI(Prefix.XSD, "time");
	public static final URI XSD_DATE = URIUtils.createURI(Prefix.XSD, "date");
	public static final URI XSD_G_YEAR_MONTH = URIUtils.createURI(Prefix.XSD, "gYearMonth");
	public static final URI XSD_G_YEAR = URIUtils.createURI(Prefix.XSD, "gYear");
	public static final URI XSD_G_MONTH_DAY = URIUtils.createURI(Prefix.XSD, "gMonthDay");
	public static final URI XSD_G_DAY = URIUtils.createURI(Prefix.XSD, "gDay");
	public static final URI XSD_G_MONTH = URIUtils.createURI(Prefix.XSD, "gMonth");
	public static final URI XSD_HEX_BINARY = URIUtils.createURI(Prefix.XSD, "hexBinary");
	public static final URI XSD_BASE64_BINARY = URIUtils.createURI(Prefix.XSD, "base64Binary");
	public static final URI XSD_ANY_URI = URIUtils.createURI(Prefix.XSD, "anyURI");
	public static final URI XSD_Q_NAME = URIUtils.createURI(Prefix.XSD, "QName");
	public static final URI XSD_NOTATION = URIUtils.createURI(Prefix.XSD, "NOTATION");
	
	// DERIVED XSD DATATYPES
	public static final URI XSD_INTEGER = URIUtils.createURI(Prefix.XSD, "integer");

	// OTHERS
	public static final Pattern LITERAL_PATTERN = Pattern.compile("\"(.*)\"\\^\\^<(.*)>");;

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	private URIUtils() {}

	public static boolean isValidURI(String uriRef) {
		boolean isValid = !uriRef.matches("[\u0000-\u001F\u007F-\u009F]");
		try {
			final java.net.URI uri = new java.net.URI(uriRef);
			isValid = uri.isAbsolute();
		}
		catch (URISyntaxException e) {
			isValid = false;
		}
		return isValid;
	}
	
	/**
	 * Return the scheme part of this uri. 
	 * URI = <scheme>:<scheme-specific-part>
	 * @param uri
	 * @return the scheme part of the specified URI.
	 */
	public static String getScheme(String uri) {
		java.net.URI tmp;
		try {
			tmp = new java.net.URI(uri);
		} catch (URISyntaxException e) {
			return "";
		}
		return tmp.getScheme();
	}
	
	/**
	 * Return the local name of this uri.
	 * The local name is computed as follow :
     *  Split after the first occurrence of the '#' character,
     *  If this fails, split after the last occurrence of the '/' character,
     *  If this fails, split after the last occurrence of the ':' character. 
     *  return the second part of this split.
     *  
	 * @param uri
	 * @return the LocalName of the specified URI.
	 */
	public static String getLocalName(String uri) {
		try {
			int localNameIdx = URIUtils.getLocalNameIndex(uri);
			return uri.substring(localNameIdx);
		} catch (IllegalArgumentException e) {
			return uri;
		}
	}
	
	/**
	 * Return the global name of this uri.
	 * The global name is computed as follow :
     *  Split after the first occurrence of the '#' character,
     *  If this fails, split after the last occurrence of the '/' character,
     *  If this fails, split after the last occurrence of the ':' character. 
     *  return the first part of this split.
     *  
	 * @param uri
	 * @return the prefix of the specified URI.
	 */
	public static String getPrefix(String uri) {
		try {
			int localNameIdx = URIUtils.getLocalNameIndex(uri);
			return uri.substring(0, localNameIdx);
		} catch (IllegalArgumentException e) {
			return "";
		}
	}
	
	/**
	 * Create an URI from a String. If the string does not contains a prefix, 
	 * defaultPrefix will be used.
	 * 
	 * @param string
	 * @param defaultPrefix
	 * @return an new URI
	 */
	public static URI createURI(String string, Prefix defaultPrefix) {
		String prefixString = URIUtils.getPrefix(string);
		if (prefixString.isEmpty()) {
			prefixString = defaultPrefix.getPrefix();
		}
		String localname = URIUtils.getLocalName(string);
		return new DefaultURI(prefixString, localname);
	}
	
	public static URI createURI(Prefix prefix, String localname) {
		return new DefaultURI(prefix.getPrefix(), localname);
	}
	
	public static URI createURI(String uri) {
		return new DefaultURI(uri);
	}

	public static int getLocalNameIndex(String uri) {
		int separatorIdx = uri.indexOf('#');

		int tmp;
		tmp = uri.lastIndexOf('/');
		if (tmp > separatorIdx)
			separatorIdx = tmp;

		tmp = uri.lastIndexOf(':');
		if (tmp > separatorIdx)
			separatorIdx = tmp;

		if (separatorIdx < 0) {
			throw new IllegalArgumentException(
					"No separator character founds in URI: " + uri);
		}

		return separatorIdx + 1;
	}
	
}

