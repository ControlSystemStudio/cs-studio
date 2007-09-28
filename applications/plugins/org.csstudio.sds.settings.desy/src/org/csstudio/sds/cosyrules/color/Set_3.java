package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;

/**
 * Color rule Set#3, translated from an ADL file.
 * 
 * @author jbercic
 *
 */
public final class Set_3 implements IRule {
	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.set#3";

	/**
	 * Standard constructor.
	 */
	public Set_3() {
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
			if (d>=-0.01 && d<=0.01) {
				return new RGB(238,182,43);
			}
			if (d>=0.99 && d<=1.01) {
				return new RGB(225,144,21);
			}
			if (d>=1.99 && d<=2.01) {
				return new RGB(205,97,0);
			}
			if (d>=2.99 && d<=3.01) {
				return new RGB(255,176,255);
			}
			if (d>=3.99 && d<=4.01) {
				return new RGB(214,127,226);
			}
			if (d>=4.99 && d<=5.01) {
				return new RGB(174,78,188);
			}
			if (d>=5.99 && d<=6.01) {
				return new RGB(139,26,150);
			}
			if (d>=6.99 && d<=7.01) {
				return new RGB(97,10,117);
			}
			if (d>=7.99 && d<=8.01) {
				return new RGB(164,170,255);
			}
			if (d>=8.99 && d<=9.01) {
				return new RGB(135,147,226);
			}
			if (d>=9.99 && d<=10.01) {
				return new RGB(106,115,193);
			}
			if (d>=10.99 && d<=11.01) {
				return new RGB(77,82,164);
			}
			if (d>=11.99 && d<=12.01) {
				return new RGB(52,51,134);
			}
			if (d>=12.99 && d<=13.01) {
				return new RGB(199,187,109);
			}
			if (d>=13.99 && d<=14.01) {
				return new RGB(183,157,92);
			}
			if (d>=14.99 && d<=15.01) {
				return new RGB(164,126,60);
			}
		}
		return new RGB(0,0,0);
	}
}
