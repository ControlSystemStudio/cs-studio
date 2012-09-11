package org.csstudio.utility.toolbox.framework.converter;

import java.text.SimpleDateFormat;

import org.eclipse.core.databinding.conversion.IConverter;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DateToStringConverter implements IConverter {

	@Inject
	private Provider<SimpleDateFormat> sd;
	
	@Override
	public Object getToType() {
		return String.class;
	}						
	@Override
	public Object getFromType() {
		return java.util.Date.class;
	}						
	@Override
	public Object convert(Object fromObject) {
		synchronized (DateToStringConverter.class) {
			return sd.get().format((java.util.Date)fromObject);
		}
	}

}
