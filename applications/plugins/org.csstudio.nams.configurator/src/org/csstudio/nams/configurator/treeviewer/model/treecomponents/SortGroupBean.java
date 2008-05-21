package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

public class SortGroupBean {
	
	String groupName;

	private Collection<AlarmbearbeiterBean> alarmbearbeiterBeans;

	private Collection<AlarmtopicBean> alarmtopicBeans;

	private Collection<AlarmbearbeitergruppenBean> alarmbearbeitergruppenBeans;

	private Collection<FilterbedingungBean> filterbedingungBeans;

	private Collection<FilterBean> filterBeans;

	public Collection<AlarmbearbeiterBean> getAlarmbearbeiterBeans() {
		return alarmbearbeiterBeans;
	}

	public Collection<AlarmtopicBean> getAlarmtopicBeans() {
		return alarmtopicBeans;
	}

	public Collection<AlarmbearbeitergruppenBean> getAlarmbearbeitergruppenBeans() {
		return alarmbearbeitergruppenBeans;
	}

	public Collection<FilterbedingungBean> getFilterbedingungBeans() {
		return filterbedingungBeans;
	}

	public Collection<FilterBean> getFilterBeans() {
		return filterBeans;
	}
}
