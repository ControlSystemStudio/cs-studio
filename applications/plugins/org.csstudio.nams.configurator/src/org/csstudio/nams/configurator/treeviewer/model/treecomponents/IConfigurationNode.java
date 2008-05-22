package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;

public interface IConfigurationNode {

	public Collection<AbstractConfigurationBean> getChildren();

	public String getName();

}
