package org.csstudio.opibuilder.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Perspective for using the OPI Editor
 *  @author Kay Kasemir, Xihui Chen
 */
public class OPIEditorPerspective implements IPerspectiveFactory
{
    private static final String ID_LEFT_BOTTOM = "bottomRight";  //$NON-NLS-1$
	private static final String ID_BOTTOM_LEFT = "bottomLeft";//$NON-NLS-1$
	private static final String ID_RIGHT = "right";//$NON-NLS-1$
	private static final String ID_LEFT = "left";//$NON-NLS-1$
	private static final String ID_CONSOLE_VIEW =
		"org.eclipse.ui.console.ConsoleView";//$NON-NLS-1$
	/** ID of navigator view.
     *  This one is deprecated, but don't know what else to use.
     */
    final String ID_NAVIGATOR = IPageLayout.ID_RES_NAV;

    /** Create suggested OPI Editor layout:
     *  <pre>Navigator | Editor | Properties</pre>
     */
    @SuppressWarnings("nls")
    public void createInitialLayout(IPageLayout layout)
    {
        final String editor = layout.getEditorArea();
        
        final IFolderLayout left = layout.createFolder(ID_LEFT,
                IPageLayout.LEFT, 0.2f, editor);
        final IFolderLayout right = layout.createFolder(ID_RIGHT,
                IPageLayout.RIGHT, 0.75f, editor);
        final IFolderLayout bottom = layout.createFolder(ID_BOTTOM_LEFT,
                IPageLayout.BOTTOM, 0.75f, editor);
        final IFolderLayout leftBottom = layout.createFolder(ID_LEFT_BOTTOM,
        		IPageLayout.BOTTOM, 0.7f, ID_LEFT);

        // Stuff for 'left'
        left.addView(ID_NAVIGATOR);
        left.addPlaceholder(IPageLayout.ID_RES_NAV);
        leftBottom.addView(IPageLayout.ID_OUTLINE);
        
        // Stuff for 'right'
        right.addView(IPageLayout.ID_PROP_SHEET);
             
        
        //Stuff for 'bottom'
        bottom.addView(ID_CONSOLE_VIEW);               
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(ID_NAVIGATOR);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(ID_CONSOLE_VIEW);
        layout.addNewWizardShortcut("org.csstudio.opibuilder.wizards.newOPIWizard"); //$NON-NLS-1$
        layout.addNewWizardShortcut("org.csstudio.opibuilder.wizards.newJSWizard"); //$NON-NLS-1$
    }
}
