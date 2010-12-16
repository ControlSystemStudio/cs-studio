/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.Perspective;
import org.csstudio.apputil.ui.workbench.OpenPerspectiveAction;

/** Action that opens the alarm perspective
 *  @author Kay Kasemir
 */
public class AlarmPerspectiveAction extends OpenPerspectiveAction
{
    /** Initialize */
    public AlarmPerspectiveAction()
    {
        super(Activator.getImageDescriptor("icons/alarm.gif"), //$NON-NLS-1$
              Messages.AlarmPerspectiveAction, Perspective.ID);
    }
}
