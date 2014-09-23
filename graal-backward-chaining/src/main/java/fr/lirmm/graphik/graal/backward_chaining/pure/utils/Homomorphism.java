/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * A simple implementation of an algorithm to find if there exist an
 * homomorphism between two facts Backtrack algorithm that look for an
 * association of atoms that correspond to a substitution of terms efficient for
 * simple facts of small size
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class Homomorphism {

	private AtomSet query;// the query that we want project on the fact
	private AtomSet fact; // the fact on which we want project the query
	private RulesCompilation compilation;// an order on predicate

	// attribute for homomorphism computation
	private ArrayList<Atom> sourceAtoms;
	private Map<Atom, Collection<Atom>> availableImage = new HashMap<Atom, Collection<Atom>>();
	private Map<Atom, Integer> currentImages = new HashMap<Atom, Integer>();
	private Map<Atom, LinkedList<Term>> firstOccurence = new HashMap<Atom, LinkedList<Term>>();
	private Map<Term, Term> currentSubstitution = new HashMap<Term, Term>();

	public Homomorphism(AtomSet source, AtomSet target,
			RulesCompilation compilation) {
		this.query = source;
		this.fact = target;
		this.compilation = compilation;

	}

	public Homomorphism(AtomSet source, AtomSet target) {
		this.query = source;
		this.fact = target;
		this.compilation = null;
	}

	/**
	 * Return the target fact
	 */
	public AtomSet getFact() {
		return fact;
	}

	/**
	 * Return the source fact called query
	 */
	public AtomSet getQuery() {
		return query;
	}

	/**
	 * return true iff exist an homomorphism from the query to the fact else
	 * return false
	 */
	public boolean existHomomorphism() {
		int level = 0;
		boolean foundImage;
		boolean hasNextImage;

		// check if the query is empty
		if (query == null || query.isEmpty()) {
			System.out.println("requete vide");
			return true;
		}

		if (!initialiseHomomorphism())
			return false;

		// can backtrack and all the atom have not been associate
		while (level >= 0 && level < sourceAtoms.size()) {
			Atom currentAtom = sourceAtoms.get(level);
			hasNextImage = chooseNextImage(currentAtom);
			if (hasNextImage) {// try next image
				foundImage = checkCurrentImage(currentAtom);
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
	 * Initialise attribute for homomorphism
	 */
	private boolean initialiseHomomorphism() {
		sourceAtoms = new ArrayList<Atom>();
		for (Atom a : query) {
			sourceAtoms.add(a);
		}
		computeFirstOccurence();
		return initialiseAvailableImage();
	}

	/**
	 * return the current image of the given atom
	 */
	private Atom getImage(Atom atom) {
		Integer numCurrentImage = currentImages.get(atom);
		LinkedList<Atom> images = (LinkedList<Atom>) availableImage.get(atom);
		return images.get(numCurrentImage);
	}

	/**
	 * Return true if the current image of the given atom is possible and
	 * compute the current substitution else return false
	 */
	private boolean checkCurrentImage(Atom atom) {
		Atom image = getImage(atom);
		List<Term> sourceTerms = atom.getTerms();
		List<Term> targetTerms = image.getTerms();
		for (int i = 0; i < atom.getPredicate().getArity(); i++) {
			Term currentImage = currentSubstitution.get(sourceTerms.get(i));
			// if the current image is no null and different of the target term
			// or if the source term is a constant différent than the target one
			if ((currentImage != null && !currentImage.equals(targetTerms
					.get(i)))
					|| (sourceTerms.get(i).isConstant() && !sourceTerms.get(i)
							.equals(targetTerms.get(i)))) { // incompatible
															// image
				resetSubstitution(atom);
				return false;
			} else { // possible image
				currentSubstitution.put(sourceTerms.get(i), targetTerms.get(i));
			}
		}
		return true;
	}

	/**
	 * Reset substitution for term that first occur in the given atom Check if
	 * the given atom has a next image available if it has : increment
	 * currentImage for the given atom and return true if has not : reset
	 * current image for the given atom and return false
	 */
	private boolean chooseNextImage(Atom atom) {
		Integer numCurrentImage = currentImages.get(atom);
		if (numCurrentImage != null) {
			resetSubstitution(atom);
			numCurrentImage++;
		} else {
			numCurrentImage = 0;
		}
		// if there is a next image in the available image
		if (numCurrentImage < availableImage.get(atom).size()) {
			currentImages.put(atom, numCurrentImage);
			return true;
		} else {
			currentImages.put(atom, -1);
			return false;
		}
	}

	/**
	 * Compute the associations between an atom and the terms that first occur
	 * in this atom i.e. for which the substitution must be reset when the image
	 * of the atom change
	 */
	private void computeFirstOccurence() {
		LinkedList<Term> alreadySeen = new LinkedList<Term>();
		for (Atom a : sourceAtoms) {
			firstOccurence.put(a, new LinkedList<Term>());
			for (Term t : a.getTerms()) {
				// if this is the first occurence of this term
				if (!alreadySeen.contains(t)) {
					alreadySeen.add(t);
					// note that its affectation come from this atom
					firstOccurence.get(a).add(t);
				}
			}
		}
	}

	/**
	 * reset the substitution of terms that need when the given atom change it
	 * image
	 * 
	 * @param atom
	 */
	private void resetSubstitution(Atom atom) {
		for (Term t : firstOccurence.get(atom)) {
			currentSubstitution.put(t, null);
		}
	}

	/**
	 * Found the possible image of each atom in the source fact into the atoms
	 * of the target fact
	 */
	private boolean initialiseAvailableImage() {
		Collection<Atom> images;
		Iterator<Atom> i = sourceAtoms.iterator();
		while (i.hasNext()) {
			Atom a = i.next();
			if (availableImage.containsKey(a))
				i.remove();
			else {
				images = new LinkedList<Atom>();
				for (Atom im : fact)
					if (isMappable(a, im))
						images.add(im);
				// images.addAll(predicateOrder.getHighterAtomWithPredicate(im,
				// a.getPredicate()));
				if (images.isEmpty())
					return false;
				availableImage.put(a, images);
			}
		}
		return true;
	}

	private boolean isMappable(Atom a, Atom im) {
		if (compilation != null) {
			return compilation.isMappable(a, im);
		} else
			return a.getPredicate().equals(im.getPredicate());
	}
}
