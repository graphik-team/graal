package fr.lirmm.graphik.graal.homomorphism;

import java.util.ArrayList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithBuiltInPredicates;
import fr.lirmm.graphik.graal.api.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;
import fr.lirmm.graphik.util.stream.converter.ConverterIterator;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * 
 * @author Olivier Rodriguez
 *
 */
public final class ConjunctiveQueryWithBuiltInPredicatesHomomorphism
		extends AbstractHomomorphism<ConjunctiveQueryWithBuiltInPredicates, AtomSet> {

	private static ConjunctiveQueryWithBuiltInPredicatesHomomorphism instance;

	protected ConjunctiveQueryWithBuiltInPredicatesHomomorphism() {
		super();
	}

	public static synchronized ConjunctiveQueryWithBuiltInPredicatesHomomorphism instance() {

		if (instance == null)
			instance = new ConjunctiveQueryWithBuiltInPredicatesHomomorphism();

		return instance;
	}

	@Override
	public CloseableIterator<Substitution> execute(ConjunctiveQueryWithBuiltInPredicates q, AtomSet a, Substitution s)
			throws HomomorphismException {

		return execute(q, SmartHomomorphism.instance().execute(q.getBaseQuery(), a, s));
	}

	private CloseableIterator<Substitution> execute(ConjunctiveQueryWithBuiltInPredicates q,
			CloseableIterator<Substitution> result) throws HomomorphismException {
		// Here we will check if the two queries have the same variables for answer
		ArrayList<Term> tmp = new ArrayList<>(q.getBaseQuery().getAnswerVariables());
		tmp.removeAll(q.getBuiltInQuery().getAnswerVariables());

		// Same answer's variable
		if (tmp.isEmpty())
			return getFilterIterator(q, result);

		return getAnswerConverter(q, getFilterIterator(q, result));
	}

	/**
	 * Filter the result to avoid Substitutions which not validate the built-in
	 * query.
	 * 
	 * @param result
	 * @return
	 */
	protected CloseableIterator<Substitution> getFilterIterator(final ConjunctiveQueryWithBuiltInPredicates q,
			CloseableIterator<Substitution> result) {
		return new FilterIterator<Substitution, Substitution>(result, new Filter<Substitution>() {

			public boolean filter(Substitution substitution) {
				boolean ret = true;
				CloseableIteratorWithoutException<Atom> qatomIt = q.getBuiltInQuery().getAtomSet().iterator();
				ArrayList<Term> termsForValidation = new ArrayList<>();

				atomCheck: while (qatomIt.hasNext()) {
					Atom qatom = qatomIt.next();
					BuiltInPredicate predicate = (BuiltInPredicate) qatom.getPredicate();

					// Clean the terms for the current step
					termsForValidation.clear();

					// We build the term set to validate
					for (Term qatomTerm : qatom.getTerms()) {

						// A constant is just add to the validation set
						if (qatomTerm.isConstant())
							termsForValidation.add(qatomTerm);
						// A variable from the query atom is not in the substitution : we pass
						else if (substitution.createImageOf(qatomTerm) == qatomTerm)
							continue atomCheck;
						// Get the substitution value of the variable
						else
							termsForValidation.add(substitution.createImageOf(qatomTerm));
					}

					// Check the predicate condition
					// TODO: do not catch the exception, throw it
					try {
						ret = predicate.validate(termsForValidation);
					} catch (HomomorphismException e) {
						e.printStackTrace();
					}

					if (!ret)
						break;
				}
				qatomIt.close();
				return ret;
			}
		});
	}

	/**
	 * Select the variables from the filtered Substitutions to make the answer.
	 * 
	 * @return
	 */
	protected ConverterIterator<Substitution, Substitution> getAnswerConverter(
			final ConjunctiveQueryWithBuiltInPredicates q, CloseableIterator<Substitution> filteredResult) {
		return new ConverterIterator<Substitution, Substitution>(filteredResult,
				new Converter<Substitution, Substitution>() {

					@Override
					public Substitution convert(Substitution substitution) throws ConversionException {
						Substitution ret = new HashMapSubstitution();

						for (Term qvar : q.getBuiltInQuery().getAnswerVariables())
							ret.put((Variable) qvar, substitution.createImageOf(qvar));

						return ret;
					}
				});
	}
}