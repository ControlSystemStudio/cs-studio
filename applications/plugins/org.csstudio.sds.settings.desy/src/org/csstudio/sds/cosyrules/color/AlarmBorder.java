package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.IRule;
import org.epics.css.dal.DynamicValueState;

/**
 * Rule to control the border style dependent on the severity.
 *
 * @author jhatje
 *
 */
public class AlarmBorder implements IRule {

	/**
	 * The ID for this rule.
	 */
	public static final String TYPE_ID = "cosyrules.color.alarmBorder";

	/**
	 * Standard constructor.
	 */
	public AlarmBorder() {
	}

	/**
	 * Set border style for non NORMAL severity to line to make the color
	 * visible. Handle DynamicValueState for DAL severities and Double for
	 * EPICS.SEVR.
	 */
	public Object evaluate(final Object[] arguments) {
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
				return BorderStyleEnum.NONE.getIndex();
			}
			if ((Math.abs(d - 1.0) < 0.00001)
					|| (s.equals(DynamicValueState.WARNING.toString()))) {
				return BorderStyleEnum.LINE.getIndex();
			}
			if ((Math.abs(d - 2.0) < 0.00001)
					|| (s.equals(DynamicValueState.ALARM.toString()))) {
				return BorderStyleEnum.LINE.getIndex();
			}
			if (((d >= 3.0) && (d <= 255.0))
					|| (s.equals(DynamicValueState.ERROR.toString()))) {
				return BorderStyleEnum.LINE.getIndex();
			}
		}

		return 0;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        String desc= "Only if the given arument a String "+DynamicValueState.NORMAL.toString()+" (DynamicValueState.NORMAL) or the argument is a Number between +- 0.00001 retrun a None-Border otherwise return a Line-Border.";
        return desc;
    }

}
