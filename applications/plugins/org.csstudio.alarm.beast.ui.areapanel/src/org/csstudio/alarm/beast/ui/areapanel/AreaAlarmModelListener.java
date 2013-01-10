package org.csstudio.alarm.beast.ui.areapanel;

public interface AreaAlarmModelListener
{
	/** Invoked when the overall {@link AreaAlarmModel} has changed */
	public void areaModelChanged();

	/** Invoked when some alarm(s) changed */
	public void alarmsChanged();

	/** Invoked when the server communication times out.
	 *  <p>
	 *  On recovery, there will be alarmsChanged calls
	 */
	public void serverTimeout();
}
