/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.display.pvtable.ui.editor.PVTableEditor;
import org.csstudio.util.wizard.NewFileWizard;

/** File/New wizard for PVTable.
 * 
 *  @author Kay Kasemir
 */
public class NewPVTableWizard extends NewFileWizard
{
    /** Constructor for NewPVTableWizard. */
    public NewPVTableWizard()
    {
        super(Plugin.getDefault(), 
                PVTableEditor.ID,
                Messages.PVTable,
                "pv_table." + Plugin.FileExtension, //$NON-NLS-1$
                Plugin.FileExtension,
                new PVListModel().getXMLContent());
    }
}
