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
package fr.lirmm.graphik.graal.trash;

import java.util.Iterator;

import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Counter<T> extends AbstractReader<T> {
	
	//private final Object lock = new Object();
	private Iterator<T> objects;
	private int count = 0;
	
	public Counter() {
	}

	public Counter(Iterator<T> objects) {
		this.objects = objects;
	}
	
	public void setIterator(Iterator<T> objects) {
		this.objects = objects;
	}

	@Override
	public boolean hasNext() {
		return objects != null && this.objects.hasNext();
	}

	@Override
	public T next() {
		++count;
		return this.objects.next();
	}
	
	public int count() {
		return this.count;
	}
	
	
	

}
