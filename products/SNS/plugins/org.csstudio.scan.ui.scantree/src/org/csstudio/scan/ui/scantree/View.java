/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the Scan Tree
 *  
 *  <p>Displays the scan tree and uses
 *  it as selection provider.
 *  {@link ScanCommandAdapterFactory} then adapts
 *  as necessary to support Properties view/editor.
 *  
 *  @author Kay Kasemir
 */
public class View extends ViewPart
{
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.scantree.view"; //$NON-NLS-1$

    private GUI gui;

    public View()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(parent);
        
        // TODO Show real scan, not dummy
        gui.setCommands(DemoScan.createCommands());
        
        getSite().setSelectionProvider(gui.getSelectionProvider());
    }
    
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
