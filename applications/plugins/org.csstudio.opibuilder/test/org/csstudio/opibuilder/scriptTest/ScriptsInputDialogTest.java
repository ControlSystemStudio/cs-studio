package org.csstudio.opibuilder.scriptTest;

import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.visualparts.ScriptsInputDialog;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

public class ScriptsInputDialogTest {
	
	
	@Test
	public void testScriptsInputDialog() {
		ScriptsInputDialog dialog = new ScriptsInputDialog(
				null, new ScriptsInput(), "");
		dialog.open();
	}
	
}
