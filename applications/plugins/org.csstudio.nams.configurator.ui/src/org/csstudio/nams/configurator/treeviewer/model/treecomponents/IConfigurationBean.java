package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

public interface IConfigurationBean extends IConfigurationNode {

	public int getID();

	public void setParent(IConfigurationGroup parent);

	public IConfigurationGroup getParent();

}
