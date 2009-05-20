package de.desy.language.snl.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ErrorManager {
	
	private Shell _shell;

	public void setShell(Shell shell) {
		_shell = shell;
	}
	
	public void markErrors(IFile source, int lineNumber, String errorMessage) {
		try {
			IMarker errorMarker = source.createMarker(IMarker.PROBLEM);
			errorMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			errorMarker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			errorMarker.setAttribute(IMarker.MESSAGE, errorMessage);
		} catch (CoreException e) {
			e.printStackTrace();
			List<String> errorList = new ArrayList<String>();
			errorList.add(errorMessage + " (in line: "+lineNumber+")");
			createErrorFeedback("Compilation fails", errorList);
		}
	}
	
	/**
	 * Shows the given message in a new {@link MessageBox}.
	 * 
	 * @param message
	 *            The message to be shown in the {@link MessageBox}
	 */
	public void createErrorFeedback(String dialogTitle, List<String> messages) {
		MessageBox messageBox = new MessageBox(_shell,
				SWT.ERROR_FAILED_EXEC);
		messageBox.setText(dialogTitle);
		StringBuffer buffer = new StringBuffer(
				"The compilations fails!\nReason(s):\n");
		for (String error : messages) {
			buffer.append("\t- ");
			buffer.append(error);
			buffer.append("\n");
		}
		messageBox.setMessage(buffer.toString());
		messageBox.open();
	}

}
