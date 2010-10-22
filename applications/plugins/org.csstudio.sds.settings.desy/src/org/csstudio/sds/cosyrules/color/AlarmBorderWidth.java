package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.IRule;
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
	public Object evaluate(final Object[] arguments) {
		int width = 0;
		if ((arguments != null) && (arguments.length > 0)) {
			for (int i = 0; i < arguments.length; i++) {

				double d = 300.0;
				String s = "init";
				if (arguments[i] instanceof Double) {
					d = (Double) arguments[i];
				} else if (arguments[i] instanceof Long) {
					d = ((Long) arguments[i]).doubleValue();
				} else if (arguments[i] instanceof String) {
					s = (String) arguments[i];
				}

				if ((Math.abs(d - 0.0) < 0.00001)
						|| (s.equals(DynamicValueState.NORMAL.toString()))) {
					if(width<0){
						width = 0;
					}
				} else if ((Math.abs(d - 1.0) < 0.00001)
						|| (s.equals(DynamicValueState.WARNING.toString()))) {
					if(width<3){
						width = 3;
					}
				} else if ((Math.abs(d - 2.0) < 0.00001)
						|| (s.equals(DynamicValueState.ALARM.toString()))) {
					if(width<3){
						width = 3;
					}
				} else if (((d >= 3.0) && (d <= 255.0))
						|| (s.equals(DynamicValueState.ERROR.toString()))) {
					if(width<3){
						width = 3;
					}
				}
			}
		}
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "Liefert die Line width abhängig vom übergeben Wert.\r\n Es können mehrere Channel übergeben werden. Es wird die Line width für die höchste Prioirät genommen.\r\n Ist der Value größer 0 oder \"WARNING\",\"ALARM\",\"ERROR\",  wird 3 zurückgeben ansonsten 0";
	}

}
