package org.csstudio.diag.diles.model;

import java.util.Enumeration;

abstract public class Logic extends Activity {

	public static String TERMINAL_A = "inTerminal_1";
	public static String TERMINAL_B = "inTerminal_2";
	public static String TERMINAL_OUT = "outTerminal";

	@Override
	abstract public boolean getResult();

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

	/**
	 * Sets output depending on the input.
	 */
	@Override
	public void setResult() {
		setOutput(TERMINAL_OUT, getResult());
	}

	/**
	 * Sets output manually.
	 * 
	 * @param b
	 *            manual output
	 */
	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_OUT, b);
	}

}
