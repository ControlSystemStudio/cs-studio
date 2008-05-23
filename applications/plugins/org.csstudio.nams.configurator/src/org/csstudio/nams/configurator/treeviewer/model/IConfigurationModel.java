package org.csstudio.nams.configurator.treeviewer.model;

import java.util.Collection;

public interface IConfigurationModel {

	public void save(IConfigurationBean bean, String groupName);

	public Collection<String> getSortgroupNames();
}
