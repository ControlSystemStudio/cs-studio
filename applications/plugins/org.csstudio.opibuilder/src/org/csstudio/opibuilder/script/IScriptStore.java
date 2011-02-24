package org.csstudio.opibuilder.script;

/**
 * A store to save script related information and register or unregister script for PVs input.
 * @author Xiuhi Chen
 *
 */
public interface IScriptStore {

	/**
	 * Remove listeners from PV. Dispose related resource if needed.
	 */
	public void unRegister();
	
	
}
