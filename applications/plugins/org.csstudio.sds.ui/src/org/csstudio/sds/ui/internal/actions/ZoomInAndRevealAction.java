package org.csstudio.sds.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.ui.IWorkbenchPart;

public class ZoomInAndRevealAction extends SelectionAction {
	
	private final ZoomInAction _zoomInAction;
	private final GraphicalViewer _viewer;

	public ZoomInAndRevealAction(ZoomManager zoomManager, IWorkbenchPart part, GraphicalViewer viewer) {
		super(part);
		_viewer = viewer;
		_zoomInAction = new ZoomInAction(zoomManager);
	}
	
	public String getId() {
		return _zoomInAction.getId();
	}

	@Override
	public void run() {
		_zoomInAction.run();
		revealSelectedEditParts();
	}
	
	private void revealSelectedEditParts() {
		List<EditPart> selectedEditParts = getSelectedEditParts();
		if (!selectedEditParts.isEmpty()) {
			_viewer.reveal(selectedEditParts.get(selectedEditParts.size() - 1));
		}
	}

	private List<EditPart> getSelectedEditParts() {
		List<?> selection = getSelectedObjects();
		
		List<EditPart> selectedEditParts = new ArrayList<EditPart>(selection.size());
		for (Object o : selection) {
			if (o instanceof EditPart) {
				selectedEditParts.add(((EditPart) o));
			}
		}
		return selectedEditParts;
	}

	@Override
	protected boolean calculateEnabled() {
		return _zoomInAction.isEnabled();
	}
	
	
}
