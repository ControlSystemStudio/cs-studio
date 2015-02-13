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
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.security.ui.SecuritySupportUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Action that disable a Component or PV and all of its children from the configuration.
 *  @author Xinyu Wu
 */
public class DisableComponentAction extends Action
{
    final private Shell shell;
    final private AlarmClientModel model;
    final private AlarmTreeItem items[];

    /** Initialize action
     *  @param shell Shell
     *  @param model Alarm model
     *  @param items Items to remove
     */
    public DisableComponentAction(final Shell shell, final AlarmClientModel model,
            final List<AlarmTreeItem> items)
    {
        super(getEnablementText(items.get(0)), getEnablementIcon(items.get(0))); //$NON-NLS-1$
        this.shell = shell;
        this.model = model;
        this.items = new AlarmTreeItem[items.size()];
        items.toArray(this.items);

        //authorization
        SecuritySupportUI.registerAction(this, AuthIDs.CONFIGURE);
    }

    
    private static String getEnablementText(AlarmTreeItem item) {
    	return item.isEnabled()?"Disable component":"Enable component";
    }
    
    private static ImageDescriptor getEnablementIcon(AlarmTreeItem item) {
    	String iconName = item.isEnabled()?"icons/disable_alarm.png":"icons/enable_alarm.png";
    	
    	ImageDescriptor icon = Activator.getImageDescriptor(iconName);
    	
    	return ImageDescriptor.createFromImageData(icon.getImageData().scaledTo(20, 20));
    }
    
    /** Prompt for PV name, add it to model
     *  @see org.eclipse.jface.action.Action#run()
     */
	@Override
	public void run()
	{
		final StringBuilder names = new StringBuilder();
        for (AlarmTreeItem item : items)
        {
            if (names.length() > 0)
                names.append(", "); //$NON-NLS-1$
            names.append(item.getName());
        }
        if (!MessageDialog.openConfirm(shell, getText(),
                "The component and all its children will be " + (items[0].isEnabled()?"disabled: ":"enabled: ")
                        + names.toString()))
            return;
        for (AlarmTreeItem item : items)
        {
            try
            {
                model.setEnabled(item, !item.isEnabled());
            }
            catch (Exception ex)
            {
                MessageDialog.openError(shell, Messages.Error,
                     NLS.bind(getText() + " failed",
                              item.getName(), ex.getMessage()));
            }
        }
	}
}
