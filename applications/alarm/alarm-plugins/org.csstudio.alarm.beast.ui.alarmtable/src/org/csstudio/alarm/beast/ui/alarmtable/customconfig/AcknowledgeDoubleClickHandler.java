package org.csstudio.alarm.beast.ui.alarmtable.customconfig;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.security.SecuritySupport;

/**
 * 
 * <code>AcknowledgeDoubleClickHandler</code> acknowledges alarms if double click originates
 * in the active table or unacknowledges if it origintas in the acknowledged table. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AcknowledgeDoubleClickHandler implements DoubleClickHandler {

    @Override
    public void acknowledgedTableDoubleClicked(AlarmTreePV pv) {
        if (SecuritySupport.havePermission(AuthIDs.ACKNOWLEDGE)) {
            pv.acknowledge(false);
        }
    }

    @Override
    public void activeTableDoubleClicked(AlarmTreePV pv) {
        if (SecuritySupport.havePermission(AuthIDs.ACKNOWLEDGE)) {
            pv.acknowledge(true);
        }
    }
}
