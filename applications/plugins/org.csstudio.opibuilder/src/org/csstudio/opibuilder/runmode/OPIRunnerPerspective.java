package org.csstudio.opibuilder.runmode;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**The perspective for OPI running environment, which has no views.
 * @author Xihui Chen
 *
 */
public class OPIRunnerPerspective implements IPerspectiveFactory {

	private static final String ID_CONSOLE_VIEW =
		"org.eclipse.ui.console.ConsoleView";//$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {

		layout.addShowViewShortcut(ID_CONSOLE_VIEW);
		layout.addFastView(ID_CONSOLE_VIEW);
	}

}
