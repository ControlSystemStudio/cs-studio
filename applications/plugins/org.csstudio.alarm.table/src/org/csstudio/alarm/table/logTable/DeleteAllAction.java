package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DeleteAllAction extends Action
{
    private JMSLogTableViewer table;

	public DeleteAllAction(final JMSLogTableViewer table)
	{
        this.table = table;
		setText("Delete All");
		setToolTipText("Delete all log messages");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
		// Conditionally enable this action
		table.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						boolean anything = !event.getSelection().isEmpty();
						setEnabled(anything);
					}
				});
	}

	@Override
	public void run() {
		JMSMessageList jmsl = table.getTableModel();
        jmsl.clearList(); 
		table.refresh();
	}
}
