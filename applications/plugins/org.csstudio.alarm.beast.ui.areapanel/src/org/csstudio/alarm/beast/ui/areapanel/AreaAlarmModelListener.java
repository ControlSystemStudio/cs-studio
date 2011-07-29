package org.csstudio.alarm.beast.ui.areapanel;

public interface AreaAlarmModelListener
{
	/** Invoked when the overall {@link AreaAlarmModel} has changed */
	public void areaModelChanged();

	/** Invoked when some alarm(s) changed */
	public void alarmsChanged();
}
