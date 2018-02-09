package fr.lirmm.graphik.graal.io.sparql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import fr.lirmm.graphik.graal.GraalConstant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.URIzer;

public class AbstractSparqlWriterTest {

	public static final AbstractSparqlWriter createSparqlWriter(java.io.Writer w) {
		AbstractSparqlWriter writer = new AbstractSparqlWriter(w, new URIzer(GraalConstant.INTERNAL_PREFIX)) {
			
		};
		return writer;
	}


	@Test
	public void testWriteLiteral1() throws IOException {
		StringWriter sw = new StringWriter();
		AbstractSparqlWriter w = createSparqlWriter(sw);
		
		w.write(DefaultTermFactory.instance().createLiteral(URIUtils.XSD_INTEGER, 1));
		assertEquals("1",sw.toString());
	}
	
	@Test
	public void testWriteLiteral2() throws IOException {
		StringWriter sw = new StringWriter();
		AbstractSparqlWriter w = createSparqlWriter(sw);
		String str = "hello world!";
		w.write(DefaultTermFactory.instance().createLiteral(URIUtils.XSD_STRING, str));
		assertEquals("\"" + str + "\"",sw.toString());
	}


}
