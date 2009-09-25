package org.csstudio.diag.diles.model;

import org.eclipse.draw2d.geometry.Dimension;

public class Xor extends Logic {
	public Xor() {
		setSize(new Dimension(46, 42));
	}

	@Override
	public boolean getResult() {
		return getInput(TERMINAL_A) & !getInput(TERMINAL_B)
				|| !getInput(TERMINAL_A) & getInput(TERMINAL_B);
	}
}