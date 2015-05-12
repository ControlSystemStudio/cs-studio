package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.dal.DynamicValueState;

/**
 * Rule to control the border style dependent on the severity.
 *
 * @author jhatje
 *
 */
public class AlarmBorder extends AbstractAlarmRule {

    /**
     * The ID for this rule.
     */
    public static final String TYPE_ID = "cosyrules.color.alarmBorder";

    /**
     * Standard constructor.
     */
    public AlarmBorder() {
        // Standard constructor.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        final String desc= "Only if the given argument a String "+DynamicValueState.NORMAL.toString()+" (DynamicValueState.NORMAL) or the argument is a Number between +- 0.00001 return a None-Border otherwise return a Line-Border.";
        return desc;
    }

    /**
     * Set border style for non NORMAL severity to line to make the color
     * visible. Handle DynamicValueState for DAL severities and Double for
     * EPICS.SEVR.
     */
    @Override
    protected Object evaluateWorker(final DynamicValueState dvc) {
        int style = BorderStyleEnum.DOTTED.getIndex();
        if (dvc != null) {
            switch (dvc) {
                case NORMAL:
                    style = BorderStyleEnum.NONE.getIndex();
                    break;
                case WARNING:
                    style = BorderStyleEnum.LINE.getIndex();
                    break;
                case ALARM:
                    style = BorderStyleEnum.LINE.getIndex();
                    break;
                case ERROR:
                    style = BorderStyleEnum.LINE.getIndex();
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
        return style;
    }

}
