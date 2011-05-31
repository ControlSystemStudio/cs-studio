package org.csstudio.sds.ui.internal.actions;

import java.util.List;

import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.ui.IWorkbenchPart;

public class ZoomInAndRevealAction extends AbstractEditPartSelectionAction {
	
	private final ZoomInAction _zoomInAction;

	public ZoomInAndRevealAction(ZoomManager zoomManager, IWorkbenchPart part, GraphicalViewer viewer) {
		super(part, viewer);
		_zoomInAction = new ZoomInAction(zoomManager);
		setId(_zoomInAction.getId());
	}
	
	@Override
	protected boolean doCalculateEnabled(List<AbstractBaseEditPart> selectedEditParts) {
		return _zoomInAction.isEnabled();
	}

	@Override
	protected Command doCreateCommand(final List<AbstractBaseEditPart> selectedEditParts) {
		return new Command() {
			@Override
			public void execute() {
				_zoomInAction.run();
				revealSelectedEditParts(selectedEditParts);
			}
		};
	}

	private void revealSelectedEditParts(List<AbstractBaseEditPart> selectedEditParts) {
		if (!selectedEditParts.isEmpty()) {
			getGraphicalViewer().reveal(selectedEditParts.get(selectedEditParts.size() - 1));
		}
	}
}
