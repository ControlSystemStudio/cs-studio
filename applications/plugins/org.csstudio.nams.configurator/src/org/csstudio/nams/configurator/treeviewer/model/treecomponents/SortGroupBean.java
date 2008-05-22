package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

/**
 * Die {@link SortGroupBean} enthält Kider, die im TreeViewer des Configurators
 * dargestellt werden. Da eine Gruppe "global" verwendet wird, kann (muss aber
 * nicht) eine Gruppe verschiedene Kinder beinhalten (siehe die privaten
 * Collections).
 * 
 * @author Eugen Reiswich
 * 
 */
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
