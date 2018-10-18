package org.graal.store.dictionary;

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;

/**
 * Convert a substitution with a mapper
 * 
 * @author renaud colin
 * @author mathieu dodard
 *
 */
public class MapperSubstitutionConverter implements Converter<Substitution, Substitution> {

	private DictionaryMapper mapper;

	public MapperSubstitutionConverter(DictionaryMapper mapper) {
		super();
		this.mapper = mapper;
	}

	@Override
	public Substitution convert(Substitution object) throws ConversionException {
		return mapper.unmap(object);
	}

}
