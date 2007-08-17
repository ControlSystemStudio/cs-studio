package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Set#2, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Set_2 implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.set#2";

	/**
	 * Standard constructor.
	 */
	public Set_2() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-0.00)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-1.00)<0.00001) {
					return new RGB(130,4,0);
				}
				if (Math.abs(d-2.00)<0.00001) {
					return new RGB(30,187,0);
				}
				if (Math.abs(d-3.00)<0.00001) {
					return new RGB(33,108,0);
				}
				if (Math.abs(d-4.00)<0.00001) {
					return new RGB(235,241,181);
				}
				if (Math.abs(d-5.00)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-6.00)<0.00001) {
					return new RGB(42,99,228);
				}
				if (Math.abs(d-7.00)<0.00001) {
					return new RGB(10,0,184);
				}
				if (Math.abs(d-8.00)<0.00001) {
					return new RGB(199,187,109);
				}
				if (Math.abs(d-9.00)<0.00001) {
					return new RGB(164,126,60);
				}
				if (Math.abs(d-10.00)<0.00001) {
					return new RGB(164,170,255);
				}
				if (Math.abs(d-11.00)<0.00001) {
					return new RGB(106,115,193);
				}
				if (Math.abs(d-12.00)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-13.00)<0.00001) {
					return new RGB(225,144,21);
				}
				if (Math.abs(d-14.00)<0.00001) {
					return new RGB(187,187,187);
				}
				if (Math.abs(d-15.00)<0.00001) {
					return new RGB(0,0,0);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
