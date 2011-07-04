package org.csstudio.nams.configurator.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class SelectionDragSourceListener implements DragSourceListener {

	private final Viewer viewer;

	public SelectionDragSourceListener(final Viewer v) {
		this.viewer = v;
	}

	@Override
    public void dragFinished(final DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}

	@Override
    public void dragSetData(final DragSourceEvent event) {
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
	}

	@Override
    public void dragStart(final DragSourceEvent event) {
		final ISelection selection = this.viewer.getSelection();
		event.doit = !selection.isEmpty();
		LocalSelectionTransfer.getTransfer().setSelection(selection);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(
				event.time & 0xFFFF);
	}
}
