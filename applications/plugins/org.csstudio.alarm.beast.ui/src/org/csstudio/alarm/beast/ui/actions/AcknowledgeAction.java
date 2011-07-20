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
import org.eclipse.jface.viewers.Viewer;

/** Action that acknowledges or un-acknowledges alarms
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class AcknowledgeAction extends AbstractUserDependentAction
{
    final private static boolean allow_anonymous_acknowledge = Preferences.getAllowAnonyACK();
    final private boolean acknowledge;
	private Viewer viewer = null;
	private ISelectionProvider selection_provider;
    private List<AlarmTreeItem> alarms;
    
    /** Initialize action
     *  @param acknowledge Acknowledge, or un-acknowledge?
     *  @param selection_provider Selection provider that must give
     *                            {@link AlarmTreeItem} elements
     */
    public AcknowledgeAction(final boolean acknowledge,
    		final ISelectionProvider selection_provider)
    {
    	super(acknowledge ? Messages.Acknowledge_Action
    			          : Messages.UnacknowledgeAction,
              acknowledge ? Activator.getImageDescriptor("icons/acknowledge.gif") //$NON-NLS-1$
            		      : Activator.getImageDescriptor("icons/unacknowledge.gif"), //$NON-NLS-1$
              AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge);
    	this.acknowledge = acknowledge;
    	this.selection_provider = selection_provider;
        selection_provider.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
            	final boolean isEmpty = event.getSelection().isEmpty();
                //authorization
                if(!isEmpty)
                {
                	setEnabledWithoutAuthorization(true);
                	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
                }
                else
                {
                	setEnabledWithoutAuthorization(false);
                	setEnabled(false);
                }
            }
        });
        setEnabled(false);
        setEnabledWithoutAuthorization(false);
    }

    /** Initialize action
     *  @param acknowledge Acknowledge, or un-acknowledge?
     *  @param alarms Alarms to acknowledge when action runs
     */
    public AcknowledgeAction(final boolean acknowledge, final List<AlarmTreeItem> alarms)
    {
    	super(acknowledge ? Messages.Acknowledge_Action
		                  : Messages.UnacknowledgeAction,
              acknowledge ? Activator.getImageDescriptor("icons/acknowledge.gif") //$NON-NLS-1$
  		                  : Activator.getImageDescriptor("icons/unacknowledge.gif"), //$NON-NLS-1$
              AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge);
    	this.acknowledge = acknowledge;
        this.alarms = alarms;

        //authorization
    	setEnabled(SecurityFacade.getInstance().canExecute(AuthIDs.ACKNOWLEDGE, allow_anonymous_acknowledge));
    	setEnabledWithoutAuthorization(true);
    }

    /** @param viewer Current selection of this viewer
     *                will be cleared when the (un-)acknowledge is performed
     */
    public void clearSelectionOnAcknowledgement(final Viewer viewer)
    {
    	this.viewer = viewer;
    }

    /** {@inheritDoc} */
    @Override
    protected void doWork()
    {
    	if (alarms != null)
        {
        	if (viewer != null)
        		viewer.setSelection(null);
            for (AlarmTreeItem item : alarms)
                item.acknowledge(acknowledge);
        }
        else
        {
            final Object items[] =
             ((IStructuredSelection)selection_provider.getSelection()).toArray();
        	if (viewer != null)
        		viewer.setSelection(null);
            for (Object item : items)
                if (item instanceof AlarmTreeItem)
                    ((AlarmTreeItem)item).acknowledge(acknowledge);
        }
    }
}
