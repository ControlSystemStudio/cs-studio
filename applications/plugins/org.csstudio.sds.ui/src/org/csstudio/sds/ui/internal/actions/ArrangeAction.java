package org.csstudio.sds.ui.internal.actions;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.commands.ArrangeCommand;
import org.csstudio.sds.ui.internal.editor.Arrange;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.ui.IWorkbenchPart;

public class ArrangeAction extends AbstractEditPartSelectionAction implements UpdateAction {

	public static final String HORIZONTAL = "org.csstudio.sds.ui.internal.editor.ArrangeAction.Horizontal";
	public static final String VERTICAL = "org.csstudio.sds.ui.internal.editor.ArrangeAction.Vertical";

	private final CommandStack _commandStack;
	private final Arrange _direction;

	public ArrangeAction(IWorkbenchPart part, GraphicalViewer viewer, CommandStack commandStack, Arrange direction) {
		super(part, viewer);
		this._commandStack = commandStack;
		this._direction = direction;
		if (direction == Arrange.HORIZONTAL) {
			setId(HORIZONTAL);
		} else {
			setId(VERTICAL);
		}
	}

	private boolean areSiblings(List<AbstractBaseEditPart> selectedEditParts) {
		if (selectedEditParts.size() < 2) {
			return false;
		}

		EditPart parent = selectedEditParts.get(0).getParent();
		if (parent == null) {
			return false;
		}

		for (AbstractBaseEditPart editPart : selectedEditParts.subList(1, selectedEditParts.size())) {
			if (!parent.equals(editPart.getParent())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void update() {
		super.update();
		setEnabled(calculateEnabled());
	}

	@Override
	protected boolean doCalculateEnabled(List<AbstractBaseEditPart> selectedEditParts) {
		return selectedEditParts.size() > 2 && areSiblings(selectedEditParts);
	}

	@Override
	protected Command doCreateCommand(final List<AbstractBaseEditPart> selectedEditParts) {
		return new Command() {
			public void execute() {
				List<AbstractBaseEditPart> normalizedSelection = normalizeGroupingSelections(selectedEditParts);
				_commandStack.execute(new ArrangeCommand(normalizedSelection, _direction));
			};
		};
	}

	private List<AbstractBaseEditPart> normalizeGroupingSelections(List<AbstractBaseEditPart> selectedEditParts) {
		List<AbstractBaseEditPart> normalizedSelection = new LinkedList<AbstractBaseEditPart>(selectedEditParts);
		for (AbstractBaseEditPart editPart : selectedEditParts) {
			List<?> children = editPart.getChildren();
			if (children.size() > 0 && selectedEditParts.containsAll(children)) {
				normalizedSelection.removeAll(children);
			}
		}
	
		return normalizedSelection;
	}
}