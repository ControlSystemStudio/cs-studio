/*
 * 
 */
package org.csstudio.alarm.treeView.views;

import org.eclipse.osgi.util.NLS;

/**
 * TODO (valett) :
 * 
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 21.06.2010
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.alarm.treeView.views.messages"; //$NON-NLS-1$
    
    public static String AlarmTreeView_Menu_Separator_Edit;
    public static String AlarmTreeView_MessageDialog_ContextMenu_Error_Message;
    public static String AlarmTreeView_MessageDialog_ContextMenu_Error_Title;
    public static String AlarmTreeView_Monitor_ConnectionJob_Start;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    
    private Messages() {
    }
}
