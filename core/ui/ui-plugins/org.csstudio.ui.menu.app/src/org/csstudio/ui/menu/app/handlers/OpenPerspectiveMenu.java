/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.app.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ContributionItemFactory;

/** Dynamic menu handler to open perspective views
 *  @author Kay Kasemir
 */
public class OpenPerspectiveMenu extends ContributionItem
{
    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public void fill(final Menu menu, final int index)
    {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window).fill(menu, index);
    }
}
