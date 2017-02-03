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
package fr.lirmm.graphik.graal.io.sparql;

import java.util.Iterator;

import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementData;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.io.ParseError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
// ////////////////////////////////////////////
// PRIVATE CLASSES
// /////////////////////////////////////////////////////////////////////////

class ElementVisitorImpl implements ElementVisitor {

	private InMemoryAtomSet atomset;

	ElementVisitorImpl(InMemoryAtomSet atomset) {
		this.atomset = atomset;
	}

	public InMemoryAtomSet getAtomSet() {
		return this.atomset;
	}

	@Override
	public void visit(ElementSubQuery arg0) {
		throw new ParseError("SubQuery not allowed");
	}

	@Override
	public void visit(ElementService arg0) {
		throw new ParseError("ElementService not allowed");
	}

	@Override
	public void visit(ElementMinus arg0) {
		throw new ParseError("ElementMinus not allowed");
	}

	@Override
	public void visit(ElementNotExists arg0) {
		throw new ParseError("ElementNotExists not allowed");
	}

	@Override
	public void visit(ElementExists arg0) {
		throw new ParseError("ElementExists not allowed");
	}

	@Override
	public void visit(ElementNamedGraph arg0) {
		throw new ParseError("ElementNamedGraph not allowed");
	}

	@Override
	public void visit(ElementDataset arg0) {
		throw new ParseError("ElementDataset not allowed");
	}

	@Override
	public void visit(ElementGroup arg0) {
		for (Element e : arg0.getElements()) {
			e.visit(this);
		}
	}

	@Override
	public void visit(ElementOptional arg0) {
		throw new ParseError("ElementOptional not allowed");
	}

	@Override
	public void visit(ElementUnion arg0) {
		throw new ParseError("ElementUnion not allowed");
	}

	@Override
	public void visit(ElementData arg0) {
		throw new ParseError("ElementData not allowed");
	}

	@Override
	public void visit(ElementBind arg0) {
		throw new ParseError("ElementBind not allowed");
	}

	@Override
	public void visit(ElementAssign arg0) {
		throw new ParseError("ElementAssign not allowed");
	}

	@Override
	public void visit(ElementFilter arg0) {
		throw new ParseError("ElementFilter not allowed");
	}

	@Override
	public void visit(ElementPathBlock arg0) {
		Iterator<TriplePath> it = arg0.patternElts();
		while (it.hasNext()) {
			TriplePath t = it.next();
			if (t.isTriple()) {
				this.atomset.add(SparqlUtils.triple2Atom(t.asTriple()));
			} else {
				throw new ParseError("TriplePath not allowed");
			}
		}
	}

	@Override
	public void visit(ElementTriplesBlock arg0) {
		throw new ParseError("ElementTriplesBlock not allowed");
	}

}