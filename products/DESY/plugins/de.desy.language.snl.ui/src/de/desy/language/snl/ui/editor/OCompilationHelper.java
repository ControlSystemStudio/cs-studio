package de.desy.language.snl.ui.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import de.desy.language.snl.ui.preferences.ICompilerOptionsService;

public class OCompilationHelper {

	private static final String TARGET_FOLDER = "bin";
	private static final String TARGET_FILE_EXTENSION = ".o";

	public void compileFile(ICompilerOptionsService compilerOptionsService,
			IFile sourceRessource, File cFile) {
		File cCompilerPath = compilerOptionsService.getCCompilerPath();
		File epicsFolder = compilerOptionsService.getEpicsFolder();
		File seqFolder = compilerOptionsService.getSeqFolder();
		if (cCompilerPath == null || !cCompilerPath.exists()
				&& epicsFolder == null || !epicsFolder.exists()
				&& seqFolder == null || !seqFolder.exists()) {
			IMarker errorMarker;
			String message = "No compiler preferences present. Please update your preferences in category SNL / Compiler!";
			try {
				errorMarker = sourceRessource.createMarker(IMarker.PROBLEM);
				errorMarker.setAttribute(IMarker.SEVERITY,
						IMarker.SEVERITY_ERROR);
				errorMarker.setAttribute(IMarker.MESSAGE, message);
			} catch (CoreException e) {
				e.printStackTrace();
				showCompileFailMessage(message);
			}
		} else {
			compileToO(cFile, cCompilerPath, epicsFolder, seqFolder,
					compilerOptionsService);
		}
	}

	void compileToO(File source, File compilerFolder, File epicsFolder,
			File seqFolder, ICompilerOptionsService compilerOptionsService) {
		assert source != null : "source != null";
		assert compilerFolder != null : "compilerPath != null";
		assert epicsFolder != null : "epicsFolder != null";
		assert seqFolder != null : "seqFolder != null";

		String fullQualifiedSourceFileName = source.getAbsolutePath();
		File rootDirectory = getRootDirectory(source);
		String sourceFileName = source.getName();
		int lastIndexOf = sourceFileName.lastIndexOf('.');
		sourceFileName = sourceFileName.substring(0, lastIndexOf);

		String fullQualifiedTargetSourceName = rootDirectory.getAbsolutePath()
				+ File.separator + TARGET_FOLDER + File.separator
				+ sourceFileName + TARGET_FILE_EXTENSION;

		try {
			Process sncProcess = createProcess(
					compilerFolder.getAbsolutePath(), epicsFolder
							.getAbsolutePath(), seqFolder.getAbsolutePath(),
					fullQualifiedSourceFileName, fullQualifiedTargetSourceName, compilerOptionsService);
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
				createErrorMessage(stdOutResult + "\n" + stdErrResult);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			String message = ex.getMessage();
			showCompileFailMessage(message);
		}
	}

	void createErrorMessage(String stdOutResult) {
		String message = stdOutResult;
		showCompileFailMessage(message);
	}

	Process createProcess(String compilerPath, String epicsPath,
			String seqPath, String fullQualifiedSourceFileName, String fullQualifiedTargetFileName,
			ICompilerOptionsService compilerOptionsService) throws IOException {
		String cCommand = compilerPath + File.separator + "gcc";

		List<String> command = new ArrayList<String>();
		command.add(cCommand);
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
		
		System.out.println("OCompilationHelper.createProcess()");
		System.out.println(command);

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
