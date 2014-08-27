package fr.lirmm.graphik.graal.core;


public interface Query /*extends Iterable<Atom>*/ {

	/**
	 * @return true if the expected answer is boolean, false otherwise.
	 */
	public boolean isBoolean();

//	@Override
//	public Iterator<Atom> iterator();

};

