package org.csstudio.nams.configurator.treeviewer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeitergruppenNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmtopicNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.FilterNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.FilterbedingungNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortGroupBean;

/**
 * Diese Klasse ist das Model für den TreeViewer des AMS-Configurators.
 * 
 * @author eugrei
 * 
 */
public class ConfigurationModel {

	private Map<String, SortGroupBean> sortgroupBeans;

	private AlarmbearbeiterNode alarmbearbeiterNode;

	private AlarmtopicNode alarmtopicNode;

	private AlarmbearbeitergruppenNode alarmbearbeitergruppenNode;

	private FilterbedingungNode filterbedingungNode;

	private FilterNode filterNode;

	public ConfigurationModel(Collection<SortGroupBean> sortgroupBeans) {
		this.initBeans(sortgroupBeans);
	}

	/**
	 * Initialisiert die GroupBeans-Map, indem als key der Gruppenname verwendet
	 * wird und als value die Gruppe an sich
	 * 
	 * @param groupBeans
	 */
	private void initBeans(Collection<SortGroupBean> groupBeans) {
		this.sortgroupBeans = new HashMap<String, SortGroupBean>();

		for (SortGroupBean bean : groupBeans) {
			this.sortgroupBeans.put(bean.getGroupName(), bean);
		}

		this.alarmbearbeiterNode = new AlarmbearbeiterNode(this.sortgroupBeans);
		this.alarmbearbeitergruppenNode = new AlarmbearbeitergruppenNode(
				this.sortgroupBeans);
		this.alarmtopicNode = new AlarmtopicNode(this.sortgroupBeans);
		this.filterbedingungNode = new FilterbedingungNode(this.sortgroupBeans);
		this.filterNode = new FilterNode(this.sortgroupBeans);
	}

	public AlarmbearbeiterNode getAlarmbearbeiterNode() {
		return alarmbearbeiterNode;
	}

	/**
	 * Liefert alle Kinder des Models.
	 * 
	 * @return
	 */
	public Collection<IConfigurationNode> getChildren() {
		Collection<IConfigurationNode> children = new ArrayList<IConfigurationNode>();

		children.add(alarmbearbeiterNode);
		children.add(alarmtopicNode);
		children.add(alarmbearbeitergruppenNode);
		children.add(filterbedingungNode);
		children.add(filterNode);

		return children;
	}

	public Collection<String> getSortgroupNames() {
		return sortgroupBeans.keySet();
	}

	public void save(ObservableBean bean, String groupName) {

	}

}
