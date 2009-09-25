package org.csstudio.diag.diles.model;

public class TDETimer extends Timer {

	@Override
	public boolean getResult() {
		if (!getInput(TERMINAL_IN)) {
			super.setTimeDelay(true);
		}
		return true;
	}

}
