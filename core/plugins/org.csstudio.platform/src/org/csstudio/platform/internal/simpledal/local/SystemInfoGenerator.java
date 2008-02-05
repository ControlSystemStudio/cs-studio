package org.csstudio.platform.internal.simpledal.local;

import java.lang.reflect.Method;

public class SystemInfoGenerator extends AbstractDataGenerator<String> {
	private static final Class INFO_CLASS = Environment.class;

	private static final String METHOD_PREFIX = "get";

	private Method _staticMethod;

	public SystemInfoGenerator(LocalChannel localChannel, int defaultPeriod,
			String[] options) {
		super(localChannel, defaultPeriod, options);

		assert options.length == 3 : "options.length==3";

		if (options[2] != null) {
			try {
				int period = Integer.valueOf(options[2]);
				setPeriod(period);
			} catch (NumberFormatException e) {
				assert false : "Should be ensured by regular expression.";
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init(String[] options) {
		String methodName = options[0];

		try {
			_staticMethod = INFO_CLASS.getMethod(METHOD_PREFIX + methodName,
					null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			int period = Integer.parseInt(options[1]);
			setPeriod(period);
		} catch (NumberFormatException nfe) {
			// ignore
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String generateNextValue() {
		Object result = "";

		if (_staticMethod != null) {
			try {
				result = _staticMethod.invoke(null, null);
			} catch (Exception e) {
				result = e.getMessage();
			}
		}

		return result.toString();
	}

}
