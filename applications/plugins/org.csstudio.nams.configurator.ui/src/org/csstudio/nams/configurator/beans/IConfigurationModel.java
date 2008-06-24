package org.csstudio.nams.configurator.beans;

import java.util.Collection;

public interface IConfigurationModel {

	public IConfigurationBean save(IConfigurationBean bean, String groupName);

	public Collection<String> getSortgroupNames();
}
