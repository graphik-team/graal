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
package fr.lirmm.graphik.graal.api.forward_chaining;

import fr.lirmm.graphik.graal.api.util.TimeoutException;
import fr.lirmm.graphik.util.profiler.Profilable;



/**
 * This interface represents a chase (forward chaining) algorithm seen as an
 * iterative process.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Chase extends Profilable {

	/**
	 * Calls next() until hasNext() return false.
	 */
	public void execute() throws ChaseException;
	
	/**
	 * Calls next() until hasNext() return false. If the method execution time exceed the specified timeout (in milliseconds) then 
	 * the method will be interrupted and a {@link TimeoutException} will be throw. A timeout of 0 means to wait forever.
	 * @throws TimeoutException 
	 * @throws ChaseException 
	 */
	public void execute(long timeout) throws ChaseException, TimeoutException;
	
	/**
	 * Execute the next step of the saturation process.
	 * 
	 * @throws ChaseException
	 */
	public void next() throws ChaseException;
	
	/**
	 * 
	 * @return true if the saturation process needs other steps.
	 */
	public boolean hasNext();
	
}
