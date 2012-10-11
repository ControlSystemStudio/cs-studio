/**
 * 
 */
package org.csstudio.beast.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeLeaf;
import org.csstudio.alarm.beast.ui.actions.AlarmTextHelper;
import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * @author shroffk
 * 
 */
public class LogbookBuilderFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == LogEntryBuilder.class) {
			if (adaptableObject instanceof AlarmTreeLeaf) {
				AlarmTreeLeaf alarmTreeLeaf = ((AlarmTreeLeaf) adaptableObject);
				List<AlarmTreeLeaf> alarms = new ArrayList<AlarmTreeLeaf>();
				alarms.add(alarmTreeLeaf);
				return LogEntryBuilder.withText(AlarmTextHelper
						.createAlarmInfoText(alarms));
			} else if (adaptableObject instanceof AlarmTreeItem) {
				AlarmTreeItem alarmTreeItem = ((AlarmTreeItem) adaptableObject);
				return LogEntryBuilder.withText("alarmTreeItem");
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] { LogEntryBuilder.class };
	}

}
