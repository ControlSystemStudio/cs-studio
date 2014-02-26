/**
 * 
 */
package org.csstudio.logbook.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/**
 * @author shroffk
 * 
 */
public class LogViewerPerspective implements IPerspectiveFactory {

    public static final String ID = "org.csstudio.logbook.ui.LogViewerPerspective";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
     * .IPageLayout)
     */
    @Override
    public void createInitialLayout(IPageLayout layout) {

	final String editor = layout.getEditorArea();

	final IFolderLayout left = layout.createFolder("Left",
		IPageLayout.LEFT, 0.33f, editor);
	left.addView(org.csstudio.logbook.ui.LogTableView.ID);
	left.addView(org.csstudio.logbook.ui.LogTreeView.ID);
    }
}
