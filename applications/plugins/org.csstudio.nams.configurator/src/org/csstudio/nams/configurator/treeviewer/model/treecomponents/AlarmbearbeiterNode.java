package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;
import java.util.Map;

import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;

public class AlarmbearbeiterNode implements IConfigurationNode {

	private String name = "Alarmbearbeiter";
	private SortgroupNode sortgroupNode;

	/**
	 * Konstruktor
	 * 
	 * @param map
	 */
	public AlarmbearbeiterNode(Map<String, SortGroupBean> map) {
		this.sortgroupNode = new SortgroupNode(map,
				SortgroupNode.GroupType.ALARMBEATERBEITER);
	}

	public String getName() {
		return name;
	}

	public Collection<AbstractConfigurationBean> getChildren() {
		return this.sortgroupNode.getChildren();
	}

}
