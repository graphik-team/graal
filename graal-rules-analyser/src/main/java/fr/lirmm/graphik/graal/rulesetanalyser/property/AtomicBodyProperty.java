package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * The body contains only one atom.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class AtomicBodyProperty extends AbstractRuleProperty {

	private static AtomicBodyProperty instance = null;
	
	private AtomicBodyProperty(){}
	
	public static AtomicBodyProperty getInstance() {
		if(instance == null) {
			instance = new AtomicBodyProperty();
		}
		return instance;	
	}
	
	@Override
	public boolean check(Rule rule) {
		Iterator<Atom> it = rule.getBody().iterator();
		if(it.hasNext())
			it.next();
		return !it.hasNext();
	}

}
