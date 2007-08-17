package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Kryo-Dig, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Kryo_Dig implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.kryo-dig";

	/**
	 * Standard constructor.
	 */
	public Kryo_Dig() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			if (arguments[0] instanceof Double) {
				double d = (Double) arguments[0];
				
				if (Math.abs(d-1.00)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-0.00)<0.00001) {
					return new RGB(253,0,0);
				}
				if (Math.abs(d-2.00)<0.00001) {
					return new RGB(190,25,11);
				}
				if (Math.abs(d-3.00)<0.00001) {
					return new RGB(51,153,0);
				}
				if (Math.abs(d-4.00)<0.00001) {
					return new RGB(255,176,255);
				}
				if (Math.abs(d-5.00)<0.00001) {
					return new RGB(235,241,181);
				}
				if (Math.abs(d-6.00)<0.00001) {
					return new RGB(164,170,255);
				}
				if (Math.abs(d-7.00)<0.00001) {
					return new RGB(164,170,255);
				}
				if (Math.abs(d-8.00)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-9.00)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-10.00)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-11.00)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-12.00)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-13.00)<0.00001) {
					return new RGB(238,182,43);
				}
				if (Math.abs(d-14.00)<0.00001) {
					return new RGB(251,243,74);
				}
				if (Math.abs(d-15.00)<0.00001) {
					return new RGB(238,182,43);
				}
			}
		}

		return new RGB(0,0,0);
	}
}
