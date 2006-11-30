package org.csstudio.display.pvtable.ui;


import org.csstudio.display.pvtable.model.PVListEntry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Cut an entry
 * 
 *  @author Kay Kasemir
 */
public class CutAction extends CopyAction
{
	CutAction(PVTableViewerHelper helper)
	{
		super(helper);
		setText("Cut");
		setToolTipText("Cut PV to Clipboard");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
        setEnabled(false);
		// Conditionally enable this action
		helper.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						boolean anything = !event.getSelection().isEmpty();
						CutAction.this.setEnabled(anything);
					}
				});
	}

	@Override
	public void run()
	{
		// First, copy the items to the clipboard
		super.run();
		// Then, remove them as in the RemoveAction
        PVListEntry entries[] = helper.getSelectedEntries();
		if (entries == null)
			return;
		for (int i = 0; i < entries.length; i++)
			helper.getPVListModel().removeEntry(entries[i]);
	}
}
