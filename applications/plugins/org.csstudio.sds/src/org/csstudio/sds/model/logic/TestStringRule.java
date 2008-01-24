package org.csstudio.sds.model.logic;

public class TestStringRule implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "TestStringRule";

	public Object evaluate(Object[] arguments) {
		String result = "Default";
		if ((arguments != null) && (arguments.length > 1)) {
			double d = 0.0;
			String prefix = "Wert: ";
            if (arguments[0] instanceof Double) {
                 d = (Double) arguments[0];
            }
            if (arguments[1] instanceof String) {
            	prefix = (String) arguments[1];
            }
            result = prefix+d;
		}
		return result;
	}

}
