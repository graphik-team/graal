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
 package fr.lirmm.graphik.graal.cqa;

import java.io.File;
import java.io.FileWriter;
import java.util.TreeMap;
import java.util.Set;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

public class AtomIndex {

	public AtomIndex(String filepath) {
		_filepath = filepath;
		readFromFile();
		_hasBeenModified = false;
	}

	protected void finalize() {
		if (_hasBeenModified)
			writeToFile();
	}

	public void readFromFile() {
		try {
			File f = new File(_filepath);
			Scanner in = new Scanner(f);
			Pattern p = Pattern.compile("([0-9]+) (.*)");
			Matcher m;
			while (in.hasNextLine()) {
				String line = in.nextLine();
				m = p.matcher(line);
				if (m.find()) {
					int i = Integer.parseInt(m.group(1));
					Atom a = DlgpParser.parseAtom(m.group(2));
					add(a,new Integer(i));
					_currentIndex = (i > _currentIndex) ? i : _currentIndex;
				}
			}
		}
		catch (Exception e) {
			System.err.println("AtomIndex::readFromFilepath: " + e);
			e.printStackTrace();
		}
	}

	public void writeToFile() {
		try {
			File f = new File(_filepath);
			FileWriter out = new FileWriter(f);
			Set<Integer> keys = _indexToAtom.keySet();
			for (Integer i : keys) {
				out.write(""+i);
				out.write(' ');
				out.write(DlgpWriter.writeToString(_indexToAtom.get(i)));
				out.write("\n");
			}
			_hasBeenModified = false;
		}
		catch (Exception e) {
			System.err.println("AtomIndex::writeToFilepath: " + e);
			e.printStackTrace();
		}
	}

	public int get(Atom a) {
		Integer i = _atomToIndex.get(a);
		if (i == null) { 
			add(a); 
			return get(a);
		}
		return _atomToIndex.get(a).intValue();
	}

	public Atom get(int i) {
		return _indexToAtom.get(new Integer(i));
	}

	public void add(Atom a, Integer i) {
		_atomToIndex.put(a,i);
		_indexToAtom.put(i,a);
		_hasBeenModified = true;
	}

	public void add(Atom a) {
		add(a,new Integer(_currentIndex));
		++_currentIndex;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		int i = 0;
		Atom a = get(i);
		while (a != null) {
			s.append(i);
			s.append("\t");
			s.append(a);
			s.append("\n");
			a = get(++i);
		}
		return s.toString();
	}

	private TreeMap<Atom,Integer> _atomToIndex = new TreeMap<Atom,Integer>();
	private TreeMap<Integer,Atom> _indexToAtom = new TreeMap<Integer,Atom>();

	private int _currentIndex = 0;
	private boolean _hasBeenModified = false;

	private String _filepath;
};

