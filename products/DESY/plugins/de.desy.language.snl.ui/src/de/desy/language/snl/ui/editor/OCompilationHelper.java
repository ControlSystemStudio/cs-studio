package de.desy.language.snl.ui.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class OCompilationHelper {

	private static final String TARGET_FOLDER = "bin";
	private static final String TARGET_FILE_EXTENSION = ".o";

	public boolean compileToO(String source, String rootPath, String compilerFolder, String epicsFolder,
			String seqFolder) {
		assert source != null : "source != null";
		assert compilerFolder != null : "compilerPath != null";
		assert epicsFolder != null : "epicsFolder != null";
		assert seqFolder != null : "seqFolder != null";
		
		boolean success = true;
		
		String sourceFileName = source;
		int lastIndexOfDot = sourceFileName.lastIndexOf('.');
		int lastIndexOfSlash = sourceFileName.lastIndexOf(File.separator);
		sourceFileName = sourceFileName.substring(lastIndexOfSlash, lastIndexOfDot);

		String fullQualifiedTargetSourceName = rootPath
				+ File.separator + TARGET_FOLDER + File.separator
				+ sourceFileName + TARGET_FILE_EXTENSION;

		try {
			Process sncProcess = createProcess(
					compilerFolder, epicsFolder, seqFolder,
					source, fullQualifiedTargetSourceName);
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
			String stdErrResult = stdErrBuffer.toString();
			if (result != 0) {
				success = false;
				showCompileFailMessage(stdOutResult + "\n" + stdErrResult);
			}
		} catch (Exception ex) {
			success = false;
			ex.printStackTrace();
			String message = ex.getMessage();
			showCompileFailMessage(message);
		}
		return success;
	}

	private Process createProcess(String compilerPath, String epicsPath,
			String seqPath, String fullQualifiedSourceFileName, String fullQualifiedTargetFileName) throws IOException {

		List<String> command = new ArrayList<String>();
		command.add(compilerPath);
		command.add("-c");
		command.add("-o");
		command.add(fullQualifiedTargetFileName);
		command.add("-D_POSIX_C_SOURCE=199506L");
		command.add("-D_POSIX_THREADS");
		command.add("-D_XOPEN_SOURCE=500");
		command.add("-D_X86_");
		command.add("-DUNIX");
		command.add("-D_BSD_SOURCE");
		command.add("-Dlinux");
		command.add("-D_REENTRANT");
		command.add("-ansi");
		command.add("-O3");
		command.add("-Wall");
		command.add("-m32");
		command.add("-g");
		command.add("-fPIC");
		command.add("-I" + seqPath + "/include");
		command.add("-I" + epicsPath + "/include/os/Linux");
		command.add("-I" + epicsPath + "/include");
		command.add(fullQualifiedSourceFileName);
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		Process sncProcess = processBuilder.start();
		return sncProcess;
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
