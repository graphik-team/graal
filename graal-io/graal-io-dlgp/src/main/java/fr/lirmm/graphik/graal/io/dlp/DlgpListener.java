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
package fr.lirmm.graphik.graal.io.dlp;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.FreshVarSubstitution;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.InMemoryStream;

class DlgpListener extends AbstractDlgpListener {

	private InMemoryStream<Object> set;

	DlgpListener(InMemoryStream<Object> buffer) {
		this.set = buffer;
	}

	@Override
	protected void createAtomSet(InMemoryAtomSet atomset) {
		FreshVarSubstitution s = new FreshVarSubstitution(DlgpParser.freeVarGen);
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			this.set.write(s.createImageOf(a));
		}
	}

	@Override
	protected void createQuery(ConjunctiveQuery query) {
		this.set.write(query);
	}

	@Override
	protected void createRule(Rule rule) {
		this.set.write(rule);
	}

	@Override
	protected void createNegConstraint(DefaultNegativeConstraint negativeConstraint) {
		this.set.write(negativeConstraint);
	}

	@Override
	public void declarePrefix(String prefix, String ns) {
		this.set.write(new Prefix(prefix.substring(0, prefix.length() - 1),
				ns));
	}

	@Override
	public void declareBase(String base) {
		this.set.write(new Directive(Directive.Type.BASE, base));
	}

	@Override
	public void declareTop(String top) {
		this.set.write(new Directive(Directive.Type.TOP, top));
	}

	@Override
	public void declareUNA() {
		this.set.write(new Directive(Directive.Type.UNA, null));
	}

	@Override
	public void directive(String text) {
		this.set.write(new Directive(Directive.Type.COMMENT, text));
	}
}