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

import org.csstudio.platform.internal.usermanagement.IUserManagementListener;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.ui.internal.console.Console;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.internal.perspectives.CssDefaultPerspective;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * The workbench advisor for the control system studio. <br>
 * 
 * @see WorkbenchAdvisor
 * 
 * @author Alexander Will, Xihui Chen
 * @deprecated Use org.csstudio.startuphelper.module.DefaultWorkbenchAdvisor instead, 
 * which supports opening file from command line. 
 */
public class CssWorkbenchAdvisor extends WorkbenchAdvisor {
	/**
	 * The initial window width.
	 */
	public static final int MAX_WINDOW_WIDTH = 1024;

	/**
	 * The initial window height.
	 */
	public static final int MAX_WINDOW_HEIGHT = 800;

	/**
	 * The action builder.
	 */
	private WorkbenchActionBuilder _actionBuilder;

	/**
	 * The window configurer.
	 */
	private IWorkbenchConfigurer _workbenchConfigurer;

	/**
	 * The window configurer.
	 */
	private IWorkbenchWindowConfigurer _workbenchWindowconfigurer;
	
	/**
	 * Monitors user logins and updates the status bar accordingly.
	 */
	private IUserManagementListener _userListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getInitialWindowPerspectiveId() {
		return CssDefaultPerspective.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void postWindowCreate(
			final IWorkbenchWindowConfigurer configurer) {
		_workbenchWindowconfigurer = configurer;
		// initialize the css console.
		Console.getInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void preWindowOpen(final IWorkbenchWindowConfigurer configurer) {
		_workbenchWindowconfigurer = configurer;
		_workbenchWindowconfigurer.setInitialSize(new Point(MAX_WINDOW_WIDTH,
				MAX_WINDOW_HEIGHT));
		_workbenchWindowconfigurer.setTitle(Messages.CssWorkbenchAdvisor_WINDOW_TITLE);
		_workbenchWindowconfigurer.setShowPerspectiveBar(true);
		_workbenchWindowconfigurer.setShowMenuBar(true);
		_workbenchWindowconfigurer.setShowCoolBar(true);
		_workbenchWindowconfigurer.setShowFastViewBars(true);
		_workbenchWindowconfigurer.setShowProgressIndicator(true);
		_workbenchWindowconfigurer.setShowStatusLine(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postWindowOpen(final IWorkbenchWindowConfigurer configurer) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postWindowClose(final IWorkbenchWindowConfigurer configurer) {
		SecurityFacade.getInstance().removeUserManagementListener(_userListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void fillActionBars(final IWorkbenchWindow window,
			final IActionBarConfigurer configurer, final int flags) {

		if (_actionBuilder == null) {
			_actionBuilder = new WorkbenchActionBuilder(window);
		}

		_actionBuilder.makeAndPopulateActions(getWorkbenchConfigurer(),
				configurer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean preShutdown() {
		Console.getInstance().resetSystemOutputStream();
		return super.preShutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void postShutdown() {
		if (_actionBuilder != null) {
			_actionBuilder.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initialize(final IWorkbenchConfigurer configurer) {
		_workbenchConfigurer = configurer;
		_workbenchConfigurer.setSaveAndRestore(true);

		super.initialize(configurer);
	}
}
