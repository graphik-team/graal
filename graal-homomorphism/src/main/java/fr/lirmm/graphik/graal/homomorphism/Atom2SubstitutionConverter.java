/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.lirmm.graphik.graal.homomorphism;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

/**
 * This class allow to convert Atoms into {@link Substitution} based on
 * an atomic {@link Query} pattern.
 * <br/>
 * For example given: <br/>
 * a query "p(X,Y,Z)"<br/>
 * a list of answer terms "X, Z"<br/>
 * <br/>
 * the {@link Atom} "p(a,b,c)" will produce the {@link Substitution} "{X->a, Z->c}".
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class Atom2SubstitutionConverter implements Converter<Atom, Substitution> {
	
	private Map<Variable, Integer> variables = new TreeMap<Variable, Integer>();
	private List<Term> ans;
	
	public Atom2SubstitutionConverter(Atom query, List<Term> ans) {
		this.ans = ans;
		int i = 0;
		for(Term t : query) {
			if(ans.contains(t))	 {
				variables.put((Variable) t, i);
			}
			++i;
		}
	}
	
	public Atom2SubstitutionConverter(Atom query, List<Term> ans, Substitution rew) {
		this.ans = ans;
		int i = 0;
		for(Term t : query) {
			if(ans.contains(t) || rew.getValues().contains(t))	 {
				variables.put((Variable) t, i);
			}
			++i;
		}
		for(Variable var : rew.getTerms()) {
			variables.put(var, variables.get(rew.createImageOf(var)));
		}
	}
	
	/**
	 * If you call this method with an atom which does not fulfill the query pattern, the behavior is not specified.
	 * 
	 * @param object the atom to convert
	 * @return  the corresponding substitution
	 */
	@Override
	public Substitution convert(Atom object) throws ConversionException {
		Substitution s = new HashMapSubstitution();
		for (Term var : ans) {
			if(var.isVariable() && variables.containsKey(var)) {
				s.put((Variable) var, object.getTerm(variables.get(var)));
			}
		}
		return s;
	}
}