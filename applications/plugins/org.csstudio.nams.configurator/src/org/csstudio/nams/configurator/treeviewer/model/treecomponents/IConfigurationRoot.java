package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

/**
 * Das {@link IConfigurationRoot} Interface weist Methoden auf, die für
 * Root-Items benötigt werden.
 * 
 * @author Eugen Reiswich
 * 
 */
public interface IConfigurationRoot extends IConfigurationNode {

	public Collection<IConfigurationGroup> getChildren();

	public ConfigurationType getConfigurationType();

	public void addChild(IConfigurationGroup group);
}
