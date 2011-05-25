package org.csstudio.sds.ui.internal.actions;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.commands.ArrangeCommand;
import org.csstudio.sds.ui.internal.editor.Arrange;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.Action;

public class ArrangeAction extends Action implements UpdateAction {

	public static final String HORIZONTAL = "org.csstudio.sds.ui.internal.editor.ArrangeAction.Horizontal";
	public static final String VERTICAL = "org.csstudio.sds.ui.internal.editor.ArrangeAction.Vertical";

	private final DisplayEditor _displayEditor;
	private final Arrange _direction;

	public ArrangeAction(DisplayEditor displayEditor, Arrange direction) {
		this._displayEditor = displayEditor;
		this._direction = direction;
		if (direction == Arrange.HORIZONTAL) {
			setId(HORIZONTAL);
		} else {
			setId(VERTICAL);
		}
	}

	@Override
	public void run() {
		List<AbstractBaseEditPart> selectedEditParts = normalizeGroupingSelections(_displayEditor
				.getSelectedEditParts());
		if (isFeasible(selectedEditParts)) {
			_displayEditor.getCommandStack().execute(new ArrangeCommand(selectedEditParts, _direction));
		}
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

	private boolean isFeasible(List<AbstractBaseEditPart> selectedEditParts) {
		return selectedEditParts.size() > 2 && areSiblings(selectedEditParts);
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
		setEnabled(isFeasible(normalizeGroupingSelections(_displayEditor.getSelectedEditParts())));
	}
}