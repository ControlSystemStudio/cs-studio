package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Die {@link ConfigurationNode} sind die obersten Elemente eines
 * ConfigurationTrees. Kinder der ConfigurationNodes sind Gruppen.
 * 
 * @author Eugen Reiswich
 * 
 */
public class ConfigurationNode extends AbstractConfigurationNode implements
		IConfigurationRoot {

	private Collection<IConfigurationGroup> groupNodes;
	private final ConfigurationType configurationType;

	public ConfigurationNode(ConfigurationType configurationType) {
		super(configurationType.getDisplayName(), null);
		this.configurationType = configurationType;
		this.groupNodes = new ArrayList<IConfigurationGroup>();
	}

	/**
	 * Konstruktor mit Gruppen-Nodes
	 * 
	 * @param groupNodes
	 * @param configurationType
	 */
	public ConfigurationNode(Collection<IConfigurationGroup> groupNodes,
			ConfigurationType configurationType) {
		super(configurationType.getDisplayName(), null);
		this.groupNodes = groupNodes;
		this.configurationType = configurationType;
	}

	public Collection<IConfigurationGroup> getChildren() {
		return this.groupNodes;
	}

	public void setChildren(Collection<IConfigurationGroup> children) {
		this.groupNodes = children;
	}

	public void addChild(IConfigurationGroup group) {
		this.getChildren().add(group);
	}

	public ConfigurationType getConfigurationType() {
		return this.configurationType;
	}
}
