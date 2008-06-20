package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

/**
 * Abstrakte Klasse für alle Knoten, die weitere Kinder enthalten. Kapselt
 * haupsächlich die Funktionalität des Parent-Items und des Display-Names.
 * 
 * @author Eugen Reiswich
 * 
 * @param <NodeType>
 */
public abstract class AbstractConfigurationNode implements IConfigurationNode {

	private IConfigurationNode parent;

	private String name;

	public AbstractConfigurationNode(String name, IConfigurationNode parent) {
		this.name = name;
		this.parent = parent;
	}

	public IConfigurationNode getParent() {
		return parent;
	}

	public String getDisplayName() {
		return this.name;
	}

	public void setParent(IConfigurationGroup parent) {
		this.parent = parent;
	}
}
