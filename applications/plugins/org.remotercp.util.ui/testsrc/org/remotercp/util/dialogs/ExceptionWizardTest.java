package org.remotercp.util.dialogs;

import org.junit.Before;
import org.junit.Test;

public class ExceptionWizardTest {

	private Exception ex;

	private String errorText;

	@Before
	public void setupException() {
		ex = new Exception("Exception occured");
		errorText = "This is an error text";
	}

	@Test
	public void testExceptionWizard() {
	    // FIXME (kasemir) : would open a window and wait for user interaction - not a test!
		//RemoteExceptionHandler.handleException(ex, errorText);
	}
}
