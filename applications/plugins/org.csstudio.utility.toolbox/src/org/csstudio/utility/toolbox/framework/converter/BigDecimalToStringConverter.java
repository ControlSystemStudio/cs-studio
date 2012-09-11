package org.csstudio.utility.toolbox.framework.converter;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;

public class BigDecimalToStringConverter implements IConverter {

	@Override
	public Object getFromType() {
		return BigDecimal.class;
	}

	@Override  
	public Object getToType() {
		return String.class;
	}

	@Override
	public Object convert(Object fromObject) {
		String value = fromObject.toString();
		value = value.replace(".", "");
		return value;
	}

}
