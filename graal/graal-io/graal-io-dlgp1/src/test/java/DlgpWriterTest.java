import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpWriterTest {
	
	private static Predicate predicat = new Predicate("p", 1);
	private static Term cst = new Term("A", Term.Type.CONSTANT);
	
	@Test
	public void writeConstant() throws IOException {
		Term a = new Term("A", Term.Type.CONSTANT);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Dlgp1Writer writer = new Dlgp1Writer(os);
		
		writer.write(new DefaultAtom(predicat, a));
		writer.flush();

		String s = new String(os.toByteArray(),"UTF-8");
		writer.close();
		
		char c = s.charAt(s.indexOf("(") + 1);
		Assert.assertTrue("Constant label does not begin with lower case.", Character.isLowerCase(c) || c == '<');
	}
	
	@Test
	public void writeVariable() throws IOException {
		Term x = new Term("x", Term.Type.VARIABLE);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Dlgp1Writer writer = new Dlgp1Writer(os);
		
		writer.write(new DefaultAtom(predicat, x));
		writer.flush();

		String s = new String(os.toByteArray(),"UTF-8");
		writer.close();
		Assert.assertTrue("Variable label does not begin with upper case.", Character.isUpperCase(s.charAt(s.indexOf("(") + 1)));
	}
	
	@Test
	public void writePredicate() throws IOException {
		Predicate p = new Predicate("P", 1);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Dlgp1Writer writer = new Dlgp1Writer(os);
		
		writer.write(new DefaultAtom(p, cst));
		writer.flush();

		String s = new String(os.toByteArray(),"UTF-8");
		writer.close();
		
		Character c = s.charAt(0);
		Assert.assertTrue("Predicate label does not begin with lower case or double quote.", Character.isLowerCase(c) || c == '"');
	}

}
