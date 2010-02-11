package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.exportview.ExportView;
import org.csstudio.trends.databrowser.sampleview.SampleView;
import org.csstudio.trends.databrowser.search.SearchView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Create a perspective that's convenient for Data Browser use.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Perspective implements IPerspectiveFactory
{
    /** Perspective ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.Perspective";
    
    final private static String ID_SNS_PV_UTIL = "org.csstudio.diag.pvutil.view.PVUtilView";

    /** {@inheritDoc} */
    @SuppressWarnings("deprecation")
    public void createInitialLayout(final IPageLayout layout)
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
        left.addView(SearchView.ID);
        left.addView(IPageLayout.ID_RES_NAV);
        left.addPlaceholder(ID_SNS_PV_UTIL);
        
        // Stuff for 'bottom'
        bottom.addView(IPageLayout.ID_PROP_SHEET);
        bottom.addView(ExportView.ID);
        bottom.addPlaceholder(SampleView.ID);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    }
}
