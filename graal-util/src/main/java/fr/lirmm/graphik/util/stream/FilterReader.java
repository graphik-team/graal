/**
 * 
 */
package fr.lirmm.graphik.util.stream;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FilterReader<T,U> extends AbstractReader<T> {
	
	private final ObjectReader<U> reader;
	private final Filter filter;
	private T next;

	public FilterReader(ObjectReader<U> reader, Filter filter) {
		this.filter = filter;
		this.reader = reader;
		this.next = null;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if(this.next == null && this.reader.hasNext()) {
			U o = this.reader.next();
			if(this.filter.filter(o)) {
				this.next = (T) o;
			} else {
				this.hasNext();
			}
		}
		return this.next != null;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public T next() {
		this.hasNext();
		T t = this.next;
		this.next = null;
		return t;
	}

	

}
