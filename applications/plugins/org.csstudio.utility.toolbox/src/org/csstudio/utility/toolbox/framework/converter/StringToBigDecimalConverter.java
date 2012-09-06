package org.csstudio.utility.toolbox.framework.converter;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;

public class StringToBigDecimalConverter implements IConverter {

	@Override
	public Object getFromType() {
		return String.class;
	}

	@Override  
	public Object getToType() {
		return BigDecimal.class;
	}

	@Override
	public Object convert(Object fromObject) {
		return new BigDecimal((String)fromObject);
	}

}
