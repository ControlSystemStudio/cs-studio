package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

/**
 * Ein {@link IConfigurationNode} ist grundsätzlich jedes Element des
 * Configuration-Trees. Allen Elementen gemein ist, dass diese einen Namen
 * haben, der im Tree angezeigt wird und einem Parent-Element zugeordnet sind.
 * 
 * @author Eugen Reiswich
 * 
 */
public interface IConfigurationNode {

	public IConfigurationNode getParent();

	public String getDisplayName();
}
