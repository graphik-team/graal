package fr.lirmm.graphik.graal.api.core.mapper;

import java.util.List;

import fr.lirmm.graphik.graal.api.core.Term;

/**
 * 
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public interface TermMapper {

	public Term map(Term term);

	public Term unmap(Term term);

	public List<Term> map(List<Term> terms);

	public List<Term> unmap(List<Term> terms);
}