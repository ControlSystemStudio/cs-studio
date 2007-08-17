package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Set#5, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Set_5 implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.set#5";

	/**
	 * Standard constructor.
	 */
	public Set_5() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-0.00)<0.00001) {
					return new RGB(0,216,0);
				}
				if (Math.abs(d-1.00)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-2.00)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-3.00)<0.00001) {
					return new RGB(255,255,255);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
