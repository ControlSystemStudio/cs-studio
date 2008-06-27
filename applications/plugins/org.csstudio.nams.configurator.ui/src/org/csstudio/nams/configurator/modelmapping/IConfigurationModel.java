package org.csstudio.nams.configurator.modelmapping;


public interface IConfigurationModel {

	public <E extends IConfigurationBean> E save(E bean);

}
