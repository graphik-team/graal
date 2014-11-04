package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;

/**
 * All atoms in the head contain either all or none variables from the body.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class DomainRestrictedProperty extends AbstractRuleProperty {

	private static DomainRestrictedProperty instance = null;
	
	private DomainRestrictedProperty(){}
	
	public static synchronized DomainRestrictedProperty getInstance() {
		if(instance == null) {
			instance = new DomainRestrictedProperty();
		}
		return instance;	
	}
	
	@Override
	public Boolean check(Rule rule) {
		boolean none;
		boolean all;

		Set<Term> bodyVars = rule.getBody().getTerms(Term.Type.VARIABLE);

		for (Atom a : rule.getHead()) {
			all = none = true;
			for (Term t : bodyVars) {
				if (a.getTerms().contains(t)) {
					none = false;
				} else {
					all = false;
				}
				if (!none && !all) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String getLabel() {
		return "dr";
	}

};
