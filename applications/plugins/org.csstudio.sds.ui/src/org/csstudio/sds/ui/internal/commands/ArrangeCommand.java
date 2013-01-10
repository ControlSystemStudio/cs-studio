package org.csstudio.sds.ui.internal.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.internal.editor.Arrange;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class ArrangeCommand extends Command {

	private final List<AbstractBaseEditPart> _editParts;
	private final String _positionProperty;
	private final String _sizeProperty;
	private Command _chain;

	public ArrangeCommand(List<AbstractBaseEditPart> editParts, Arrange direction) {
		_editParts = editParts;

		if (direction == Arrange.HORIZONTAL) {
			_positionProperty = AbstractWidgetModel.PROP_POS_X;
			_sizeProperty = AbstractWidgetModel.PROP_WIDTH;
		} else {
			_positionProperty = AbstractWidgetModel.PROP_POS_Y;
			_sizeProperty = AbstractWidgetModel.PROP_HEIGHT;
		}
	}

	@Override
	public void execute() {
		if (_chain == null) {
			_chain = createCommandChain();
		}

		_chain.execute();
	}

	@Override
	public void undo() {
		if (_chain != null) {
			_chain.undo();
		}
	}

	private Command createCommandChain() {
		CompoundCommand chain = new CompoundCommand();

		sort(_editParts);
		double startPosition = calculateStartPosition();
		double spacing = calculateSpacing();

		for (int i = 1; i < _editParts.size() - 1; i++) {
			AbstractWidgetModel widget = _editParts.get(i).getWidgetModel();
			int newPosition = calculateNewPosition(startPosition, spacing, i);

			chain.add(new SetPropertyCommand(widget, _positionProperty, newPosition));
		}

		return chain;
	}

	private void sort(List<AbstractBaseEditPart> selectedEditParts) {
		Collections.sort(selectedEditParts, new Comparator<AbstractBaseEditPart>() {

			@Override
			public int compare(AbstractBaseEditPart o1, AbstractBaseEditPart o2) {
				return (int) (calculateCenter(o1.getWidgetModel()) - calculateCenter(o2.getWidgetModel()));
			}
		});
	}

	private double calculateCenter(AbstractWidgetModel widget) {
		return widget.getIntegerProperty(_positionProperty) + (widget.getIntegerProperty(_sizeProperty) / 2);
	}

	private double calculateEndPosition() {
		return calculateCenter(_editParts.get(_editParts.size() - 1).getWidgetModel());
	}

	private double calculateStartPosition() {
		return calculateCenter(_editParts.get(0).getWidgetModel());
	}

	private double calculateSpacing() {
		double leftBound = calculateStartPosition();
		double rightBound = calculateEndPosition();

		return (rightBound - leftBound) / (_editParts.size() - 1);
	}

	private int calculateNewPosition(double startPosition, double spacing, int index) {
		return (int) (startPosition + (index * spacing) - (_editParts.get(index).getWidgetModel()
				.getIntegerProperty(_sizeProperty) / 2));
	}
}
