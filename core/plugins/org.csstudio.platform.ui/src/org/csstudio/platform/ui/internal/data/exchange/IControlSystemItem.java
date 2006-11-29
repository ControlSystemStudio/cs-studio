package org.csstudio.platform.ui.internal.data.exchange;

/**
 * Interface to a generic control system item.
 * <p>
 * Used to identify and name control system items for context menu object
 * contributions or other data exchange between CSS applications.
 * 
 * Its recommended not to implement this interface directly, but inherit your
 * classes from {@link IControlSystemItem}.
 * 
 * @see org.csstudio.platform.ui.internal.data.exchange.IProcessVariableName
 * @see org.csstudio.platform.ui.internal.data.exchange.IFrontEndControllerName
 * @author Kay Kasemir, Sven Wende
 */
public interface IControlSystemItem {
	/** @return Returns the name of the control system item. */
	public String getName();
}
