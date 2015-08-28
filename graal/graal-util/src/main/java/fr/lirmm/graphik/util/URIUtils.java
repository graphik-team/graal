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
package fr.lirmm.graphik.util;


import java.net.URISyntaxException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class URIUtils {
	
	public static final URI RDF_TYPE = URIUtils.createURI(Prefix.RDF, "type");

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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
	 */
	public static URI createURI(String string, Prefix defaultPrefix) {
		String prefixString = URIUtils.getPrefix(string);
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

		if (separatorIdx < 0) {
			separatorIdx = uri.lastIndexOf('/');
		}

		if (separatorIdx < 0) {
			separatorIdx = uri.lastIndexOf(':');
		}

		if (separatorIdx < 0) {
			throw new IllegalArgumentException(
					"No separator character founds in URI: " + uri);
		}

		return separatorIdx + 1;
	}
	
}

