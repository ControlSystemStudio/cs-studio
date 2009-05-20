package de.desy.language.snl.ui.editor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.ui.SNLEditorConstants;

public class CCompilationHelper {

	public ErrorUnit compileToC(String sourceRessource, String rootPath, String compilerFolder,
			List<String> compilerOptions) {
		assert sourceRessource != null : "sourceRessource != null";
		assert compilerFolder != null : "compilerFolder != null";
		assert compilerOptions != null : "compilerOptions != null";
		
		ErrorUnit error = null;

		String sourceFileName = sourceRessource;
		int lastIndexOfDot = sourceFileName.lastIndexOf('.');
		int lastIndexOfSlash = sourceFileName.lastIndexOf(File.separator);
		sourceFileName = sourceFileName.substring(lastIndexOfSlash+1, lastIndexOfDot);

		String fullQualifiedTargetSourceName = rootPath
				+ File.separator + SNLEditorConstants.GENERATED_C_FOLDER.getValue() + File.separator
				+ sourceFileName + SNLEditorConstants.C_FILE_EXTENSION.getValue();

		try {
			Process sncProcess = createProcess(compilerFolder,
					sourceRessource, fullQualifiedTargetSourceName,
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
				error = createErrorUnit(stdOutResult);
			}
		} catch (Exception ex) {
			error = new ErrorUnit(ex.getMessage());
			ex.printStackTrace();
		}
		return error;
	}

	private ErrorUnit createErrorUnit(String stdOutResult) {
		Pattern pattern = Pattern
				.compile("(syntax error: line no. )([\\d]*)([^\\n]*)(\\n)([\\S\\s]*)");
		Matcher resultMatcher = pattern.matcher(stdOutResult);
		resultMatcher.find();
		String message = resultMatcher.group(5).trim();
		int lineNumber = Integer.parseInt(resultMatcher.group(2).trim());
		ErrorUnit error = new ErrorUnit(message, lineNumber);
		return error;
	}

	private Process createProcess(String compilerPath,
			String fullQualifiedSourceFileName,
			String fullQualifiedTargetSourceName, List<String> compilerOptions)
			throws IOException {
		
		List<String> command = new ArrayList<String>();
		command.add(compilerPath);
		command.addAll(compilerOptions);
		command.add("-o ");
		command.add(fullQualifiedTargetSourceName);
		command.add(fullQualifiedSourceFileName);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		Process sncProcess = processBuilder.start();
		return sncProcess;
	}

}
