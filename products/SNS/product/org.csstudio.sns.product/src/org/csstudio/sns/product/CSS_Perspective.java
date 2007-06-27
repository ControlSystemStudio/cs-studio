package org.csstudio.sns.product;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** A perspective for CSS at SNS
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSS_Perspective implements IPerspectiveFactory
{
    /** The perspective ID */
    final public static String ID = CSS_Perspective.class.getName();

    // Other view IDs
    // Copied them here instead of using their ...View.ID member so that
    // this plugin doesn't depend on other app plugins.
    private static final String ID_PROBE = "org.csstudio.diag.probe.Probe";
    private static final String ID_NAVIGATOR = "org.csstudio.platform.ui.views.WorkspaceExplorerView";
    private static final String ID_CLOCK = "org.csstudio.utility.clock.ClockView";
    private static final String ID_DATABROWSER_PERSP = "org.csstudio.trends.databrowser.Perspective";
    private static final String ID_DATABROWSER_CONFIG = "org.csstudio.trends.databrowser.configview.ConfigView";
    private static final String ID_PROGRESS = "org.csstudio.platform.ui.views.progress";

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
        // Workspace explorer
        left.addView(ID_NAVIGATOR);
        
        // Stuff for 'bottom'
        bottom.addPlaceholder(ID_PROBE);
        bottom.addPlaceholder(ID_PROBE + ":*");
        bottom.addPlaceholder(ID_DATABROWSER_CONFIG);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        bottom.addPlaceholder(ID_PROGRESS);
        
        // Populate the "Window/Perspectives..." menu with suggested persp.
        layout.addPerspectiveShortcut(ID);
        layout.addPerspectiveShortcut(ID_DATABROWSER_PERSP);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(ID_NAVIGATOR);
        layout.addShowViewShortcut(ID_CLOCK);
        layout.addShowViewShortcut(ID_PROGRESS);
	}
}
