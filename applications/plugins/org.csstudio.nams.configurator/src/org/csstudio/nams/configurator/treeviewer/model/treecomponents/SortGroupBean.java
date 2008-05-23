package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.IConfigurationBean;

/**
 * Die {@link SortGroupBean} enthält Kider, die im TreeViewer des Configurators
 * dargestellt werden. Da eine Gruppe "global" verwendet wird, kann (muss aber
 * nicht) eine Gruppe verschiedene Kinder beinhalten (siehe die privaten
 * Collections).
 * 
 * @author Eugen Reiswich
 * 
 */
public class SortGroupBean implements IConfigurationBean {

	private String groupName;

	private int groupID;

	private Collection<AlarmbearbeiterBean> alarmbearbeiterBeans;

	private Collection<AlarmtopicBean> alarmtopicBeans;

	private Collection<AlarmbearbeitergruppenBean> alarmbearbeitergruppenBeans;

	private Collection<FilterbedingungBean> filterbedingungBeans;

	private Collection<FilterBean> filterBeans;

	/**
	 * Constructor
	 * 
	 * @param ID
	 *            Die Gruppen ID
	 * @param name
	 *            Der Gruppenname
	 */
	public SortGroupBean(int ID, String name) {
		this.groupID = ID;
		this.groupName = name;

		// init collections
		this.alarmbearbeiterBeans = new ArrayList<AlarmbearbeiterBean>();
		this.alarmtopicBeans = new ArrayList<AlarmtopicBean>();
		this.alarmbearbeitergruppenBeans = new ArrayList<AlarmbearbeitergruppenBean>();
		this.filterbedingungBeans = new ArrayList<FilterbedingungBean>();
		this.filterBeans = new ArrayList<FilterBean>();
	}

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

	public String getGroupName() {
		return groupName;
	}

	public int getGroupID() {
		return groupID;
	}

	public String getDisplayName() {
		return getGroupName();
	}

	public void copyStateOf(SortGroupBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	public void setAlarmbearbeiterBeans(
			Collection<AlarmbearbeiterBean> alarmbearbeiterBeans) {
		this.alarmbearbeiterBeans = alarmbearbeiterBeans;
	}

	public void setAlarmtopicBeans(Collection<AlarmtopicBean> alarmtopicBeans) {
		this.alarmtopicBeans = alarmtopicBeans;
	}

	public void setAlarmbearbeitergruppenBeans(
			Collection<AlarmbearbeitergruppenBean> alarmbearbeitergruppenBeans) {
		this.alarmbearbeitergruppenBeans = alarmbearbeitergruppenBeans;
	}

	public void setFilterbedingungBeans(
			Collection<FilterbedingungBean> filterbedingungBeans) {
		this.filterbedingungBeans = filterbedingungBeans;
	}

	public void setFilterBeans(Collection<FilterBean> filterBeans) {
		this.filterBeans = filterBeans;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
