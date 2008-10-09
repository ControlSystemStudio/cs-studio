package de.desy.language.snl.diagram.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;

public class StateFigure extends Label {
	
	private String _name;

	public StateFigure(String name) {
		_name = name;
		this.setText(_name);
		this.setBackgroundColor(ColorConstants.cyan);
		LineBorder lineBorder = new LineBorder();
		lineBorder.setColor(ColorConstants.black);
		this.setBorder(lineBorder);
	}

}
