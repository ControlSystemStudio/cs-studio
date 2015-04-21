package org.csstudio.alarm.beast.ui.alarmtable.customconfig;

import org.csstudio.alarm.beast.client.AlarmTreePV;

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
        pv.acknowledge(false);
    }

    @Override
    public void activeTableDoubleClicked(AlarmTreePV pv) {
        pv.acknowledge(true);
    }
}
