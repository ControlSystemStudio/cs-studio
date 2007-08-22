package org.csstudio.platform.ui.dnd.rfc;


/**
 * Strategy which can be used to decide whether the control system dialog (see
 * {@link ChooseControlSystemPrefixDialog}) should pop up during DnD or not.
 * 
 * @author Sven Wende
 * 
 */
public interface IShowControlSystemDialogStrategy {
	/**
	 * Return true, if the control system dialog should pop up.
	 * 
	 * @param rawName
	 *            the raw name, which is about to be dropped
	 * 
	 * @return true, if the dialog should pop up, false otherwise
	 */
	boolean showControlSystem(String rawName);
}
