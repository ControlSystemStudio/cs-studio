package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule BIN_trennstlg, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class BIN_trennstlg implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.bin_trennstlg";

	/**
	 * Standard constructor.
	 */
	public BIN_trennstlg() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-1.00)<0.00001) {
					return new RGB(222,19,9);
				}
				if (Math.abs(d-2.00)<0.00001) {
					return new RGB(187,187,187);
				}
				if (d>=1.01 && d<=1.99) {
					return new RGB(253,0,0);
				}
				if (d>=2.01 && d<=65535.0) {
					return new RGB(253,0,0);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
