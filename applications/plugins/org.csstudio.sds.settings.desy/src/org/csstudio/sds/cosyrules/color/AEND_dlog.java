package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule AEND_dlog, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class AEND_dlog implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.aend_dlog";

	/**
	 * Standard constructor.
	 */
	public AEND_dlog() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
            double d = 0.0;
            if (arguments[0] instanceof Double) {
                 d = (Double) arguments[0];
            }else if (arguments[0] instanceof Long) {
                d = ((Long)  arguments[0]).doubleValue();
            }
			if (Math.abs(d-0.00)<0.00001) {
				return new RGB(253,0,0);
			}
			if (Math.abs(d-1.00)<0.00001) {
				return new RGB(42,99,228);
			}
			if (Math.abs(d-2.00)<0.00001) {
				return new RGB(30,187,0);
			}
			if (d>=3.00 && d<=15.00) {
				return new RGB(249,218,60);
			}
			if (Math.abs(d-16.00)<0.00001) {
				return new RGB(255,255,255);
			}
		}

		return new RGB(0,0,0);
	}
}
