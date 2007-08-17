package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Kryo-TTF, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Kryo_TTF implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.kryo-ttf";

	/**
	 * Standard constructor.
	 */
	public Kryo_TTF() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (d>=0.0 && d<=4.00) {
					return new RGB(253,0,0);
				}
				if (d>=4.50 && d<=95.00) {
					return new RGB(164,170,255);
				}
				if (d>=95.1 && d<=101.0) {
					return new RGB(0,216,0);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
