package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

public class ConfigurationNode implements IConfigurationNode {

	private final Collection<SortgroupNode> groupNodes;
	private final ConfigurationType configurationType;

	public ConfigurationType getConfigurationType() {
		return configurationType;
	}

	public ConfigurationNode(Collection<SortgroupNode> groupNodes,
			ConfigurationType configurationType) {
		this.groupNodes = groupNodes;
		this.configurationType = configurationType;
	}

	public Collection<SortgroupNode> getChildren() {
		return groupNodes;
	}

	public String getName() {
		return configurationType.getDisplayName();
	}

}
