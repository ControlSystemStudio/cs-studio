/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.actions;

import org.csstudio.scan.ui.scantree.Activator;
import org.eclipse.jface.action.Action;

/** (Menu) action to add command to tree
 *  @author Kay Kasemir
 */
public class AddCommandAction extends Action
{
    public AddCommandAction()
    {
        super("Add Command", Activator.getImageDescriptor("icons/add.gif"));
    }

    @Override
    public void run()
    {
        // TODO Add command
        super.run();
    }
}
