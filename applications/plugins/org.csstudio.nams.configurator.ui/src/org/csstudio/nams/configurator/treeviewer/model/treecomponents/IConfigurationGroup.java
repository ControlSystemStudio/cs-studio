package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.Collection;

/**
 * Das {@link IConfigurationGroup} Interface weist Methode auf, die für
 * Gruppen-Nodes benötigt werden.
 * 
 * @author Eugen Reiswich
 * 
 */
public interface IConfigurationGroup extends IConfigurationNode {

	public Collection<? extends IConfigurationBean> getChildren();

	public ConfigurationType getConfigurationType();
}
