/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan;

import org.csstudio.trends.sscan.exportview.ExportView;
import org.csstudio.trends.sscan.scancontrol.SscanView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/** Create a perspective that's convenient for Sscan use.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Perspective implements IPerspectiveFactory
{
    /** Perspective ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.sscan.Perspective";


    /** Try to switch to the Sscan perspective
     *  @throws WorkbenchException on error
     */
    public static void showPerspective() throws WorkbenchException
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        workbench.showPerspective(Perspective.ID, window);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("deprecation")
    public void createInitialLayout(final IPageLayout layout)
    {
        //  editor  | right
        //          |
        //          |
        // +--------+
        //  bottom  |
        String editor = layout.getEditorArea();
        IFolderLayout right = layout.createFolder("right",
                        IPageLayout.RIGHT, 0.66f, editor);
        IFolderLayout bottom = layout.createFolder("bottom",
                        IPageLayout.BOTTOM, 0.66f, editor);

        // Stuff for 'right'
        right.addView(SscanView.ID);
        right.addView(IPageLayout.ID_RES_NAV);

        // Stuff for 'bottom'
        bottom.addView(IPageLayout.ID_PROP_SHEET);
        bottom.addView(ExportView.ID);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);

        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    }
}
