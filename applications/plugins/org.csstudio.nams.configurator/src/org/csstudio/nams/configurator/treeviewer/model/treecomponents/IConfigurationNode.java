package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public interface IConfigurationNode {

	public Collection<ConfigurationBean> getChildren();

	public String getName();

}
