package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Substitution;

public interface SubstitutionReader extends Iterator<Substitution>,
        Iterable<Substitution> {

    boolean hasNext();

    Substitution next();

    Iterator<Substitution> iterator();

    void close();

}
