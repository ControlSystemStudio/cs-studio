package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule BIN_0rt_1gn, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class BIN_0rt_1gn implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.bin_0rt_1gn";

	/**
	 * Standard constructor.
	 */
	public BIN_0rt_1gn() {
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
			if (d>=1.00 && d<=65535.0) {
				return new RGB(30,187,0);
			}
		}

		return new RGB(0,0,0);
	}
}
