package fr.lirmm.graphik.graal.core.grd;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRD;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

public class ChaseWithGRDAndCompilationTest {

	private void checkResult(InMemoryAtomSet result, InMemoryAtomSet expected)
			throws AtomSetException, HomomorphismException {
		PureHomomorphism pure = PureHomomorphism.instance();
		boolean a = expected.size() == result.size();
		boolean b = a && pure.exist(expected, result);
		boolean res = b;
		assertTrue(String.format("Waiting for\n%s\nResult is\n%s", expected, result), res);
	}

	@Test
	public void oneCompilationSimple() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		// Compilable
		onto.add(DlgpParser.parseRule("s(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X), u(a,b,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void oneCompilationWith2SameVars_1() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		// Compilable
		onto.add(DlgpParser.parseRule("s(X,X,Z) :- q(X,X,Z)."));
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,a)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,a), q(a,a,X), u(a,a,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void oneCompilationWith2SameVars_2() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		// Compilable
		onto.add(DlgpParser.parseRule("s(X,X,Z) :- q(X,X,Z)."));
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X)."));
		checkResult(atoms, expected);
	}

	@Test
	public void oneCompilationWithReversedVar() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		// Compilable
		onto.add(DlgpParser.parseRule("s(Y,X,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X), u(b,a,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void oneCompilationAndOneRuleWithReversedVar() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		// Compilable
		onto.add(DlgpParser.parseRule("s(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("u(Y,X,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X), u(b,a,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void twoCompilationsSimple() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(X,Y,Z) :- t(X,Y,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(Y,X,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X), u(b,a,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void twoCompilationsWithOneReversedVarsWithOneRuleReversedVars() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(Y,X,Z) :- t(X,Y,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(Y,X,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X), u(a,b,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void twoCompilationsWithOne2SameVars_1() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(X,X,Z) :- t(X,X,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b), q(a,b,X)."));
		checkResult(atoms, expected);
	}

	@Test
	public void twoCompilationsWithOne2SameVars_2() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(X,X,Z) :- t(X,X,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,a)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,a), q(a,a,X),u(a,a,Y)."));
		checkResult(atoms, expected);
	}

	@Test
	public void compilationWithACycle_1() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,Z) :- p(X,Y)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(X,Y,Z) :- t(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("q(Y,X,Z) :- s(X,Y,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b),q(a,b,X),u(a,b,Y),u(b,a,Z)."));
		checkResult(atoms, expected);
	}

	@Test
	public void compilationWithACycle_2() throws Exception {
		Ontology onto = new DefaultOntology();
		InMemoryAtomSet atoms = new LinkedListAtomSet();

		onto.add(DlgpParser.parseRule("q(X,Y,ZE) :- p(X,Y,Z)."));
		
		// Compilables{
		onto.add(DlgpParser.parseRule("t(X,Y,Z) :- q(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("s(X,Y,Z) :- t(X,Y,Z)."));
		onto.add(DlgpParser.parseRule("p(Y,X,Z) :- s(X,Y,Z)."));
		// }
		onto.add(DlgpParser.parseRule("u(X,Y,ZE) :- s(X,Y,Z)."));

		atoms.add(DlgpParser.parseAtom("p(a,b,c)."));

		IDCompilation compilation = new IDCompilation();
		compilation.compile(onto.iterator());
		Chase GRDChase = new ChaseWithGRD<AtomSet>(onto.iterator(), atoms, compilation);
		GRDChase.execute();

		InMemoryAtomSet expected = new LinkedListAtomSet();
		expected.addAll(DlgpParser.parseAtomSet("p(a,b,c),q(a,b,X),u(a,b,Y),q(b,a,Z),u(b,a,A)."));
		checkResult(atoms, expected);
	}
}
