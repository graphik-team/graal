package fr.lirmm.graphik.graal.core.atomset;

public class AtomSetException extends Exception {

	private static final long serialVersionUID = -7793681455338699527L;

	public AtomSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public AtomSetException(String message) {
		super(message);
	}
	
	public AtomSetException(Throwable e) {
		super(e);
	}
}
