package fr.lirmm.graphik.util.stream;

import java.io.IOException;
import java.util.Iterator;

public interface ObjectReader<T> extends Iterator<T>, Iterable<T> {

	boolean hasNext();
	T next();
	Iterator<T> iterator();
	void read(ObjectWriter<T> writer) throws IOException;
	
}
