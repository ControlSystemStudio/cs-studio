package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.logic.IRule;
import org.eclipse.swt.graphics.RGB;
import org.epics.css.dal.DynamicValueState;

/**
 * Rule to control the border width dependent on the severity.
 * 
 * @author jhatje
 * 
 */
public class AlarmBorderWidth implements IRule {

	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.alarmBorderWidth";

	/**
	 * Standard constructor.
	 */
	public AlarmBorderWidth() {
	}

	/**
	 * Set border width for non NORMAL severity to line to make the color
	 * visible. Handle DynamicValueState for DAL severities and Double for
	 * EPICS.SEVR.
	 */
	public Object evaluate(Object[] arguments) {
		if ((arguments != null) && (arguments.length > 0)) {
			double d = 300.0;
			String s = "init";
			if (arguments[0] instanceof Double) {
				d = (Double) arguments[0];
			} else if (arguments[0] instanceof Long) {
				d = ((Long) arguments[0]).doubleValue();
			} else if (arguments[0] instanceof String) {
				s = (String) arguments[0];
			}

			if ((Math.abs(d - 0.0) < 0.00001)
					|| (s.equals(DynamicValueState.NORMAL.toString()))) {
				return 0;
			}
			if ((Math.abs(d - 1.0) < 0.00001)
					|| (s.equals(DynamicValueState.WARNING.toString()))) {
				return 3;
			}
			if ((Math.abs(d - 2.0) < 0.00001)
					|| (s.equals(DynamicValueState.ALARM.toString()))) {
				return 3;
			}
			if ((d >= 3.0 && d <= 255.0)
					|| (s.equals(DynamicValueState.ERROR.toString()))) {
				return 3;
			}
		}

		return 0;
	}

}
