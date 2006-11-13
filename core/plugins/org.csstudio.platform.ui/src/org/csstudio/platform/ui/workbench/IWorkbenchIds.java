/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
