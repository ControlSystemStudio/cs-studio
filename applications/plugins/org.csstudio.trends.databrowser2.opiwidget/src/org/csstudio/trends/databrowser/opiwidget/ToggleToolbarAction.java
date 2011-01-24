/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import org.eclipse.ui.IWorkbenchPage;

/** Action for context menu object contribution that
 *  toggles tool bar visibility
 *  @author Kay Kasemir
 */
public class ToggleToolbarAction extends DataBrowserWidgetAction
{
    @Override
    protected void doRun(final IWorkbenchPage page, final DataBrowserWidgedEditPart edit_part)
    {
        final DataBrowserWidgetFigure figure = edit_part.getWidgetFigure();
        figure.setToolbarVisible(! figure.isToolbarVisible());
    }
}
