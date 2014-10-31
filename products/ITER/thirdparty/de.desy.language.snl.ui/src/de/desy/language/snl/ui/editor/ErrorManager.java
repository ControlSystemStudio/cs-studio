package de.desy.language.snl.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import de.desy.language.snl.ui.SNLUiActivator;

/**
 * A Manger for errors occurred during compilation.
 * 
 * @author Kai Meyer (C1 WPS)
 * 
 */
public class ErrorManager {

	/**
	 * The {@link Shell} used for the error dialog.
	 */
	private Shell _shell;
	private final String _lineSeparator;
	
	public ErrorManager() {
		_lineSeparator = System.getProperty("line.separator");
	}

	/**
	 * Sets the shell used for the error dialog.
	 * 
	 * @param shell
	 *            The shell for the dialog
	 */
	public void setShell(Shell shell) {
		_shell = shell;
	}

	/**
	 * Creates a new {@link IMarker} belonging to the given file. The given
	 * <code>lineNumber</code> and the <code>errorMessage</code> are used for
	 * the marker. If the marker can't be created, then a dialog displaying the
	 * error will appear.
	 * 
	 * @param source
	 *            The file where the marker belongs to
	 * @param lineNumber
	 *            The line where the error occurs
	 * @param errorMessage
	 *            The message of the marker describing the error
	 * @required isShellSet.
	 */
	public void markError(IFile source, int lineNumber, String errorMessage) {
		try {
			IMarker errorMarker = source.createMarker(IMarker.PROBLEM);
			errorMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			errorMarker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			errorMarker.setAttribute(IMarker.MESSAGE, errorMessage);
			List<String> errorList = new ArrayList<String>();
			errorList.add(errorMessage + " (in line: " + lineNumber + ")");
			createErrorFeedback("Error during compilation", "", errorList);
		} catch (CoreException e) {
			e.printStackTrace();
			
		}
	}

	/**
	 * Shows the given message in a {@link ErrorDialog}.
	 * 
	 * @param dialogTitle
	 *            The title for the {@link ErrorDialog}
	 * @param message
	 *            The message to be shown in the {@link ErrorDialog}
	 * @param details
	 *            The details for the error message used int the Details Area of
	 *            the {@link ErrorDialog}
	 * @required isShellSet.
	 */
	public void createErrorFeedback(String dialogTitle, String message,
			List<String> details) {
		assert details != null : "details != null";
		assert dialogTitle != null : "dialogTitle != null";
		assert dialogTitle.trim().length() != 0 : "dialogTitle.trim().length() != 0";
		assert message != null : "message != null";
		assert message.trim().length() != 0 : "message.trim().length() != 0";
		assert isShellSet() : "isShellSet()";

		StringBuffer buffer = new StringBuffer();
		for (String error : details) {
			if (error != null && error.trim().length() > 0) {
				buffer.append("\t- ");
				buffer.append(error);
				buffer.append(_lineSeparator);
			}
		}
		Exception exception = new Exception(buffer.toString());
		
		IStatus status = new Status(IStatus.ERROR, SNLUiActivator.PLUGIN_ID,
				message, exception);
		
		ErrorDialog
				.openError(_shell, "Compilation fails!", dialogTitle, status);
	}

	/**
	 * Checks if a shell used for the dialog is set.
	 * 
	 * @return <code>true</code> if a shell is set, <code>false</code>
	 *         otherwise.
	 */
	public boolean isShellSet() {
		return _shell != null;
	}

}
