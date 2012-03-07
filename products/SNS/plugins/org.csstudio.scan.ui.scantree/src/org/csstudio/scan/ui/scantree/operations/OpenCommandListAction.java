/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.operations;

import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.scan.ui.scantree.Activator;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.gui.CommandListView;

/** Action that opens the command list view
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class OpenCommandListAction extends OpenViewAction
{
    public OpenCommandListAction()
    {
        super(CommandListView.ID,
              Messages.OpenCommandList,
              Activator.getImageDescriptor("icons/scantree.gif"));
    }
}
