package fr.lirmm.graphik.graal.store.triplestore.rdf4j;

import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.common.rdf4j.RDF4jUtils;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

public class TupleQueryResult2SubstitutionConverter implements Converter<TupleQueryResult, Substitution> {

	private List<Term> ans;
	private RDF4jUtils utils;

	public TupleQueryResult2SubstitutionConverter(List<Term> ans, RDF4jUtils utils) {
		this.ans = ans;
		this.utils = utils;
	}

	@Override
	public Substitution convert(TupleQueryResult object) throws ConversionException {
		Substitution substitution = new TreeMapSubstitution();
		if (object.hasNext()) {
			BindingSet bindingSet = object.next();
			for (Term var : ans) {
				Term value = utils.valueToTerm(bindingSet.getValue(var.getLabel()));
				substitution.put((Variable) var, value);
			}
		}
		return substitution;
	}
}