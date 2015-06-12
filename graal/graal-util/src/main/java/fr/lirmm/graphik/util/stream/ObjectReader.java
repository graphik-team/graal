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
 package fr.lirmm.graphik.util.stream;

import java.io.IOException;
import java.util.Iterator;

@Deprecated
public interface ObjectReader<T> extends Iterator<T>, Iterable<T> {

	boolean hasNext();
	T next();
	Iterator<T> iterator();
	void read(ObjectWriter<T> writer) throws IOException;
	
}
