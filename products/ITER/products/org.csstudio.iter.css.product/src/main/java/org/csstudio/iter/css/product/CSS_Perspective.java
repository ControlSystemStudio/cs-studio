package org.csstudio.iter.css.product;

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
//    final private static String ID_PROBE = "org.csstudio.diag.probe.Probe";
    final private static String ID_CLOCK = "org.csstudio.utility.clock.ClockView";
    final private static String ID_DATABROWSER_PERSP = "org.csstudio.trends.databrowser.Perspective";
    final private static String ID_ALARM_PERSP = "org.csstudio.alarm.beast.ui.perspective";
//    final private static String ID_DATABROWSER_CONFIG = "org.csstudio.trends.databrowser.configview.ConfigView";
//    final private static String ID_SNS_PV_UTIL = "org.csstudio.diag.pvutil.view.PVUtilView";
    final private static String ID_ALARM_TREE = "org.csstudio.alarm.beast.ui.alarmtree.View";
    final private static String ID_ALARM_TABLE= "org.csstudio.alarm.beast.ui.alarmtable.view";
    
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
        left.addView("org.eclipse.ui.views.ResourceNavigator");
//        left.addView(IPageLayout.ID_RES_NAV); // Deprecated, ID_PROJECT_EXPLORER?
//        left.addPlaceholder(ID_SNS_PV_UTIL);
        left.addPlaceholder(ID_ALARM_TREE);
        
        // Stuff for 'bottom'
//        bottom.addPlaceholder(ID_PROBE);
//        bottom.addPlaceholder(ID_PROBE + ":*");
//        bottom.addPlaceholder(ID_DATABROWSER_CONFIG);
        bottom.addPlaceholder(ID_ALARM_TABLE);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Perspectives..." menu with suggested persp.
        layout.addPerspectiveShortcut(ID);
        layout.addPerspectiveShortcut(ID_DATABROWSER_PERSP);
        layout.addPerspectiveShortcut(ID_ALARM_PERSP);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(ID_CLOCK);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
	}
}
