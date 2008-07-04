package org.csstudio.nams.configurator.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class SelectionDragSourceListener implements DragSourceListener {
	
	private final Viewer viewer;
	public SelectionDragSourceListener(Viewer viewer) {
		this.viewer = viewer;
	}
	
	public void dragFinished(DragSourceEvent event) {
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}
	public void dragSetData(DragSourceEvent event) {
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
	}
	public void dragStart(DragSourceEvent event) {
		ISelection selection = viewer.getSelection();
		event.doit = !selection.isEmpty();
		LocalSelectionTransfer.getTransfer().setSelection(selection);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFF);
	}
}
