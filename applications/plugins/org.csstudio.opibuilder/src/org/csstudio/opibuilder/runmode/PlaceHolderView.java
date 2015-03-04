/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;

/** RCP 'View' for debugging a Perspective
 *  @author Kay Kasemir
 */
public class PlaceHolderView extends ViewPart
{
    /** View ID registered in plugin.xml */
    public static final String ID = "org.csstudio.opibuilder.placeHolder"; //$NON-NLS-1$

    @Override
    public void createPartControl(final Composite parent)
    {
        final IViewSite site = getViewSite();
        parent.setLayout(new FillLayout());
        final Label text = new Label(parent, SWT.NONE);
        text.setText(site.getId() + ":" + site.getSecondaryId());
    }

    @Override
    public void setFocus()
    {
        // NOP
    }
}
