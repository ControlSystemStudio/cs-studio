package org.csstudio.opibuilder.scriptUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;

public class AlarmUtil {
	/**
	 * Get the instance of AlarmClientModel. You must call release() method when you finish using this instnace.
	 * @return AlarmClientModel
	 */
	public static AlarmClientModel getAlarmClientModel() {
		try {
			return AlarmClientModel.getInstance();
		} catch (final Throwable ex) {
            Logger.getLogger("org.csstudio.opibuilder.alarm").log(Level.SEVERE, "Cannot load alarm model", ex); //$NON-NLS-1$
            return null;
        }
	}
}
