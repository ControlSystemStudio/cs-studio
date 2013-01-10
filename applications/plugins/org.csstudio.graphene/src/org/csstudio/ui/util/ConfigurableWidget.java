package org.csstudio.ui.util;

/**
 * A widget that can be configured and can open up a 
 * dialog to prompt the user for configuration
 * 
 * @author Gabriele Carcassi
 */
public interface ConfigurableWidget {
	
	/**
	 * True if the specific instance is configurable.
	 * 
	 * @return true if it can be configured
	 */
	public boolean isConfigurable();
	
	/**
	 * Changes whether this specific instance can be configured
	 * or not.
	 * 
	 * @param configurable true if it can be configured
	 */
	public void setConfigurable(boolean configurable);
	
	/**
	 * Opens the configuration dialog for the widget.
	 */
	public void openConfigurationDialog();
	
	/**
	 * True if the configuration dialog is already open.
	 * 
	 * @return true if a dialog is open
	 */
	public boolean isConfigurationDialogOpen();
	
	/**
	 * Used by the dialog to communicate the dialog was closed.
	 */
	public void configurationDialogClosed();
	
}
