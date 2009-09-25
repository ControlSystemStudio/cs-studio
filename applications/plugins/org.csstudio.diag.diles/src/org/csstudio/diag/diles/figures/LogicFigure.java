package org.csstudio.diag.diles.figures;

import org.csstudio.diag.diles.model.Logic;

public class LogicFigure extends ActivityFigure {

	FixedAnchor leftAnchor, rightAnchor, outAnchor;

	public LogicFigure() {
		leftAnchor = new FixedAnchor(this);
		leftAnchor.vertical = 9;
		targetAnchors.put(Logic.TERMINAL_A, leftAnchor);

		rightAnchor = new FixedAnchor(this);
		rightAnchor.vertical = 32;
		targetAnchors.put(Logic.TERMINAL_B, rightAnchor);

		outAnchor = new FixedAnchor(this);
		outAnchor.vertical = 20;
		outAnchor.horizontal = 46;
		sourceAnchors.put(Logic.TERMINAL_OUT, outAnchor);
	}
}
