/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.AlarmTree;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.platform.security.SecurityFacade;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.csstudio.platform.ui.security.AbstractUserDependentAction;

/** Action that un-acknowledges alarms
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class UnAcknowledgeAction extends AbstractUserDependentAction
{
	private ISelectionProvider selection_provider;
    private List<AlarmTree> alarms;
    private static boolean allow_anonymous_acknowledge = Preferences.getAllowAnonyACK();

    /** Initialize action
     *  @param selection_provider Selection provider that must give AlarmTree items
     */
    public UnAcknowledgeAction(final ISelectionProvider selection_provider)
    {
        super(Messages.UnacknowledgeAction,
                Activator.getImageDescriptor("icons/unacknowledge.gif"), AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge); //$NON-NLS-1$
        this.selection_provider = selection_provider;
        selection_provider.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
            	boolean isEmpty = event.getSelection().isEmpty();
            	if(!isEmpty){
            		setEnabledWithoutAuthorization(true);
            	     //authorization
                    setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
            	}else {
            		setEnabled(false);
            		setEnabledWithoutAuthorization(false);
            	}
            }
        });
        setEnabled(false);
        setEnabledWithoutAuthorization(false);
    }

    
    /** Initialize action
     *  @param alarms Alarms to acknowledge when action runs
     */
    public UnAcknowledgeAction(final ArrayList<AlarmTree> alarms)
    {
        
    	super(Messages.UnacknowledgeAction,
                Activator.getImageDescriptor("icons/unacknowledge.gif"), AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge); //$NON-NLS-1$
        this.alarms = alarms;
        //authorization
        setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
        setEnabledWithoutAuthorization(true);        
    }

	@Override
	protected void doWork() {
		if (alarms != null)
        {
            for (AlarmTree item : alarms)
                item.acknowledge(false);
        }
        else
        {
            final Object items[] =
             ((IStructuredSelection)selection_provider.getSelection()).toArray();
            for (Object item : items)
                if (item instanceof AlarmTree)
                    ((AlarmTree)item).acknowledge(false);
        }		
	}
}
