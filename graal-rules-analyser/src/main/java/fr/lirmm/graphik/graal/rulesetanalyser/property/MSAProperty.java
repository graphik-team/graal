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
 /**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.GraalConstant;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

public final class MSAProperty extends RuleSetProperty.Default {

	private static final Logger LOGGER = LoggerFactory.getLogger(MSAProperty.class);

	private static MSAProperty instance = null;

	private MSAProperty() { }

	public static synchronized MSAProperty instance() {
		if (instance == null) {
			instance = new MSAProperty();
		}
		return instance;
	}

	@Override
	public String getFullName() {
		return "Model-summarizing acyclicity";
	}

	@Override
	public String getDescription() {
		return "Approximates MFA with a lower complexity.";
	}

	@Override
	public int check(AnalyserRuleSet ruleSet) {
		RuleSet R = translateToMSA(ruleSet);
		AtomSet A = Rules.criticalInstance(ruleSet);

		try { StaticChase.executeChase(A,R); }
		catch (ChaseException e) {
			LOGGER.warn("An error occurs during the chase: ", e);
			return 0;
		}

		DefaultConjunctiveQuery Q = new DefaultConjunctiveQuery();
		DefaultAtom q = new DefaultAtom(C);
		q.setTerm(0,FAKE);
		Q.getAtomSet().add(q);

		try { 
			if (SmartHomomorphism.instance().exist(Q, A))
				return -1;
			return 1;
		}catch (HomomorphismException e) {
			LOGGER.warn("An error occurs during the homomorphism: ", e);
			return 0;
		}
	}

	@Override
	public String getLabel() {
		return "msa";
	}

	@Override
	public Iterable<RuleSetProperty> getGeneralisations() {
		List<RuleSetProperty> gen = new LinkedList<RuleSetProperty>();
		gen.add(FESProperty.instance());
		gen.add(BTSProperty.instance());
		gen.add(MFAProperty.instance());
		return gen;
	}

	public static RuleSet translateToMSA(Iterable<Rule> rules) {
		RuleSet R = new LinkedListRuleSet();
		for (Rule r : rules) {
			for (Rule r2 : translateRuleToMSA(r))
				R.add(r2);
		}
		DefaultRule rule = new DefaultRule();
		Atom s = new DefaultAtom(S);
		s.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
		s.setTerm(1,DefaultTermFactory.instance().createVariable("X2"));
		Atom d = new DefaultAtom(D);
		d.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
		d.setTerm(1,DefaultTermFactory.instance().createVariable("X2"));
		rule.getBody().add(s);
		rule.getHead().add(d);

		R.add(rule);

		s = new DefaultAtom(S);
		d = new DefaultAtom(D);
		Atom d2 = new DefaultAtom(D);
		d.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
		d.setTerm(1,DefaultTermFactory.instance().createVariable("X2"));
		s.setTerm(0,DefaultTermFactory.instance().createVariable("X2"));
		s.setTerm(1,DefaultTermFactory.instance().createVariable("X3"));
		d2.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
		d2.setTerm(1,DefaultTermFactory.instance().createVariable("X3"));
		rule = new DefaultRule();
		rule.getBody().add(d);
		rule.getBody().add(s);
		rule.getHead().add(d2);

		R.add(rule);

		return R;
	}

	public static List<Rule> translateRuleToMSA(final Rule r) {
		List<Rule> result = new LinkedList<Rule>();
		Substitution s = buildMSASubstitution(r);
		DefaultRule r2 = new DefaultRule(r);
		/*r2.setBody(r.getBody());
		r2.setHead(r.getHead());*/
		for (Term yi : r2.getExistentials()) {
			Predicate Fir = GraalConstant.freshPredicate(1);
			DefaultAtom f = new DefaultAtom(Fir);
			f.setTerm(0,yi);
			r2.getHead().add(f);
			for (Term xj : r2.getFrontier()) {
				DefaultAtom ss = new DefaultAtom(S);
				ss.setTerm(0,xj);
				ss.setTerm(1,yi);
				r2.getHead().add(ss);
			}

			DefaultRule r3 = new DefaultRule();
			DefaultAtom f1 = new DefaultAtom(Fir);
			f1.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
			DefaultAtom f2 = new DefaultAtom(Fir);
			f2.setTerm(0,DefaultTermFactory.instance().createVariable("X2"));
			DefaultAtom d = new DefaultAtom(D);
			d.setTerm(0,DefaultTermFactory.instance().createVariable("X1"));
			d.setTerm(1,DefaultTermFactory.instance().createVariable("X2"));

			r3.getBody().add(f1);
			r3.getBody().add(d);
			r3.getBody().add(f2);

			DefaultAtom c = new DefaultAtom(C);
			c.setTerm(0,FAKE);
			r3.getHead().add(c);

			result.add(r3);
		}
		r2.setHead(s.createImageOf(r2.getHead()));
		result.add(r2);

		return result;
	}

	public static Substitution buildMSASubstitution(final Rule r) {
		Substitution s = new TreeMapSubstitution();
		for (Variable yi : r.getExistentials())
			s.put(yi,GraalConstant.freshConstant());
		return s;
	}

	private static final Predicate D = GraalConstant.freshPredicate(2);
	private static final Predicate S = GraalConstant.freshPredicate(2);
	private static final Predicate C = GraalConstant.freshPredicate(1);
	private static final Term FAKE = GraalConstant.freshConstant();

};

