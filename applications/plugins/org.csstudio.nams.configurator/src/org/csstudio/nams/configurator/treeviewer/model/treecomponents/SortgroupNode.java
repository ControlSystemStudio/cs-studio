package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.IConfigurationBean;

public class SortgroupNode {

	private final ConfigurationType groupType;
	private final Collection<? extends IConfigurationBean> beans;
	private final String name;

	public SortgroupNode(String name,
			Collection<? extends IConfigurationBean> collection,
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
	public Collection<? extends IConfigurationBean> getChildren() {
		/*
		 * falls Kinder null sind, erzeuge eine leere Liste. Andernfalls wird
		 * eine NullpointerException im TreeViewer in der Methode hasChildren
		 * geworfen
		 */
		return beans == null ? new ArrayList<IConfigurationBean>() : beans;
	}

	public String getDisplayName() {
		return name;
	}
}
