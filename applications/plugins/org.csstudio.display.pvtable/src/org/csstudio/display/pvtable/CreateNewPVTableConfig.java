/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import util.eclipse.menu.NewFileWizardMenuAction;

/** Action to run the NewPVTableWizard.
 *  <p>
 *  Hooked into navigator or workspace explorer context menu
 *  via object contrib to IContainer.
 *
 *  @author Kay Kasemir
 */
public class CreateNewPVTableConfig extends NewFileWizardMenuAction
{
    public CreateNewPVTableConfig()
    {
        super(NewPVTableWizard.class);
    }
}
