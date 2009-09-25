package org.csstudio.diag.diles.model;

public class TDDTimer extends Timer {

	@Override
	public boolean getResult() {
		if (getInput(TERMINAL_IN)) {
			super.setTimeDelay(true);
		}
		return false;
	}
}
