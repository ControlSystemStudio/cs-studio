package org.csstudio.diag.diles.model;

import java.util.Enumeration;

import org.eclipse.draw2d.geometry.Dimension;

public class FlipFlop extends Activity {

	public static final String TERMINAL_J = "inTerminal_1";
	public static final String TERMINAL_K = "inTerminal_2";
	public static final String TERMINAL_Q = "outTerminal";

	public boolean input_j;
	public boolean input_k;
	public boolean result_q, old_result_q;

	public FlipFlop() {
		setSize(new Dimension(57, 42));
	}

	@Override
	public boolean getResult() {

		boolean result = false;

		if ((getInput(TERMINAL_J) && !getInput(TERMINAL_K))) {
			old_result_q = false;

			return false;
		} else if ((!getInput(TERMINAL_J) && getInput(TERMINAL_K))) {
			old_result_q = true;

			return true;
		} else if (!getInput(TERMINAL_J) && !getInput(TERMINAL_K)) {

			return old_result_q;
		} else if (getInput(TERMINAL_J) && getInput(TERMINAL_K)) {
			return !old_result_q;
		}

		return result;
	}

	protected void setOutput(String terminal, boolean val) {
		Enumeration elements = sources.elements();
		Path p;
		while (elements.hasMoreElements()) {
			p = (Path) elements.nextElement();
			// System.out.println(p.getSourceName() + " " + terminal);
			if (p.getSourceName().equals(terminal)
					&& this.equals(p.getSource())) {
				p.setStatus(val);
				// System.out.println(getClass() + " " + getResult());
			}
		}
	}

	@Override
	public void setResult() {
		setOutput(TERMINAL_Q, getResult());
	}

	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_Q, b);
	}

}
