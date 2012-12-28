package de.desy.language.snl.compilerconfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericCompilationHelper {

	public ErrorUnit compile(List<String> compilerParameter, Pattern errorPattern) {
		assert compilerParameter != null : "compilerOptions != null";
		
		ErrorUnit error = null;

		try {
			Process sncProcess = createProcess(compilerParameter);
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
				error = createErrorUnit(errorPattern, stdOutResult, stdErrBuffer.toString());
			}
		} catch (Exception ex) {
			List<String> errorList = new ArrayList<String>();
			errorList.add(ex.getMessage());
			error = new ErrorUnit("Can't invoke compiler", errorList);
			ex.printStackTrace();
		}
		return error;
	}

	private ErrorUnit createErrorUnit(Pattern errorPattern, String rawMessage,
			String rawDetails) {
		ErrorUnit error;
		if (errorPattern == null) {
			String message = rawMessage;
			String details = rawDetails;
			if (rawMessage.contains("\n")) {
				int lineBreak = rawMessage.indexOf("\n");
				message = rawMessage.substring(0, lineBreak);
				String detailsPart = rawMessage.substring(lineBreak + 1);
				if (detailsPart != null && detailsPart.trim().length() > 0) {
					details = detailsPart + "\n" + rawDetails;
				}
			}
			List<String> errorList = new ArrayList<String>();
			errorList.add(details);
			error = new ErrorUnit(message, errorList);
		} else {
			List<String> errorList = new ArrayList<String>();
			errorList.add(rawDetails);

			Matcher resultMatcher = errorPattern.matcher(rawMessage);
			if (resultMatcher.find()) {
				String clearedMessage = resultMatcher.group(5).trim();
				int lineNumber = Integer
						.parseInt(resultMatcher.group(2).trim());
				error = new ErrorUnit(clearedMessage, lineNumber, errorList);
			} else {
				error = new ErrorUnit(rawMessage, errorList);
			}
		}
		return error;
	}

	private Process createProcess(List<String> compilerParameter)	throws IOException {
		List<String> command = compilerParameter;
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		Process sncProcess = processBuilder.start();
		return sncProcess;
	}

}
