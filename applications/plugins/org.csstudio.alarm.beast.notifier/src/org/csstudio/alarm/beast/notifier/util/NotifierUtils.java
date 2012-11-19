package org.csstudio.alarm.beast.notifier.util;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.csstudio.data.values.ITimestamp;

/**
 * Alarm Notifier utilities.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class NotifierUtils {

	/**
	 * Create an {@link AlarmUpdateInfo} from an {@link AlarmTreePV}.
	 * @param pv
	 * @return
	 */
	public static AlarmUpdateInfo getInfofromPVItem(final AlarmTreePV pv)
    {
        final String name = pv.getPathName();
        final SeverityLevel severity = pv.getSeverity();
        final String status = pv.getMessage();
        final SeverityLevel current_severity = pv.getCurrentSeverity();
        final String current_message = pv.getCurrentMessage();
        final String value = pv.getValue();
        final ITimestamp timestamp = pv.getTimestamp();
        return new AlarmUpdateInfo(name, current_severity, current_message,
                severity, status, value, timestamp);
    }
}
