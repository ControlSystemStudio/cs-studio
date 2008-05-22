package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;

/**
 * Die {@link SortGroupBean} enthält Kider, die im TreeViewer des Configurators
 * dargestellt werden. Da eine Gruppe "global" verwendet wird, kann (muss aber
 * nicht) eine Gruppe verschiedene Kinder beinhalten (siehe die privaten
 * Collections).
 * 
 * @author Eugen Reiswich
 * 
 */
public class SortGroupBean extends AbstractConfigurationBean<SortGroupBean> {

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

	@Override
	public String getDisplayName() {
		return getGroupName();
	}

	@Override
	public void copyStateOf(SortGroupBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}
}
