package fr.lirmm.graphik.graal.homomorphism;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

public class Atom2SubstitutionConverterTest {

	@Test
	public void basic() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 1);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Atom queryAtom = new DefaultAtom(p, x);
		List<Term> ansList = new LinkedList<>();
		ansList.add(x);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a)."));
		} catch (ConversionException e) {
			fail();
		}
		
		// then
		Constant a = DefaultTermFactory.instance().createConstant("a");
		assertEquals(a, s.createImageOf(x));
	}
	
	@Test
	public void basic2() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 3);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Variable z = DefaultTermFactory.instance().createVariable("Z");
		Atom queryAtom = new DefaultAtom(p, x, y, z);

		List<Term> ansList = new LinkedList<>();
		ansList.add(x);
		ansList.add(z);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a,b,c)."));
		} catch (ConversionException e) {
			fail();
		}
		
		// then
		Constant a = DefaultTermFactory.instance().createConstant("a");
		Constant c = DefaultTermFactory.instance().createConstant("c");
		assertEquals(a, s.createImageOf(x));
		assertEquals(y, s.createImageOf(y));
		assertEquals(c, s.createImageOf(z));
	}

	
	@Test
	public void githubIssue2() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 1);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Atom queryAtom = new DefaultAtom(p, x);
		List<Term> ansList = new LinkedList<>();
		ansList.add(y);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a)."));
		} catch (ConversionException e) {
			fail();
		}
		// then
		assertEquals(x, s.createImageOf(x));
		assertEquals(y, s.createImageOf(y));
	}
	
	@Test
	public void githubIssue2variant1() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 1);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Atom queryAtom = new DefaultAtom(p, x);
		List<Term> ansList = new LinkedList<>();
		ansList.add(x);
		ansList.add(y);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a)."));
		} catch (ConversionException e) {
			fail();
		}
		// then
		Constant a = DefaultTermFactory.instance().createConstant("a");
		assertEquals(a, s.createImageOf(x));
		assertEquals(y, s.createImageOf(y));
	}
	

	@Test
	public void githubIssue2variantWithConstant() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 1);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Constant b = DefaultTermFactory.instance().createConstant("b");
		Atom queryAtom = new DefaultAtom(p, x);
		List<Term> ansList = new LinkedList<>();
		ansList.add(x);
		ansList.add(b);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a)."));
		} catch (ConversionException e) {
			fail();
		}
		// then
		Constant a = DefaultTermFactory.instance().createConstant("a");
		assertEquals(a, s.createImageOf(x));
		assertEquals(b, s.createImageOf(b));
	}
	
	@Test
	public void wrongUsage() throws ParseException {
		// given
		Predicate p = DefaultPredicateFactory.instance().create("p", 1);
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Atom queryAtom = new DefaultAtom(p, x, x);
		List<Term> ansList = new LinkedList<>();
		ansList.add(x);
		
		// when
		Converter<Atom, Substitution> converter = new Atom2SubstitutionConverter(queryAtom, ansList);
		Substitution s = null;
		try {
			s = converter.convert(DlgpParser.parseAtom("p(a, b)."));
		} catch (ConversionException e) {
			fail();
		}
		// then
		Constant a = DefaultTermFactory.instance().createConstant("a");
		System.out.println(s);
	}

}
