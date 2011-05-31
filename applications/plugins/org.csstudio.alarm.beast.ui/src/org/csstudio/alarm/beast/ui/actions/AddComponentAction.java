/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.swt.widgets.Shell;

/** Action that adds a Component or PV to the configuration.
 *  For AlarmTreeComponent items, it can add a PV.
 *  For other items, it can add a new AlarmTreeComponent or even
 *  a new AlarmTreeFacility
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class AddComponentAction extends AbstractUserDependentAction
{
    final private Shell shell;
    final private AlarmClientModel model;
    final private AlarmTreeItem parent;

    /** Initialize action
     *  @param shell Shell
     *  @param model Alarm model
     *  @param parent Parent component
     */
    public AddComponentAction(final Shell shell, final AlarmClientModel model,
            final AlarmTreeItem parent)
    {
        super(Messages.AddComponent, Activator.getImageDescriptor("icons/add.gif"), AuthIDs.CONFIGURE, false); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.parent = parent;
        setEnabledWithoutAuthorization(true);
    	//authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.CONFIGURE, false));
    }

    /** Prompt for PV name, add it to model.
     *  @see AbstractUserDependentAction
     */
	@Override
	protected void doWork()
	{
        new AddComponentDialog(shell, model, parent).open();
	}
}
