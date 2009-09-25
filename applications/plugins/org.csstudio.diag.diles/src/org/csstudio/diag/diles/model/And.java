package org.csstudio.diag.diles.model;

import org.eclipse.draw2d.geometry.Dimension;

public class And extends Logic {
	public And() {
		setSize(new Dimension(46, 42));
	}

	@Override
	public boolean getResult() {
		return getInput(TERMINAL_A) & getInput(TERMINAL_B);
	}
}