package org.csstudio.nams.configurator.beans;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;

public interface IConfigurationModel {

	public IConfigurationBean save(IConfigurationBean bean, String groupName);

	public Collection<String> getSortgroupNames();
}
