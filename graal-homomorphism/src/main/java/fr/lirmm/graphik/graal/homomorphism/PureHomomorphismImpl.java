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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

class PureHomomorphismImpl extends AbstractProfilable {

	private static final Logger LOGGER = LoggerFactory.getLogger(PureHomomorphismImpl.class);

	// attribute for homomorphism computation
	private ArrayList<Atom> source;
	private AtomSet target;
	private RulesCompilation compilation;

	private ArrayList<Integer> currentImagesPerLevel;
	private ArrayList<List<Substitution>> availableImage;

	private ArrayList<Substitution> currentSubstitutionPerLevel;
	
	private final Substitution initialSubstitution;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public PureHomomorphismImpl(InMemoryAtomSet source, AtomSet target, RulesCompilation compilation, Substitution s) {
		this.compilation = compilation;
		this.target = target;
		this.source = new ArrayList<Atom>();

		CloseableIteratorWithoutException<Atom> it = source.iterator();
		while (it.hasNext()) {
			this.source.add(it.next());
		}
		int size = this.source.size();

		this.availableImage = new ArrayList<List<Substitution>>(size);
		this.currentImagesPerLevel = new ArrayList<Integer>(size);
		this.currentSubstitutionPerLevel = new ArrayList<Substitution>(size + 1);

		for (int i = 0; i < size; ++i) {
			this.currentImagesPerLevel.add(-1);
			this.availableImage.add(null);
			this.currentSubstitutionPerLevel.add(null);
		}
		this.currentSubstitutionPerLevel.add(null);
		this.currentSubstitutionPerLevel.set(0, new TreeMapSubstitution());
		
		this.initialSubstitution = s;

	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return true if there exist a homomorphism.
	 * @throws HomomorphismException
	 */
	public boolean exist() throws HomomorphismException  {
		// check if the query is empty
		if (source == null || !source.iterator().hasNext()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Empty query");
			}
			return true;
		}

		// /////////////////////////////////////////////////////////////////////
		// Initialisation
		Profiler profiler = this.getProfiler();
		profiler.start("preprocessing time");
		boolean res = this.initialiseHomomorphism();
		profiler.stop("preprocessing time");

		if (res) {
			profiler.start("backtracking time");
			res = this.backtrack();
			profiler.stop("backtracking time");
		}
		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Initialise attribute for homomorphism
	 * 
	 * @throws HomomorphismException
	 */
	protected boolean initialiseHomomorphism() throws HomomorphismException {
		try {
			return initialiseAvailableImage(this.target);
		} catch (IteratorException e) {
			throw new HomomorphismException("An errors occurs while initializing available images for homomorphism",
			                                e);
		}
	}

	protected boolean backtrack() {
		int level = 0;
		boolean foundImage;
		Substitution nextCandidate;

		// can backtrack and all the atom have not been associate
		while (level >= 0 && level < this.source.size()) {
			nextCandidate = this.getNextCandidate(level);
			if (nextCandidate != null) {// try next candidate
				foundImage = this.checkCurrentCandidate(level, nextCandidate);
				if (foundImage) {// need go to the next atom
					level++;
				}
			} else { // need backtrack
				level--;
			}
		}
		return !(level < 0);
	}

	/**
	 * Return true if the current image of the given atom is possible and
	 * compute the current substitution else return false
	 * 
	 */
	protected boolean checkCurrentCandidate(int level, Substitution candidate) {
		Substitution newSub = Substitutions.add(currentSubstitutionPerLevel.get(level), candidate);
		if (newSub != null) {
			this.currentSubstitutionPerLevel.set(level + 1, newSub);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the given atom has a next image available if it has : increment
	 * currentImage for the given atom and return true if has not : reset
	 * current image for the given atom and return false
	 */
	protected Substitution getNextCandidate(int level) {
		Integer numCurrentImage = this.currentImagesPerLevel.get(level);
		numCurrentImage++;

		// if there is a next image in the available image
		List<Substitution> images = this.availableImage.get(level);
		if (numCurrentImage < images.size()) {
			this.currentImagesPerLevel.set(level, numCurrentImage);
			return images.get(numCurrentImage);
		} else {
			this.currentImagesPerLevel.set(level, -1);
			return null;
		}
	}

	/**
	 * Found the possible image of each atom in the source fact into the
	 * atoms of the target fact
	 * 
	 * @throws IteratorException
	 */
	protected boolean initialiseAvailableImage(CloseableIterable<Atom> target)
	    throws IteratorException {
		for (int i = 0; i < this.source.size(); ++i) {
			Atom atomSource = this.source.get(i);
			List<Substitution> images = new ArrayList<Substitution>();
			CloseableIterator<Atom> it = target.iterator();
			while (it.hasNext()) {
				Atom im = it.next();
				for (Substitution s : this.compilation.homomorphism(atomSource, im, this.initialSubstitution)) {
					images.add(s);
				}
			}
			it.close();
			if (images.isEmpty()) {
				return false;
			}
			this.availableImage.set(i, images);
		}
		return true;
	}

}