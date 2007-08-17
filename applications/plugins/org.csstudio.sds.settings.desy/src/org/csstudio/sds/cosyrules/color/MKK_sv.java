package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule MKK_sv, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class MKK_sv implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.mkk_sv";

	/**
	 * Standard constructor.
	 */
	public MKK_sv() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-0.0)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-1.0)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-2.0)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-3.0)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-4.0)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-5.0)<0.00001) {
					return new RGB(115,255,107);
				}
				if (Math.abs(d-6.0)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-7.0)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-8.0)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-9.0)<0.00001) {
					return new RGB(45,127,0);
				}
				if (Math.abs(d-10.0)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-11.0)<0.00001) {
					return new RGB(238,182,43);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
