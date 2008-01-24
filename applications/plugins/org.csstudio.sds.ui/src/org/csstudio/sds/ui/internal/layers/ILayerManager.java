package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.commands.CommandStack;

/**
 * Adapter interface which is used by the {@link LayerManagementView} to query
 * layer relevant data from active workbench parts using the standard adapter
 * mechanisms provided by {@link IAdaptable}.
 * 
 * @author swende
 * 
 */
public interface ILayerManager {
	/**
	 * Returns an object which provides access to the layer model.
	 * 
	 * @return an object which provides access to the layer model
	 */
	LayerSupport getLayerSupport();

	/**
	 * Returns the command stack, which should be used to execute commands that
	 * manipulate layer information (like layer order, visibility etc.).
	 * 
	 * @return the command stack, which should be used to execute commands that
	 *         manipulate layers
	 */
	CommandStack getCommandStack();
}
