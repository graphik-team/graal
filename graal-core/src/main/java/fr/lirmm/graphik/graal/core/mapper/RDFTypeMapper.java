package fr.lirmm.graphik.graal.core.mapper;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.AtomFactory;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;

/**
 * Mapper used to map a unary atom to a binary atom with rdf:type predicate
 * 
 * @author Mathieu Dodard
 * @author Renaud Colin
 */
public class RDFTypeMapper extends AbstractMapper {

        private TermFactory termFactory;
        private AtomFactory atomFactory;

        public RDFTypeMapper() {
                super();
                termFactory = DefaultTermFactory.instance();
                atomFactory = DefaultAtomFactory.instance();
        }

        /**
         * Creates, from a unary atom, a new binary atom for which the predicate is the
         * URI of rdf:type, the first term is the term given in the unary atom and the
         * second term is the predicate of the unary atom. p(X) gives [rdf:type URI](X, p).
         * 
         * @param atom
         *            The unary atom you want to transform
         * 
         * @return The new binary atom
         */
        @Override
        public Atom map(Atom atom) {

            if (atom.getPredicate().getArity() != 1)
                    return atom;

            Term term1=null;
            Predicate rdfTypePredicate = new Predicate(URIUtils.RDF_TYPE, 2);
            
            if(!atom.getTerm(0).isConstant() ) {
            	term1=atom.getTerm(0);
            }
            else{
            	term1 = atom.getTerm(0).getIdentifier() instanceof URI ? 
            		termFactory.createConstant(new DefaultURI(atom.getTerm(0).toString())):
            			termFactory.createConstant(atom.getTerm(0).toString());				
            }
            
            Term term2 = termFactory.createConstant(atom.getPredicate().getIdentifier());
            Atom newAtom = atomFactory.create(rdfTypePredicate, term1, term2);
            
            return newAtom;

        }

        /**
         * Creates, from a binary atom, a new unary atom for which the predicate is the
         * second term of the binary atom and the term is the first term of the binary
         * atom. [rdf:type URI](X, p) gives p(X)
         * 
         * @param atom
         *            The binary atom you want to transform
         * 
         * @return The new unary atom
         */
        @Override
        public Atom unmap(Atom atom) {
                
                if ((atom.getPredicate().getArity() == 1) 
                                || (!atom.getPredicate().getIdentifier().equals(URIUtils.RDF_TYPE))
                                || (!atom.getTerm(1).isConstant()) )
                        return atom;

//                if(!atom.getTerm(1).isConstant()) 
//                        throw new IllegalArgumentException("The second term must be a constant");
                
                Predicate predicate = new Predicate(atom.getTerm(1), 1);
                Term term = atom.getTerm(0).isVariable() ? atom.getTerm(0) : termFactory.createConstant(atom.getTerm(0).toString());
                Atom newAtom = atomFactory.create(predicate, term);
                return newAtom;
        }

        /**
         * This method is not supposed to be used. It returns the same predicate given as
         * parameter.
         * 
         * @param predicate
         *            The same predicate that will be returned.
         * 
         * @return The same predicate given as parameter.
         */
        @Override
        public Predicate map(Predicate predicate) {
                return predicate;
        }

        /**
         * This method is not supposed to be used. It returns the same predicate given as
         * parameter.
         * 
         * @param predicate
         *            The same predicate that will be returned.
         * 
         * @return The same predicate given as parameter.
         */
        @Override
        public Predicate unmap(Predicate predicate) {
                return predicate;
        }

}

