package org.csstudio.opibuilder.runmode;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/**The perspective for OPI running environment, which has no views.
 * @author Xihui Chen
 *
 */
public class OPIRunnerPerspective implements IPerspectiveFactory {

	private static final String ID_CONSOLE_VIEW =
		"org.eclipse.ui.console.ConsoleView";//$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {

		final String editor = layout.getEditorArea();       
        
        final IPlaceholderFolderLayout bottom = layout.createPlaceholderFolder("bottom",
                IPageLayout.BOTTOM, 0.75f, editor);
		
        bottom.addPlaceholder(ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(ID_CONSOLE_VIEW);
	}

}
