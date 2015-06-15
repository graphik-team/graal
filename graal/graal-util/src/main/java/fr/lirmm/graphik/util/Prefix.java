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
package fr.lirmm.graphik.util;


/**
 * Immutable
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Prefix {

	public static final Prefix XSD = new Prefix("xsd",
			"http://www.w3.org/2001/XMLSchema#");
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	private String prefixName;
	private String prefix;
	
	public Prefix(String prefixName, String prefix) {
		this.prefixName = prefixName;
		this.prefix = prefix;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public String getPrefixName() {
		return this.prefixName;
	}
	
	public String getPrefix() {
		return prefix;
	}
		
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		return "prefix[" + this.prefixName + ", " + this.prefix + "]";
	}
}
