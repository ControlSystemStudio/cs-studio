package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.ObservableBean;

public interface IConfigurationNode {

	public Collection<ObservableBean> getChildren();

	public String getName();

}
