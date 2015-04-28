package org.csstudio.alarm.beast.ui.alarmtable.customconfig;

import org.csstudio.alarm.beast.client.AlarmTreePV;

/**
 * 
 * <code>DoubleClickHandler</code> is called when an item in the alarm table is double clicked. Depending on
 * which table was double-clicked, the appropriate method is called.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface DoubleClickHandler {
    
    /** The extension id */
    public static final String EXTENSION_ID = "org.csstudio.alarm.beast.ui.alarmtable.customconfig";
    /** The extension config element name */
    public static final String NAME = "doubleClickHandler";
    
    /**
     * Called when the active alarms table was double-clicked.
     * 
     * @param pv the double-clicked item 
     */
    public void activeTableDoubleClicked(AlarmTreePV pv);
    
    /**
     * Called when the acknowledged alarms table was double-clicked.
     * 
     * @param pv the double-clicked item
     */
    public void acknowledgedTableDoubleClicked(AlarmTreePV pv);

}
