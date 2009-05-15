/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.ui.editor.LanguageEditor;
import de.desy.language.editor.ui.editor.highlighting.AbstractRuleProvider;
import de.desy.language.snl.parser.SNLParser;
import de.desy.language.snl.ui.RuleProvider;
import de.desy.language.snl.ui.SNLUiActivator;
import de.desy.language.snl.ui.preferences.CompilerOptionsService;

/**
 * This class provides a SNL specific {@link TextEditor}.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public class SNLEditor extends LanguageEditor {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IPresentationDamager doGetPresentationDamager(
			final ITokenScanner codeScannerUsedForHighligthing) {
		return new SNLPresentationDamager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IPresentationRepairer doGetPresentationRepairer(
			final ITokenScanner codeScannerUsedForHighligthing) {
		DefaultDamagerRepairer damagerRepairer = new DefaultDamagerRepairer(
				codeScannerUsedForHighligthing);
		return damagerRepairer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractRuleProvider doGetRuleProviderForHighlighting() {
		return new RuleProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetPartitioningId() {
		return null;// DocumentSetupParticipant.LANGUAGE_PARTITIONING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractLanguageParser doGetLanguageParser() {
		return new SNLParser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doHandleSourceModifiedAndSaved(
			final IProgressMonitor progressMonitor) {
		IPreferenceStore preferenceStore = SNLUiActivator.getDefault().getPreferenceStore();
		CompilerOptionsService compilerOptionsService = new CompilerOptionsService(preferenceStore);		
		
		File snCompilerPath = compilerOptionsService.getSNCompilerPath();

		IFile sourceRessource = ((FileEditorInput) getEditorInput()).getFile();

		if (snCompilerPath == null || !snCompilerPath.exists()) {
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
			File cFile = compileToC(sourceRessource, snCompilerPath, compilerOptionsService);
			if (cFile != null && cFile.exists()) {
				File cCompilerPath = compilerOptionsService.getCCompilerPath(); 
				compileToO(cFile, cCompilerPath);
			}
		}
	}

	private void compileToO(File file, File compilerPath) {
		// TODO Auto-generated method stub
	}

	private File compileToC(IFile sourceRessource, File compilerPath, CompilerOptionsService compilerOptionsService) {
		assert sourceRessource != null : "sourceRessource != null";
		assert compilerPath != null : "compilerPath != null";

		File source = sourceRessource.getLocation().toFile();

		String fullQualifiedSourceFileName = source.getAbsolutePath();
		File rootDirectory = getRootDirectory(source);
		String sourceFileName = source.getName();

		String fullQualifiedTargetSourceName = rootDirectory.getAbsolutePath()
				+ File.separator + "generated-c" + File.separator
				+ sourceFileName + ".c";

		File targetSourceFile = new File(fullQualifiedTargetSourceName);
		if (targetSourceFile.exists()) {
			boolean deleted = targetSourceFile.delete();
			System.out.println("File deleted? " + deleted);
		} else {
			System.out.println("No old source "
					+ targetSourceFile.getAbsolutePath());
		}

		try {
			Process sncProcess = createProcess(compilerPath,
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
			if (result != 0) {
				targetSourceFile = null;
				createErrorMarker(sourceRessource, stdOutResult);
			}
		} catch (Exception ex) {
			targetSourceFile = null;
			ex.printStackTrace();
			String message = ex.getMessage();
			showCompileFailMessage(message);
		}
		return targetSourceFile;
	}

	private void createErrorMarker(IFile sourceRessource, String stdOutResult) {
		Pattern pattern = Pattern
				.compile("(syntax error: line no. )([\\d]*)([^\\n]*)(\\n)([\\S\\s]*)");
		Matcher resultMatcher = pattern.matcher(stdOutResult);
		resultMatcher.find();
		String message = resultMatcher.group(5).trim();

		IMarker errorMarker;
		try {
			errorMarker = sourceRessource.createMarker(IMarker.PROBLEM);
			errorMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_ERROR);
			errorMarker.setAttribute(IMarker.LINE_NUMBER, Integer
					.parseInt(resultMatcher.group(2).trim()));
			errorMarker.setAttribute(IMarker.MESSAGE, message);
		} catch (CoreException e) {
			e.printStackTrace();
			showCompileFailMessage(e.getMessage() + "\n" + message);
		}
	}

	private Process createProcess(File compilerPath,
			String fullQualifiedSourceFileName,
			String fullQualifiedTargetSourceName, 
			CompilerOptionsService compilerOptionsService) throws IOException {
		String sncCommand = compilerPath.getAbsolutePath() + File.separator
				+ "snc";
		
		List<String> compilerOptions = compilerOptionsService.getCompilerOptions();
		
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

	private File getRootDirectory(File source) {
		File parentFile = source.getParentFile();
		return parentFile.getParentFile();
	}

	/**
	 * Shows the given message in a new {@link MessageBox}.
	 * 
	 * @param message
	 *            The message to be shown in the {@link MessageBox}
	 */
	private void showCompileFailMessage(String message) {
		MessageBox messageBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.ERROR_FAILED_EXEC);
		messageBox.setText("Compilation fails!");
		messageBox.setMessage("The compilations of source fails!\nReason: "
				+ message);
		messageBox.open();
	}
}
