package org.csstudio.platform.ui.workbench;

/**
 * CSS specific action ids for standard actions, groups in the workbench menu
 * bar, and global actions.
 * <p>
 * Note: Eclipse RCP already defines a bunch of action ids in
 * {@link org.eclipse.ui.IWorkbenchActionConstants} that should also be applied!
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 * <h3>Standard menus</h3>
 * <ul>
 * <li>CSS menu (<code>M_CSS</code>)</li>
 * <li>?? menu (<code>M_??</code>)</li>
 * <li>?? menu (<code>M_??</code>)</li>
 * </ul>
 * <p>
 * To hook a global action handler, a view should use the following code: <code>
 *   IAction copyHandler = ...;
 *   view.getSite().getActionBars().setGlobalActionHandler(
 *       IWorkbenchIds.COPY, 
 *       copyHandler);
 * </code>
 * For editors, this should be done in the
 * <code>IEditorActionBarContributor</code>.
 * </p>
 * 
 * @see org.eclipse.ui.IActionBars#setGlobalActionHandler
 */

public interface IWorkbenchIds {
	/**
	 * ID for the <i>CSS</i> menu.
	 */
	String MENU_CSS = "css"; //$NON-NLS-1$

	/**
	 * ID for the <i>Display</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_DISPLAY = "display"; //$NON-NLS-1$

	/**
	 * ID for the <i>Alarm</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_ALARM = "alarm"; //$NON-NLS-1$

	/**
	 * ID for the <i>Trends</i> section of the <i>CSS</i> menu.
	 */	
	String MENU_CSS_TRENDS = "trends"; //$NON-NLS-1$

	/**
	 * ID for the <i>Diagnostic Tools</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_DIAGNOSTICS = "diagnostics"; //$NON-NLS-1$

	/**
	 * ID for the <i>Debugging</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_DEBUGGING = "debugging"; //$NON-NLS-1$

	/**
	 * ID for the <i>Configuration</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_CONFIGURATION = "configuration"; //$NON-NLS-1$

	/**
	 * ID for the <i>Management</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_MANAGEMENT = "management"; //$NON-NLS-1$

	/**
	 * ID for the <i>Editors</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_EDITORS = "editors"; //$NON-NLS-1$

	/**
	 * ID for the <i>Utilities</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_UTILITIES = "utilities"; //$NON-NLS-1$

	/**
	 * ID for the <i>Test</i> section of the <i>CSS</i> menu.
	 */
	String MENU_CSS_TEST = "test"; //$NON-NLS-1$

	/**
	 * ID for the <i>Other</i> section of the <i>CSS</i> menu.
	 */	
	String MENU_CSS_OTHER = "other"; //$NON-NLS-1$
	
	/**
	 * Name of standard File menu (value <code>"file"</code>).
	 */
	String GROUP_CSS_MB3 = "css_mb3"; //$NON-NLS-1$
}
