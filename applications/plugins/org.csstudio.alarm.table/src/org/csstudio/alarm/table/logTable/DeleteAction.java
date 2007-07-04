package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DeleteAction extends Action
{
    private JMSLogTableViewer table;

	public DeleteAction(final JMSLogTableViewer table)
	{
        this.table = table;
		setText("Delete");
		setToolTipText("Delete selected messages");
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
        JMSMessage entries[] = table.getSelectedEntries();
		if (entries == null)
			return;
		for (int i = 0; i < entries.length; i++)
			table.getTableModel().removeJMSMessage(entries[i]);
		table.refresh();
	}
}
