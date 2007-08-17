package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule MKK-10KV, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class MKK_10KV implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.mkk-10kv";

	/**
	 * Standard constructor.
	 */
	public MKK_10KV() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-0.00)<0.00001) {
					return new RGB(187,193,135);
				}
				if (Math.abs(d-1.00)<0.00001) {
					return new RGB(255,176,255);
				}
				if (Math.abs(d-2.00)<0.00001) {
					return new RGB(153,255,255);
				}
				if (Math.abs(d-3.00)<0.00001) {
					return new RGB(42,99,228);
				}
				if (Math.abs(d-4.00)<0.00001) {
					return new RGB(205,97,0);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
