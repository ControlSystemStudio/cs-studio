package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public class SortgroupNode {

	private final ConfigurationType groupType;
	private final Collection<? extends ConfigurationBean> beans;
	private final String name;

	public SortgroupNode(String name,
			Collection<? extends ConfigurationBean> collection,
			ConfigurationType groupType) {
		this.name = name;
		this.beans = collection;
		this.groupType = groupType;
	}

	public ConfigurationType getGroupType() {
		return groupType;
	}

	/**
	 * Liefert die Mitglieder einer Gruppe abhängig vom GroupType
	 * 
	 * @return
	 */
	public Collection<? extends ConfigurationBean> getChildren() {
		return beans;
	}

	public String getDisplayName() {
		return name;
	}
}
