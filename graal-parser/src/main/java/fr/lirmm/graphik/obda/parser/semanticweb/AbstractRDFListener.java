/**
 * 
 */
package fr.lirmm.graphik.obda.parser.semanticweb;

import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractRDFListener extends RDFHandlerBase {

	@Override
	public void handleStatement(Statement st) {
		DefaultAtom a = new DefaultAtom(new Predicate(st.getPredicate().toString(), 2), new Term(
				st.getSubject(), Type.CONSTANT), new Term(st.getObject(),
				Type.CONSTANT));
		
		this.createAtom(a);
	}

	/**
	 * @param a
	 */
	protected abstract void createAtom(DefaultAtom a);
}
