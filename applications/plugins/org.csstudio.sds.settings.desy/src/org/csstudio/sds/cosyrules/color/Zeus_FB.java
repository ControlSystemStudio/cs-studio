package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Zeus_FB, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Zeus_FB implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.zeus-fb";

	/**
	 * Standard constructor.
	 */
	public Zeus_FB() {
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
			if (d>=0.0 && d<0.05) {
				return new RGB(253,0,0);
			}
			if (d>=0.05 && d<0.95) {
				return new RGB(164,170,255);
			}
			if (d>=0.95 && d<=1.0) {
				return new RGB(0,216,0);
			}
		}

		return new RGB(0,0,0);
	}
}
