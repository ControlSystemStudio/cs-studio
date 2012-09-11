package org.csstudio.utility.toolbox.framework.converter;

import org.csstudio.utility.toolbox.common.Environment;
import org.eclipse.core.databinding.conversion.IConverter;

import com.google.inject.Inject;

public class NullToStringConverter implements IConverter {

	@Inject
	private Environment env;

	@Override
	public Object getFromType() {
		return String.class;
	}

	@Override  
	public Object getToType() {
		return String.class;
	}

	@Override
	public Object convert(Object fromObject) {
		String value = (String)fromObject;		
		if (value ==  null) {
			return env.getEmptySelectionText();
		}		
		return value;
	}

}
