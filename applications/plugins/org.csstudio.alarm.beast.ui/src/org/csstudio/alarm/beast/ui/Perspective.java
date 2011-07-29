/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

/** Alarm Perspective
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Perspective implements IPerspectiveFactory
{
    /** ID of this perspective, registered in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.ui.perspective";

    // Ideally, these constants would come from e.g.
    // org.csstudio.alarm.beast.ui.alarmtree.AlarmTreeView.ID,
    // but that would add a (circular) dependency to that plugin.
    // This way, one could replace the table or tree view plugins.
    final private static String ID_SNS_PV_UTIL =
        "org.csstudio.diag.pvfields.view.PVFieldsView";
    final private static String ID_ALARM_PANEL =
        "org.csstudio.alarm.beast.ui.areapanel";
    final private static String ID_ALARM_TREE =
        "org.csstudio.alarm.beast.ui.alarmtree.View";
    final private static String ID_ALARM_TABLE =
        "org.csstudio.alarm.beast.ui.alarmtable.view";

    @Override
    @SuppressWarnings("deprecation")
    public void createInitialLayout(final IPageLayout layout)
    {
        // left | editor
        //      |
        //      |
        //      +-------------
        //      | bottom
    	final String editor = layout.getEditorArea();
        final IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);
        final IFolderLayout bottom = layout.createFolder("bottom",
                        IPageLayout.BOTTOM, 0.66f, editor);
        
        
        // Stuff for 'left'
        if (isViewAvaialble(ID_ALARM_PANEL))
        {
        	final IFolderLayout topleft = layout.createFolder("topleft", IPageLayout.TOP, 0.4f, "left");
        	topleft.addView(ID_ALARM_PANEL);
        }
        left.addView(ID_ALARM_TREE);
        left.addPlaceholder(IPageLayout.ID_RES_NAV);
        left.addPlaceholder(IPageLayout.ID_PROP_SHEET);
        left.addPlaceholder(ID_SNS_PV_UTIL);

        // Stuff for 'bottom'
        bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
        bottom.addView(ID_ALARM_TABLE);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
    }

    /** Check if view is available, i.e. suitable plugin was included in product */
	private boolean isViewAvaialble(final String view)
    {
		return PlatformUI.getWorkbench().getViewRegistry().find(view) != null;
    }
}
