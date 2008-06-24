package org.csstudio.nams.configurator.modelmapping;

import java.util.Collection;

public interface IConfigurationModel {

	public IConfigurationBean save(IConfigurationBean bean);

	public Collection<String> getSortgroupNames();
}
