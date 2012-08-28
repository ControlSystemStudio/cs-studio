package org.csstudio.utility.toolbox.framework.converter;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.conversion.IConverter;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class StringToDateConverter implements IConverter {

	@Inject
	private Provider<SimpleDateFormat> sd;
	
	@Override
	public Object getFromType() {
		return String.class;
	}

	@Override
	public Object getToType() {
		return java.util.Date.class;
	}

	@Override
	public Object convert(Object fromObject) {
		String value = (String)fromObject;
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return sd.get().parse(value);
		} catch (Exception e) {
			return new Date(0l);
		}
				
	}


}
