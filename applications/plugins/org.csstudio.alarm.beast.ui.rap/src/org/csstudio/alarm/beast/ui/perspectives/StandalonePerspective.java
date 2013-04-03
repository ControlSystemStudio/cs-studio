/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

/** Alarm Perspective
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StandalonePerspective implements IPerspectiveFactory
{
    /** ID of this perspective, registered in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.ui.standalone";

    final private static String ID_ALARM_PANEL =
        "org.csstudio.alarm.beast.ui.areapanel";
    final private static String ID_ALARM_TREE =
        "org.csstudio.alarm.beast.ui.alarmtree.View";
    final private static String ID_ALARM_TABLE =
            "org.csstudio.alarm.beast.ui.alarmtable.view";
    final private static String ID_MSG_HIST =
            "org.csstudio.alarm.beast.msghist.MessageHistoryView";

    @Override
    public void createInitialLayout(final IPageLayout layout)
    {
        // left | top
        //      |
        //      |
        //      +-------------
        //      | bottom
    	
    	final String editor = layout.getEditorArea();
        layout.setFixed(true);
    	layout.setEditorAreaVisible(false);
    	
        final IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);
        final IFolderLayout top = layout.createFolder("top",
                IPageLayout.TOP, 0.66f, editor);
        final IFolderLayout bottom = layout.createFolder("bottom",
                IPageLayout.BOTTOM, 0.66f, editor);
        
        
        // Stuff for 'left'
        if (isViewAvailable(ID_ALARM_PANEL))
        {
        	final IFolderLayout topleft = layout.createFolder("topleft", IPageLayout.TOP, 0.4f, "left");
        	topleft.addView(ID_ALARM_PANEL);
        }
        left.addView(ID_ALARM_TREE);
        left.addPlaceholder(IPageLayout.ID_PROP_SHEET);

        // Stuff for 'top'
        top.addView(ID_ALARM_TABLE);
        
        // Stuff for 'bottom'
        bottom.addView(ID_MSG_HIST);
    }

    /** Check if view is available, i.e. suitable plugin was included in product */
	private boolean isViewAvailable(final String view)
    {
		return PlatformUI.getWorkbench().getViewRegistry().find(view) != null;
    }
}
