package de.desy.language.snl.ui.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class CCompilationHelper {

	private static final String TARGET_FILE_EXTENSION = ".c";
	private static final String TARGET_FOLDER = "generated-c";

	public File compileToC(IFile sourceRessource, String compilerFolder,
			List<String> compilerOptions) {
		assert sourceRessource != null : "sourceRessource != null";
		assert compilerFolder != null : "compilerFolder != null";
		assert compilerOptions != null : "compilerOptions != null";

		File source = sourceRessource.getLocation().toFile();

		String fullQualifiedSourceFileName = source.getAbsolutePath();
		File rootDirectory = getRootDirectory(source);
		String sourceFileName = source.getName();
		int lastIndexOf = sourceFileName.lastIndexOf('.');
		sourceFileName = sourceFileName.substring(0, lastIndexOf);

		String fullQualifiedTargetSourceName = rootDirectory.getAbsolutePath()
				+ File.separator + TARGET_FOLDER + File.separator
				+ sourceFileName + TARGET_FILE_EXTENSION;

		try {
			Process sncProcess = createProcess(compilerFolder,
					fullQualifiedSourceFileName, fullQualifiedTargetSourceName,
					compilerOptions);
			InputStream stdOut = sncProcess.getInputStream();
			InputStream stdErr = sncProcess.getErrorStream();
			int result = sncProcess.waitFor();
			int c;
			StringBuffer stdOutBuffer = new StringBuffer();
			while ((c = stdOut.read()) != -1) {
				stdOutBuffer.append((char) c);
			}
			String stdOutResult = stdOutBuffer.toString();
			StringBuffer stdErrBuffer = new StringBuffer();
			while ((c = stdErr.read()) != -1) {
				stdErrBuffer.append((char) c);
			}
			if (result != 0) {
				createErrorMarker(sourceRessource, stdOutResult);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			String message = ex.getMessage();
			showCompileFailMessage(message);
		}
		File targetSourceFile = new File(fullQualifiedTargetSourceName);
		if (!targetSourceFile.exists()) {
			targetSourceFile = null;
		}
		return targetSourceFile;
	}

	void createErrorMarker(IFile sourceRessource, String stdOutResult) {
		Pattern pattern = Pattern
				.compile("(syntax error: line no. )([\\d]*)([^\\n]*)(\\n)([\\S\\s]*)");
		Matcher resultMatcher = pattern.matcher(stdOutResult);
		resultMatcher.find();
		String message = resultMatcher.group(5).trim();

		try {
			IMarker errorMarker = sourceRessource.createMarker(IMarker.PROBLEM);
			errorMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			errorMarker.setAttribute(IMarker.LINE_NUMBER, Integer
					.parseInt(resultMatcher.group(2).trim()));
			errorMarker.setAttribute(IMarker.MESSAGE, message);
		} catch (CoreException e) {
			e.printStackTrace();
			showCompileFailMessage(e.getMessage() + "\n" + message);
		}
	}

	Process createProcess(String compilerPath,
			String fullQualifiedSourceFileName,
			String fullQualifiedTargetSourceName, List<String> compilerOptions)
			throws IOException {
		String sncCommand = compilerPath + File.separator + "snc";

		List<String> command = new ArrayList<String>();
		command.add(sncCommand);
		command.addAll(compilerOptions);
		command.add("-o ");
		command.add(fullQualifiedTargetSourceName);
		command.add(fullQualifiedSourceFileName);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		Process sncProcess = processBuilder.start();
		return sncProcess;
	}

	File getRootDirectory(File source) {
		File parentFile = source.getParentFile();
		return parentFile.getParentFile();
	}

	/**
	 * Shows the given message in a new {@link MessageBox}.
	 * 
	 * @param message
	 *            The message to be shown in the {@link MessageBox}
	 */
	void showCompileFailMessage(String message) {
		MessageBox messageBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.ERROR_FAILED_EXEC);
		messageBox.setText("Compilation fails!");
		messageBox.setMessage("The compilations fails!\nReason: " + message);
		messageBox.open();
	}

}
