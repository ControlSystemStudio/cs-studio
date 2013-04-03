/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.AuthIDs;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

/** Action that acknowledges or un-acknowledges alarms
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class AcknowledgeAction extends Action
{
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
            		      : Activator.getImageDescriptor("icons/unacknowledge.gif")); //$NON-NLS-1$
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
                	setEnabled(SecuritySupport.havePermission(AuthIDs.ACKNOWLEDGE));
                else
                	setEnabled(false);
            }
        });        
        SecuritySupportUI.registerAction(this, AuthIDs.ACKNOWLEDGE);
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
  		                  : Activator.getImageDescriptor("icons/unacknowledge.gif")); //$NON-NLS-1$
    	this.acknowledge = acknowledge;
        this.alarms = alarms;

        //authorization
        SecuritySupportUI.registerAction(this, AuthIDs.ACKNOWLEDGE);
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
	public void run()
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
