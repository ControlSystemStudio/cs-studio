package org.csstudio.platform.ui.workbench;

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
 * @author awill
 * 
 */
public class CssWorkbenchAdvisor extends WorkbenchAdvisor {
	/**
	 * The initial window width.
	 */
	public static final int MAX_WINDOW_WIDTH = 800;

	/**
	 * The initial window height.
	 */
	public static final int MAX_WINDOW_HEIGHT = 600;

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
		_workbenchWindowconfigurer.setTitle(Messages.getString("CssWorkbenchAdvisor.WINDOW_TITLE")); //$NON-NLS-1$
		_workbenchWindowconfigurer.setShowPerspectiveBar(true);
		_workbenchWindowconfigurer.setShowMenuBar(true);
		_workbenchWindowconfigurer.setShowCoolBar(false);
		_workbenchWindowconfigurer.setShowFastViewBars(false);
		_workbenchWindowconfigurer.setShowProgressIndicator(false);
		_workbenchWindowconfigurer.setShowStatusLine(true);
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
