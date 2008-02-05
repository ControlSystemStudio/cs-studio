package org.csstudio.platform.internal.simpledal.local;

import java.lang.reflect.Method;

public class ClassMethodGenerator extends AbstractDataGenerator<String> {
	private Method _staticMethod;

	public ClassMethodGenerator(LocalChannel localChannel, int defaultPeriod,
			String[] options) {
		super(localChannel, defaultPeriod, options);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init(String[] options) {
		String className = options[0];
		String methodName = options[1];

		try {
			Class c = Class.forName(className);
			_staticMethod = c.getMethod(methodName, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			int period = Integer.parseInt(options[2]);
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
