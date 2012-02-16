/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.csstudio.scan.ui.scantree.operations.SubmitCurrentScanAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.part.EditorActionBarContributor;

/** Add actions to toolbar while ScanEditor is active
 *  @author Kay Kasemir
 */
public class ScanEditorContributor extends EditorActionBarContributor
{
    @Override
    public void contributeToToolBar(final IToolBarManager manager)
    {
        manager.add(new SubmitCurrentScanAction());
    }

    @Override
    public void contributeToCoolBar(final ICoolBarManager manager)
    {
        manager.add(new SubmitCurrentScanAction());
    }

    // With global Actions, this required code like
    //    @Override
    //    public void setActiveEditor(final IEditorPart editor)
    //    {
    //        getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), my_copy_actionl);
    //    }
    // Now using menu command (from o.c.ui.menu.app) and handlers (from this plugin)
}
