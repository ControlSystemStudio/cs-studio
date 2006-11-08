package org.csstudio.platform.model;

/**
 * Interface for a process variable item.
 * 
 * This interface is not intended to be implemented by clients. Instances of
 * ProcessVariables can be created via the
 * 
 * @see {@link ControlSystemItemFactory} factory.
 * 
 * @author Kay Kasemir, swende
 */
public interface IProcessVariable extends IControlSystemItem {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:processVariable"; //$NON-NLS-1$
}
