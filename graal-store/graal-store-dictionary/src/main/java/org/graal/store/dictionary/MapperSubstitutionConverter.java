package org.graal.store.dictionary;

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.core.mapper.AbstractMapper;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

/**
 * Convert a substitution with a mapper
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class MapperSubstitutionConverter implements Converter<Substitution,Substitution>{

	private AbstractMapper mapper;
	
	
	public MapperSubstitutionConverter(AbstractMapper mapper) {
		super();
		this.mapper = mapper;
	}


	@Override
	public Substitution convert(Substitution object) throws ConversionException {
		return mapper.unmap(object);
	}
	
}
