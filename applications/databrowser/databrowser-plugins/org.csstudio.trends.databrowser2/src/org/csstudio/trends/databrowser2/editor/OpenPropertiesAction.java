/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/** Action for opening property sheet.
 *
 *  <p>When simply opening the property sheet, it tends to gain focus.
 *  Since the data browser editor for which the user just asked to open the prop. view
 *  is no longer active, the property sheet is empty.
 *
 *  <p>This adjustment to the basic OpenViewAction keeps/re-activates the editor.
 *
 *  @author Kay Kasemir
 */
public class OpenPropertiesAction extends OpenViewAction
{
    public OpenPropertiesAction()
    {
        super(IPageLayout.ID_PROP_SHEET,
              Messages.OpenPropertiesView,
              Activator.getDefault().getImageDescriptor("icons/prop_ps.gif"));
    }

    @Override
    public void run()
    {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final IEditorPart editor = page.getActiveEditor();
        doShowView();
        if (editor != null)
            page.activate(editor);
    }
}
