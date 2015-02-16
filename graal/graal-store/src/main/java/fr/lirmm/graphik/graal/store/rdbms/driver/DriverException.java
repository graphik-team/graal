package fr.lirmm.graphik.graal.store.rdbms.driver;


public class DriverException extends Exception {

	private static final long serialVersionUID = -8060290615270641168L;

	public DriverException(String message, Exception e) {
		super(message, e);
	}

}
