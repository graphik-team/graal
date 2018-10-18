/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

package org.graal.store.dictionary;

import java.util.Map;
import java.util.TreeMap;

/**
 * DictionnaryMapper which use {@link TreeMap} as index
 * 
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class TreeMapDictionaryMapper extends DictionaryMapper {

	/*
	 * Associate Short Integer id to term or predicate URI
	 */
	protected Map<String, Integer> termIds;

	@Override
	public Integer getIntegerIdOf(String termURI) {
		return termIds.get(termURI);
	}

	@Override
	public Map<String, Integer> getIdentifierDictionary() {
		return termIds;
	}

	public TreeMapDictionaryMapper() {
		super();
		termIds = new TreeMap<>();
	}

	@Override
	public void buildDictionary() {
		if (identifiers.isEmpty()) {
			return;
		}

		int objIdx = 0;

		for (int i = 0; i < identifiers.size(); i++) {
			termIds.put(identifiers.get(i), dataTypes.get(i));
		}

		identifiers.clear();
		dataTypes.clear();

		for (String str : termIds.keySet()) {
			identifiers.add(str);
			dataTypes.add(termIds.get(str));
			termIds.put(str, objIdx++); // rebuild of the index
		}
		existentialBeginIdx = termIds.size();
	}
}