package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.client.AlarmTreeItem;

/**
 *
 * <code>DropListener</code> provides a mechanism to receive to drop events from the GUI.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface DropListener
{
    /**
     * Called when a drop event occurs on the GUI and the dropped item can be cast to {@link AlarmTreeItem}.
     * There is no guarantee that the dropped item belongs to any active alarm model nor that it has all the
     * fields initialised.
     *
     * @param item the item that was dropped
     */
    public void handleDrop(AlarmTreeItem item);
}
