package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.archiveview.ArchiveView;
import org.csstudio.trends.databrowser.configview.ConfigView;
import org.csstudio.trends.databrowser.exportview.ExportView;
import org.csstudio.trends.databrowser.sampleview.SampleView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Create a perspective that's convenient for Data Browser use.
 *  @author Kay Kasemir
 */
public class Perspective implements IPerspectiveFactory
{
    public static final String ID = Perspective.class.getName();
    final private static String ID_SNS_PV_UTIL = 
        "org.csstudio.sns.pvutil.view.PVUtilView"; //$NON-NLS-1$
    
    @SuppressWarnings("nls")
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
        left.addView(ArchiveView.ID);
        left.addView(IPageLayout.ID_RES_NAV);
        left.addPlaceholder(ID_SNS_PV_UTIL);
        // Stuff for 'bottom'
        bottom.addView(ConfigView.ID);
        bottom.addView(ExportView.ID);
        bottom.addPlaceholder(SampleView.ID);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    }
}
