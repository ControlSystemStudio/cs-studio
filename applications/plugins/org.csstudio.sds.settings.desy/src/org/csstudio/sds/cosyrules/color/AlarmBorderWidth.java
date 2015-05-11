package org.csstudio.sds.cosyrules.color;

import org.csstudio.dal.DynamicValueState;

/**
 * Rule to control the border width dependent on the severity.
 *
 * @author jhatje
 *
 */
public class AlarmBorderWidth extends AbstractAlarmRule {

    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.alarmBorderWidth";

    /**
     * Standard constructor.
     */
    public AlarmBorderWidth() {
        // Standard constructor.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Liefert die Line width abhängig vom übergeben Wert.\r\n Es können mehrere Channel übergeben werden. Es wird die Line width für die höchste Prioirät genommen.\r\n Ist der Value größer 0 oder \"WARNING\",\"ALARM\",\"ERROR\",  wird 3 zurückgeben ansonsten 0";
    }

    /**
     * Set border width for non NORMAL severity to line to make the color
     * visible. Handle DynamicValueState for DAL severities and Double for
     * EPICS.SEVR.
     */
    @Override
    protected Object evaluateWorker(final DynamicValueState dvc) {
        int width = 3;
        if (dvc != null) {
            switch (dvc) {
                case NORMAL:
                    width=0;
                    break;
                case WARNING:
                    width=3;
                    break;
                case ALARM:
                    width=3;
                    break;
                case ERROR:
                    width=3;
                    break;
                case HAS_LIVE_DATA:
                    break;
                case HAS_METADATA:
                    break;
                case LINK_NOT_AVAILABLE:
                    break;
                case NO_VALUE:
                    break;
                case TIMELAG:
                    break;
                case TIMEOUT:
                    break;
            }
        }
        return width;
    }

}
