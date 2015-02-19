/**
 * 
 */
package fr.lirmm.graphik.util;


import java.net.URISyntaxException;

import org.openrdf.model.util.URIUtil;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class URIUtils {
	
	private URIUtils() {}

	public static boolean isValidURI(String uri) {
		return URIUtil.isValidURIReference(uri);
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
			org.openrdf.model.URI tmp = new org.openrdf.model.impl.URIImpl(uri);
			return tmp.getLocalName();
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
			org.openrdf.model.URI tmp = new org.openrdf.model.impl.URIImpl(uri);
			return tmp.getNamespace();
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
		Prefix prefix = Prefix.getPrefix(URIUtils.getPrefix(string));
		if(prefix.getPrefix().equals("")) {
			prefix = defaultPrefix;
		}
		String localname = URIUtils.getLocalName(string);
		return new DefaultURI(prefix, localname);
	}

	
}

