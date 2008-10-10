package de.desy.language.snl.diagram.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;

import de.desy.language.snl.parser.nodes.StateNode;

public class StateFigure extends Label {
	
	private StateNode _state;

	public StateFigure(StateNode state) {
		_state = state;
		this.setText(_state.getSourceIdentifier());
		Label label = new Label();
		label.setText(_state.getContent());
		label.setBackgroundColor(ColorConstants.tooltipBackground);
		this.setToolTip(label);
		this.setBackgroundColor(ColorConstants.cyan);
		LineBorder lineBorder = new LineBorder();
		lineBorder.setColor(ColorConstants.black);
		this.setBorder(lineBorder);
	}

}
