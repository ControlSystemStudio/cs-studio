/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/** Action that acknowledges alarms
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class AcknowledgeAction extends AbstractUserDependentAction {
	private ISelectionProvider selection_provider;
    private List<AlarmTreeItem> alarms;
    private static boolean allow_anonymous_acknowledge = Preferences.getAllowAnonyACK();

    /** Initialize action
     *  @param selection_provider Selection provider that must give AlarmTree items
     */
    public AcknowledgeAction(final ISelectionProvider selection_provider)
    {

    	super(Messages.Acknowledge_Action,
                Activator.getImageDescriptor("icons/acknowledge.gif"), AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge); //$NON-NLS-1$
        this.selection_provider = selection_provider;
        selection_provider.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
            	boolean isEmpty= event.getSelection().isEmpty();

                if(!isEmpty) {
                	setEnabledWithoutAuthorization(true);
                	//authorization
                	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
                }else {
                	setEnabledWithoutAuthorization(false);
                	setEnabled(false);
                }
            }
        });
        setEnabled(false);
        setEnabledWithoutAuthorization(false);
    }

    /** Initialize action
     *  @param alarms Alarms to acknowledge when action runs
     */
    public AcknowledgeAction(final List<AlarmTreeItem> alarms)
    {
        super(Messages.Acknowledge_Action,
                Activator.getImageDescriptor("icons/acknowledge.gif"), AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge); //$NON-NLS-1$
        this.alarms = alarms;

        //authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
    	setEnabledWithoutAuthorization(true);
    }


    /** {@inheritDoc} */
    @Override
    protected void doWork()
    {
        if (alarms != null)
        {
            for (AlarmTreeItem item : alarms)
                item.acknowledge(true);
        }
        else
        {
            final Object items[] =
             ((IStructuredSelection)selection_provider.getSelection()).toArray();
            for (Object item : items)
                if (item instanceof AlarmTreeItem)
                    ((AlarmTreeItem)item).acknowledge(true);
        }
    }

}
