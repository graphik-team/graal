package fr.lirmm.graphik.util.stream;

import java.io.IOException;

public interface ObjectWriter<T> {
    
    void write(T object) throws IOException;
	
	void write(Iterable<T> objects) throws IOException;
}
