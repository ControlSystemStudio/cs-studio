package org.csstudio.frib.product;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** A perspective for CSS at SNS
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSSPerspective implements IPerspectiveFactory
{
    /** The perspective ID */
    final public static String ID = CSSPerspective.class.getName();

    // Other view IDs
    // Copied them here instead of using their ...View.ID member so that
    // this plugin doesn't depend on other app plugins.
    final private static String ID_PROBE = "org.csstudio.diag.pvmanager.probe.MultipleView";
    final private static String ID_CLOCK = "org.csstudio.utility.clock.ClockView";
    final private static String ID_DATABROWSER_PERSP = "org.csstudio.trends.databrowser.Perspective";
    
    public void createInitialLayout(IPageLayout layout)
    {
        // left | editor
        //      |
        //      |
        //      +-------------
        //      | bottom
        String editor = layout.getEditorArea();
        IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);
        IFolderLayout bottom = layout.createFolder("bottom",
                        IPageLayout.BOTTOM, 0.66f, editor);
        
        // Stuff for 'left'
        left.addView(IPageLayout.ID_RES_NAV); // Deprecated, but what use instead?
        
        // Stuff for 'bottom'
        bottom.addPlaceholder(ID_PROBE);
        bottom.addPlaceholder(ID_PROBE + ":*");
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Perspectives..." menu with suggested persp.
        layout.addPerspectiveShortcut(ID);
        layout.addPerspectiveShortcut(ID_DATABROWSER_PERSP);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(ID_CLOCK);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
	}
}
