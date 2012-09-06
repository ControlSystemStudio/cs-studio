package org.csstudio.utility.toolbox.guice.provider;

import java.text.SimpleDateFormat;

import org.csstudio.utility.toolbox.common.Environment;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SimpleDateFormatProvider  implements Provider<SimpleDateFormat> {

	@Inject
	private Environment env;
	
	@Override
	public SimpleDateFormat get() {
		return new SimpleDateFormat(env.getDateFormat());
	}

}
