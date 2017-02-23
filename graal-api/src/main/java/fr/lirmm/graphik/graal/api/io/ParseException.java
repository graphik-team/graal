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
 /**
 * 
 */
package fr.lirmm.graphik.graal.api.io;

import fr.lirmm.graphik.util.stream.IteratorException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ParseException extends IteratorException {

	private static final long serialVersionUID = -4455111019098315998L;
	private int line = -1;
	private int column = -1;
	
	/**
	 * @param message
	 * @param e
	 */
	public ParseException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * @param message
	 */
	public ParseException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 */
	public ParseException(String message, int line, int column) {
		super((line >= 0 && column >= 0)?"line " + line + " column " + column + ": " + message:message);
		this.line = line;
		this.column = column;
	}

	public ParseException(Throwable e) {
		super(e);
	}
	
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}

}
