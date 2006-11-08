package org.csstudio.platform.model;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Interface for control system items. It should be used as base interface for
 * control system item interface hierarchies.
 * 
 * As control system items are supposed to inherit from
 * {@link AbstractControlSystemItem}, which already implements this interface, there
 * are no further implementation efforts.
 * 
 * Among other things, the interface is used to identify control system items in
 * object contributions for popup menus or other data exchange functionalities
 * between CSS applications.
 * 
 * @author Kay Kasemir, swende
 */
public interface IControlSystemItem extends IAdaptable {

	/**
	 * @return Returns the name of the control system item.
	 */
	String getName();

	/**
	 * Gets the type identifier for this control system item.
	 * 
	 * @return the type identifier
	 */
	String getTypeId();
}
