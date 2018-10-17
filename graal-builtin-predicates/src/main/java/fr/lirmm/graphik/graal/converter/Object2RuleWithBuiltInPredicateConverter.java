package fr.lirmm.graphik.graal.converter;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.BuiltInPredicateSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleWithBuiltInPredicate;
import fr.lirmm.graphik.graal.core.DefaultRuleWithBuiltInPredicates;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

/**
 * The class can detect if an input rule contains a built-in predicates, and if
 * so it convert it in a built-in predicate rule.
 * 
 * @author Olivier Rodriguez
 */
public class Object2RuleWithBuiltInPredicateConverter implements Converter<Object, Object> {
	BuiltInPredicateSet btpredicates;

	public Object2RuleWithBuiltInPredicateConverter(BuiltInPredicateSet btpredicates) {
		this.btpredicates = btpredicates;
	}

	@Override
	public Object convert(Object object) throws ConversionException {

		if (object instanceof Rule && !(object instanceof RuleWithBuiltInPredicate)) {
			boolean found = false;

			// We will do a speculative process on a DefaultRuleWithBuiltInPredicates object
			Rule newRule = new DefaultRuleWithBuiltInPredicates((Rule) object, btpredicates);
			InMemoryAtomSet newRuleBody = newRule.getBody();

			CloseableIteratorWithoutException<Atom> it = newRuleBody.iterator();

			// Detect built-in predicates
			while (it.hasNext()) {
				Atom a = it.next();
				int predicateIndex = btpredicates.indexOf(a.getPredicate());

				// Match
				if (predicateIndex != -1) {
					found = true;
					a.setPredicate(btpredicates.get(predicateIndex));
				}
			}

			if (found)
				return newRule;
		}
		return object;
	}
}