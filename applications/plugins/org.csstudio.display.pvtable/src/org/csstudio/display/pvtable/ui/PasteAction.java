package org.csstudio.display.pvtable.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Paste an entry
 * 
 *  @author Kay Kasemir
 */
public class PasteAction extends Action
{
	private PVTableViewerHelper helper;

	PasteAction(PVTableViewerHelper helper)
	{
		this.helper = helper;
		setText("Paste");
		setToolTipText("Paste new PV");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	@Override
	public void run()
	{
		TextTransfer transfer = TextTransfer.getInstance();
		String name = (String) helper.getClipboard().getContents(transfer);
		// TODO: Paste more than one PV name?
		if (name == null)
			System.out.println("Empty Clipboard");
		else
            helper.getPVListModel().addPV(name);
	}
}
