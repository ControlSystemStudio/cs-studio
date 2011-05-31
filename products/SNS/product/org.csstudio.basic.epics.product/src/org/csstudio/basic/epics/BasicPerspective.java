/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.basic.epics;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Initial perspective
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BasicPerspective implements IPerspectiveFactory
{
    /** Perspective ID registered in plugin.xml */
    final public static String ID = "org.csstudio.basic.epics.BasicPerspective";

    @Override
    public void createInitialLayout(final IPageLayout layout)
    {
        layout.addView("org.eclipse.ui.views.ResourceNavigator",
                IPageLayout.LEFT, 0.33f,
                IPageLayout.ID_EDITOR_AREA);

    }
}
