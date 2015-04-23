/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import org.csstudio.trends.databrowser2.exportview.ExportView;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.trends.databrowser2.sampleview.SampleView;
import org.csstudio.trends.databrowser2.search.SearchView;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/** Create a perspective that's convenient for Data Browser use.
 *  @author Kay Kasemir
 *  @author Naceur Benhadj (add property to hide "Property" view)
 */
@SuppressWarnings("nls")
public class Perspective implements IPerspectiveFactory
{
    /** Perspective ID (same ID as original Data Browser) registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.Perspective";


    /** Try to switch to the DataBrowser perspective
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
        // left | editor
        //      |
        //      |
        //      +-------------
        //      | bottom
		final boolean rcp = SingleSourcePlugin.getUIHelper().getUI() == UI.RCP;
		
        String editor = layout.getEditorArea();
		// Stuff for 'left'
        IFolderLayout left = null;
		if (rcp || !Preferences.hideSearchView()) {
			left = layout.createFolder("left", IPageLayout.LEFT,
					0.25f, editor);
		}
		
		if (left != null) {
			left.addView(SearchView.ID);
			if (rcp) {
				left.addView(IPageLayout.ID_RES_NAV);
			}
		}
		
        // Stuff for 'bottom'
		IFolderLayout bottom = null;
		if (rcp || !Preferences.hidePropertiesView()) {
			bottom = layout.createFolder("bottom",IPageLayout.BOTTOM, 0.66f, editor);
			bottom.addView(IPageLayout.ID_PROP_SHEET);
		}
        
		if (bottom != null) {
			if (rcp) bottom.addView(ExportView.ID);
			bottom.addPlaceholder(SampleView.ID);
			if (rcp) bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
		}
        // Populate the "Window/Views..." menu with suggested views
		if (rcp) {
			layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
	        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
	        layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
		}
    }
}
