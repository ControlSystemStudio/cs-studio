package org.csstudio.diag.diles.model;

import java.util.Enumeration;

import org.eclipse.draw2d.geometry.Dimension;

public class Not extends Activity {

	public static String TERMINAL_IN = "inTerminal";
	public static String TERMINAL_OUT = "outTerminal";

	public Not() {
		setSize(new Dimension(40, 40));
	}

	@Override
	public boolean getResult() {
		return !getInput(TERMINAL_IN);
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
		setOutput(TERMINAL_OUT, getResult());
	}

	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_OUT, b);
	}

}
