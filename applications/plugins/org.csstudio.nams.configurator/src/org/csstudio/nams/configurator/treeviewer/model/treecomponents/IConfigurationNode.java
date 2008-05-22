package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

public interface IConfigurationNode {

	public Collection<SortgroupNode> getChildren();

	public String getName();

	public ConfigurationType getConfigurationType();

}
