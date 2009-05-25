package de.desy.language.snl.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.desy.language.snl.ui.SNLUiActivator;

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
			errorList.add(errorMessage + " (in line: " + lineNumber + ")");
			createErrorFeedback("Error during compilation", "", errorList);
		}
	}

	/**
	 * Shows the given message in a new {@link MessageBox}.
	 * 
	 * @param message
	 *            The message to be shown in the {@link MessageBox}
	 */
	public void createErrorFeedback(String dialogTitle, String message,
			List<String> messages) {
		StringBuffer buffer = new StringBuffer();
		for (String error : messages) {
			if (error != null && error.trim().length() > 0) {
				buffer.append("\t- ");
				buffer.append(error);
				buffer.append("\n");
			}
		}
		Exception exception = new Exception(buffer.toString());
		IStatus status = new Status(IStatus.ERROR, SNLUiActivator.PLUGIN_ID,
				message, exception);

		ErrorDialog
				.openError(_shell, "Compilation fails!", dialogTitle, status);
	}

}
