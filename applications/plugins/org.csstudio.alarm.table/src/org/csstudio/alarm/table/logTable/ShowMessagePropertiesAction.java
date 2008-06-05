/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.LogView;
import org.csstudio.alarm.table.PropertiesView;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ShowMessagePropertiesAction extends Action
{
    private JMSLogTableViewer table;

	public ShowMessagePropertiesAction(final JMSLogTableViewer table)
	{
        this.table = table;
		setText("Show Properties");
		setToolTipText("Show all properties of selected message");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
		// Conditionally enable this action
		table.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						if(!event.getSelection().isEmpty()) {
							boolean anything = true; 
							JMSMessage entries[] = table.getSelectedEntries();
							if ((entries != null) && (entries.length == 1)) {
								anything = true;
							} else {
								anything = false;
							}
							setEnabled(anything);
						}
					}
				});
	}

	@Override
	public void run() {
        JMSMessage entries[] = table.getSelectedEntries();
		if (entries == null)
			return;

	    try
	    {
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        page.showView(PropertiesView.ID);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }

		
		System.out.println("XXXXXXXXXXXXXXXX in show run");
			
	}
}
