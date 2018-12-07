package fr.lirmm.graphik.graal.core.mapper;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.URIUtils;

/**
 * @author Mathieu Dodard
 * @author Renaud Colin
 *
 */

public class RDFTypeMapperTest {

        ////////////////////
        //// MAP
        ///////////////////

        @Test
        public void testMapUnaryAtom() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToMap = DlgpParser.parseAtom("<P>(X). ");
                Atom mappedAtom = rdfTypeMapper.map(atomToMap);

                /* creation of the model atom */
                Term termFromPredicate = DefaultTermFactory.instance().createConstant(atomToMap.getPredicate().getIdentifier());
                Predicate rdfTypePredicate = new Predicate(URIUtils.RDF_TYPE, 2);
                Atom modelAtom = new DefaultAtom(rdfTypePredicate, atomToMap.getTerm(0), termFromPredicate);

                Assert.assertEquals(mappedAtom, modelAtom);
        }
        
        @Test
        public void testMapUnaryAtomWithConstant() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToMap = DlgpParser.parseAtom("<P>(<X>). ");
                Atom mappedAtom = rdfTypeMapper.map(atomToMap);

                /* creation of the model atom */
                Term termFromPredicate = DefaultTermFactory.instance().createConstant(atomToMap.getPredicate().getIdentifier());
                Predicate rdfTypePredicate = new Predicate(URIUtils.RDF_TYPE, 2);
                Atom modelAtom = new DefaultAtom(rdfTypePredicate, atomToMap.getTerm(0), termFromPredicate);

                Assert.assertEquals(mappedAtom, modelAtom);
        }

        @Test
        public void testMapBinaryAtom() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToMap = DlgpParser.parseAtom("<P>(X,Y). ");
                Atom mappedAtom = rdfTypeMapper.map(atomToMap);

                Assert.assertEquals(mappedAtom, atomToMap);
        }

        ////////////////////
        //// UNMAP
        ///////////////////

        @Test
        public void testUnmapUnaryAtom() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<P>(Y). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);

                Assert.assertEquals(unmappedAtom, atomToUnmap);
        }

        @Test
        public void testUnmapAtomWithoutRdfType() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<P>(X,Y). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);

                Assert.assertEquals(unmappedAtom, atomToUnmap);
        }
        
        @Test
        public void testUnmapBinaryAtomWithFirstTermConstant() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>(<X>,<Y>). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);
                
                /* creation of the model atom */
                Predicate predicateFromTerm = new Predicate(atomToUnmap.getTerm(1), 1);
                Atom modelAtom = new DefaultAtom(predicateFromTerm, atomToUnmap.getTerm(0));

                Assert.assertEquals(unmappedAtom, modelAtom);
        }
        
        @Test
        public void testUnmapBinaryAtomWithFirstTermVariable() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>(X,<Y>). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);
                
                /* creation of the model atom */
                Predicate predicateFromTerm = new Predicate(atomToUnmap.getTerm(1), 1);
                Atom modelAtom = new DefaultAtom(predicateFromTerm, atomToUnmap.getTerm(0));

                Assert.assertEquals(unmappedAtom, modelAtom);
        }
        
        @Test
        public void testUnmapBinaryAtomWithSecondTermConstant() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>(X,<Y>). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);
                
                /* creation of the model atom */
                Predicate predicateFromTerm = new Predicate(atomToUnmap.getTerm(1), 1);
                Atom modelAtom = new DefaultAtom(predicateFromTerm, atomToUnmap.getTerm(0));

                Assert.assertEquals(unmappedAtom, modelAtom);
        }
        
        
        @Test
        public void testUnmapBinaryAtomWithSecondTermVariable() throws ParseException {
                RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();

                /* creation of the mapped atom */
                Atom atomToUnmap = DlgpParser.parseAtom("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>(X,Y). ");
                Atom unmappedAtom = rdfTypeMapper.unmap(atomToUnmap);
                Assert.assertEquals(unmappedAtom, atomToUnmap);
               
        }
        

}

