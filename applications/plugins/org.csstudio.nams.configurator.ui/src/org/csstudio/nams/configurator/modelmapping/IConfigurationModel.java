package org.csstudio.nams.configurator.modelmapping;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

@Deprecated
public interface IConfigurationModel {
	@Deprecated
	public <E extends IConfigurationBean> E save(E bean);
}
