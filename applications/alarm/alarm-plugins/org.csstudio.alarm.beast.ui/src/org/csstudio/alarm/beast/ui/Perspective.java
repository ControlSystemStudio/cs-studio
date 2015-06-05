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

/** Alarm Perspective
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Perspective implements IPerspectiveFactory
{
    /** ID of this perspective, registered in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.ui.perspective";

    final private static String ID_CONSOLE_VIEW =
            "org.eclipse.ui.console.ConsoleView";

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
        left.addPlaceholder(IPageLayout.ID_RES_NAV);
        left.addPlaceholder(IPageLayout.ID_PROP_SHEET);

        // Stuff for 'bottom'
        bottom.addPlaceholder(ID_CONSOLE_VIEW);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(ID_CONSOLE_VIEW);
    }
}
