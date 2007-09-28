package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule CRYO_Temp#1, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class CRYO_Temp_1 implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.cryo_temp#1";

	/**
	 * Standard constructor.
	 */
	public CRYO_Temp_1() {
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
            
			if (d>=0.0 && d<5.0) {
				return new RGB(42,99,228);
			}
			if (d>=5.0 && d<9.0) {
				return new RGB(78,165,249);
			}
			if (d>=9.0 && d<20.0) {
				return new RGB(238,182,43);
			}
			if (d>=20.0 && d<80.0) {
				return new RGB(205,97,0);
			}
			if (d>=80.0 && d<=1000.0) {
				return new RGB(190,25,11);
			}
		}

		return new RGB(0,0,0);
	}
}
