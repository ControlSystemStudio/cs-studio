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
import java.util.ArrayList;
import java.util.List;

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
import de.desy.language.snl.ui.preferences.ICompilerOptionsService;

/**
 * This class provides a SNL specific {@link TextEditor}.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public class SNLEditor extends LanguageEditor {

	private CCompilationHelper _ccompilationHelper;
	private OCompilationHelper _ocompilationHelper;

	public SNLEditor() {
		_ccompilationHelper = new CCompilationHelper();
		_ocompilationHelper = new OCompilationHelper();
	}

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
		IPreferenceStore preferenceStore = SNLUiActivator.getDefault()
				.getPreferenceStore();
		ICompilerOptionsService service = new CompilerOptionsService(
				preferenceStore);

		IFile sourceRessource = ((FileEditorInput) getEditorInput()).getFile();

		List<String> configurationErrors = checkPreferenceConfiguration(service);

		if (configurationErrors.isEmpty()) {
			File cFile = _ccompilationHelper.compileToC(sourceRessource,
					service.getSNCompilerPath(), service.getCCompilerOptions());
			if (cFile != null && cFile.exists()) {
				_ocompilationHelper.compileToO(cFile, service
						.getCCompilerPath(), service.getEpicsFolder(), service
						.getSeqFolder());
			}
		} else {
			createErrorFeedback(configurationErrors, sourceRessource);
		}

	}

	private List<String> checkPreferenceConfiguration(
			ICompilerOptionsService compilerOptionsService) {
		List<String> errorMessages = new ArrayList<String>();

		String snCompilerPath = compilerOptionsService.getSNCompilerPath();
		if (snCompilerPath == null || snCompilerPath.trim().length() > 0) {
			errorMessages
					.add("The location of the SN-Compiler is not specified.");
		}

		String cCompilerPath = compilerOptionsService.getCCompilerPath();
		if (cCompilerPath == null || cCompilerPath.trim().length() > 0) {
			errorMessages
					.add("The location of the C-Compiler is not specified.");
		}

		String epicsPath = compilerOptionsService.getEpicsFolder();
		if (epicsPath == null || epicsPath.trim().length() > 0) {
			errorMessages
					.add("The location of the EPICS environment is not specified.");
		}

		String seqPath = compilerOptionsService.getSeqFolder();
		if (seqPath == null || seqPath.trim().length() > 0) {
			errorMessages
					.add("The location of the \"Seq\" folder is not specified.");
		}

		return errorMessages;
	}

	/**
	 * Shows the given message in a new {@link MessageBox}.
	 * 
	 * @param message
	 *            The message to be shown in the {@link MessageBox}
	 */
	private void createErrorFeedback(List<String> messages, IFile sourceFile) {
		MessageBox messageBox = new MessageBox(this.getEditorSite().getShell(),
				SWT.ERROR_FAILED_EXEC);
		messageBox.setText("No Preferences set!");
		StringBuffer buffer = new StringBuffer(
				"The compilations fails!\nReason(s):\n");
		for (String error : messages) {
			buffer.append("\t- ");
			buffer.append(error);
			buffer.append("\n");

			createErrorMarker(sourceFile, error);
		}
		buffer
				.append("Please update your preferences in category SNL / Compiler!");
		messageBox.setMessage(buffer.toString());
		messageBox.open();
	}

	private void createErrorMarker(IFile sourceFile, String message) {
		IMarker errorMarker;
		try {
			errorMarker = sourceFile.createMarker(IMarker.PROBLEM);
			errorMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			errorMarker.setAttribute(IMarker.MESSAGE, message);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
