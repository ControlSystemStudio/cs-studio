package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Gradient color rule, needs a double channel type.
 * Generates a gradient from -1.0=black to 1.0=white.
 * 
 * @author jbercic
 *
 */
public final class Gradient implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.gradient";

	/**
	 * Standard constructor.
	 */
	public Gradient() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				double b=(d+1.0)/2.0;
				return new RGB((int)(b*240.0),(int)(b*240.0),(int)(b*240.0));
			}
		}
		return new RGB(0,0,0);
	}
}
