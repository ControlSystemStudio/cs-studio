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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.ui.editor.LanguageEditor;
import de.desy.language.editor.ui.editor.highlighting.AbstractRuleProvider;
import de.desy.language.snl.SNLConstants;
import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.compilerconfiguration.AbstractTargetConfigurationProvider;
import de.desy.language.snl.compilerconfiguration.ErrorUnit;
import de.desy.language.snl.compilerconfiguration.GenericCompilationHelper;
import de.desy.language.snl.configurationservice.ConfigurationService;
import de.desy.language.snl.configurationservice.ICompilerOptionsService;
import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.parser.SNLParser;
import de.desy.language.snl.parser.nodes.StateNode;
import de.desy.language.snl.parser.parser.StateParser;
import de.desy.language.snl.ui.RuleProvider;
import de.desy.language.snl.ui.SNLUiActivator;

/**
 * This class provides a SNL specific {@link TextEditor}.
 * 
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public class SNLEditor extends LanguageEditor {

    // Vorgeschlagene Schlüsselwörter
    private final static String[] KEYWORDS = new String[] { "#define", "%#include", "%%#include",
            "when(", "state", "assign" };

    private ErrorManager _errorManager;
    private IPreferenceStore _preferenceStore;
    private ICompilerOptionsService _compilerOptionService;
    private Image stateImage = SNLUiActivator.getImageDescriptor("/icons/nodes/state.gif")
            .createImage();

    /**
     * Code Based on example from Berthold Daum 
     * Title: Java-Entwicklung mit Eclipse 3.3
     * Publisher: dpunkt verlag
     * ISBN: 978-3-89864-504-1
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 02.10.2009
     */
    public class KeywordContentAssistProcessor implements IContentAssistProcessor {

        /**
         * Stellt ein CompletionProposal-Array zusammen
         * 
         * @param viewer
         *            - der Viewer, von dem diese Methode aufgerufen wird
         * @param documentOffset
         *            - aktuelle Position im Dokument
         */
        public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
                int documentOffset) {
            // bDaum
            IDocument docu = viewer.getDocument();
            // Textselektion holen

            Point selectedRange = viewer.getSelectedRange();
            List<CompletionProposal> propList;
            propList = computeStateProposals(selectedRange.x, docu);
            if (propList.isEmpty()) {
                propList.addAll(computeKeywordProposals(getQualifier(docu, documentOffset),
                        documentOffset));
            }
            // In Array umwandeln und zurückgeben
            return (CompletionProposal[]) propList.toArray(new CompletionProposal[propList.size()]);
        }

        /**
         * Standardimplementierung für Kontextanzeige
         * 
         * @return null
         */
        public IContextInformation[] computeContextInformation(ITextViewer viewer,
                int documentOffset) {
            return null;
        }

        /**
         * Nach einem '#','%' Zeichen sollen automatisch Vorschläge erfolgen
         */
        public char[] getCompletionProposalAutoActivationCharacters() {
            // Automatisch Vorschläge machen nach Eingabe
            // folgender Zeichen
            return new char[] { '#', '%' };
        }

        /**
         * Stellt Vorschläge für HTML-Markup bereit
         * 
         * @param documentOffset
         *            - die aktuelle Cursorposition im Text
         * @param selectedText
         *            - der z.Zt. selektierte Text
         * @param docu
         * @return - Vorschlagsliste
         */
        private List<CompletionProposal> computeStateProposals(int documentOffset, IDocument docu) {
            List<CompletionProposal> propList = new ArrayList<CompletionProposal>();
            final StateParser parser = new StateParser();
            int offset = 0;
            String selectedText = getStateSelection(docu, documentOffset);
            if (selectedText.length() > 0) {
                parser.findNext(docu.get());
                while (parser.hasFoundElement()) {
                    StateNode lastFoundAsNode = parser.getLastFoundAsNode();
                    String lastFoundStatement = lastFoundAsNode.getSourceIdentifier();
                    if (lastFoundStatement.startsWith(selectedText)) {
                        int cursor = lastFoundStatement.length();
                        CompletionProposal proposal = new CompletionProposal(lastFoundStatement,
                                documentOffset - selectedText.length(), selectedText.length(),
                                cursor, stateImage, lastFoundStatement, null, null);
                        propList.add(proposal);
                    }
                    offset = parser.getEndOffsetLastFound();
                    parser.findNext(docu.get(), offset);
                }
            }
            return propList;
        }

        /**
         * Ermittelt bisher eingegebene signifikante Zeichen
         * 
         * @param docu
         *            - das Dokument, mit dem wir arbeiten
         * @param documentOffset
         *            - aktuelle Position im Dokument
         * @return - bereits eingegebener Anfang eines Schlüsselwortes
         */
        private String getStateSelection(IDocument docu, int documentOffset) {
            // Zunächst lesen wir das Dokument rückwärts bis zum
            // Anfang
            // einer Zeile oder bis wir ein Schlüsselwort erkannt
            // haben
            StringBuffer buf = new StringBuffer();
            int whiteSpaceCount = 0;
            while (true) {
                try {
                    // Zeichen vor dem Cursor holen
                    char c = docu.getChar(--documentOffset);
                    if (Character.getType(c) == Character.LINE_SEPARATOR) {
                        // Zeilenanfang oder Wortanfang erreicht,
                        // kein Schlüsselwort gefunden
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        if (whiteSpaceCount < 1) {
                            whiteSpaceCount++;
                        } else {
                            // Schlüsselwort gefunden
                            // Die erhaltene Zeichenkette bringen wir in die
                            // richtige Reihenfolge
                            String string = buf.reverse().toString();
                            if (string.startsWith("state")) {
                                return string.substring(6, string.length());
                            }
                            return "";
                        }
                    }
                    buf.append(c);
                } catch (BadLocationException e) {
                    // Dokumentanfang erreicht, kein Schlüsselwort
                    // gefunden
                    break;
                }
            }
            return "";
        }

        /**
         * Ermittelt bisher eingegebene signifikante Zeichen
         * 
         * @param docu
         *            - das Dokument, mit dem wir arbeiten
         * @param documentOffset
         *            - aktuelle Position im Dokument
         * @return - bereits eingegebener Anfang eines Schlüsselwortes
         */
        private String getQualifier(IDocument docu, int documentOffset) {
            // Zunächst lesen wir das Dokument rückwärts bis zum
            // Anfang
            // einer Zeile oder bis wir ein Schlüsselwort erkannt
            // haben
            StringBuffer buf = new StringBuffer();
            while (true) {
                try {
                    // Zeichen vor dem Cursor holen
                    char c = docu.getChar(--documentOffset);
                    if (Character.isWhitespace(c)) {
                        // Zeilenanfang oder Wortanfang erreicht,
                        // kein Schlüsselwort gefunden
                        break;
                    }
                    buf.append(c);
                    if (c == '%') {
                        // Schlüsselwort gefunden
                        c = docu.getChar(documentOffset - 1);
                        if (c == '%') {
                            buf.append(c);
                            return buf.reverse().toString();
                        }
                        // Die erhaltene Zeichenkette bringen wir in die
                        // richtige Reihenfolge
                        return buf.reverse().toString();
                    } else if (c == '#') {
                        c = docu.getChar(documentOffset - 1);
                        if (c == '%') {
                            buf.append(c);
                            return buf.reverse().toString();
                        }
                        // Die erhaltene Zeichenkette bringen wir in die
                        // richtige Reihenfolge
                        return buf.reverse().toString();
                    }
                } catch (BadLocationException e) {
                    // Dokumentanfang erreicht, kein Schlüsselwort
                    // gefunden
                    break;
                }
            }
            return "";
        }

        /**
         * 
         * Stellt Schlüsselwort-Vorschläge zusammen
         * 
         * @param qualifier
         *            - Anfang eines Schlüsselwortes zur Eingrenzung der Vorschläge
         * @param documentOffset
         *            - die aktuelle Cursorposition im Text
         * @return - Liste der vorgeschlagenen Strings
         */
        private List<CompletionProposal> computeKeywordProposals(String qualifier,
                int documentOffset) {
            List<CompletionProposal> propList = new ArrayList<CompletionProposal>();
            for (int i = 0; i < KEYWORDS.length; i++) {
                String insert = KEYWORDS[i] + " ";
                if (insert.startsWith(qualifier)) {
                    // Wir lassen nur die Vorschläge zu,
                    // die dem angefangenen Schlüsselwort entsprechen
                    int cursor = insert.length();
                    CompletionProposal proposal = new CompletionProposal(insert, documentOffset
                            - qualifier.length(), qualifier.length(), cursor);
                    propList.add(proposal);
                }
            }
            return propList;
        }

        /**
         * Standardimplementierung für Kontextaktivierung
         * 
         * @return null
         */
        public char[] getContextInformationAutoActivationCharacters() {
            return null;
        }

        /**
         * Standardimplementierung für die Kontextvalidierung
         * 
         * @return null
         */
        public IContextInformationValidator getContextInformationValidator() {
            return null;
        }

        /**
         * Standardimplementierung für Fehlermeldungen
         * 
         * @return null
         */
        public String getErrorMessage() {
            return null;
        }
    }

    public SNLEditor() {
        _errorManager = new ErrorManager();
        _preferenceStore = SNLUiActivator.getDefault().getPreferenceStore();
        _compilerOptionService = new CompilerOptionsService(_preferenceStore);
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
    protected void doHandleSourceModifiedAndSaved(final IProgressMonitor progressMonitor) {
        if (_compilerOptionService.getSaveAndCompile()) {
            compileFile(progressMonitor);
        }
    }

    public void compileFile(final IProgressMonitor progressMonitor) {
        if (!_errorManager.isShellSet()) {
            _errorManager.setShell(this.getEditorSite().getShell());
        }

        String targetPlatform = _preferenceStore.getString(SNLUiActivator.PLUGIN_ID
                + PreferenceConstants.TARGET_PLATFORM);
        IFile sourceRessource = ((FileEditorInput) getEditorInput()).getFile();
        IProject baseDirectory = sourceRessource.getProject();

        ErrorUnit errorUnit = checkEnvironmentConfiguration(baseDirectory, progressMonitor,
                targetPlatform);

        if (errorUnit == null) {
            if (targetPlatform.equals("none")) {
                MessageBox box = new MessageBox(this.getSite().getShell(), SWT.ICON_INFORMATION);
                box.setText("No compiler configuration selected");
                box
                        .setMessage("No files generated.\nChoose a compiler configuration in the preferences.");
                box.open();
            } else {
                // We want the base directory (the project of the *.st file)
                String basePath = baseDirectory.getLocation().toFile().getAbsolutePath();

                String sourceFileName = extractFileName(sourceRessource);

                GenericCompilationHelper compiler = new GenericCompilationHelper();

                invokeCompilers(targetPlatform, sourceRessource, basePath, sourceFileName, compiler);
                if (!_compilerOptionService.getKeepGeneratedFiles()) {
                    deleteFilesInGeneratedFolder(baseDirectory, progressMonitor);
                }
            }
        } else {
            _errorManager.createErrorFeedback("Compilation aborted!", errorUnit.getMessage(),
                    errorUnit.getDetails());
        }
    }

    private ErrorUnit checkEnvironmentConfiguration(IProject baseDirectory,
            IProgressMonitor progressMonitor, String targetPlatform) {
        ErrorUnit error = null;
        List<String> errorMessages = new ArrayList<String>();
        if (targetPlatform == null || targetPlatform.trim().length() < 1) {
            String errorDetail = "Target Platform not valid";
            errorMessages.add(errorDetail);
        }
        if (!targetPlatform.equals("none")) {
            errorMessages.addAll(checkPreferenceConfiguration(_compilerOptionService));
            errorMessages.addAll(checkDirectories(baseDirectory, progressMonitor));
            if (!errorMessages.isEmpty()) {
                error = new ErrorUnit("Compilation could not be initialized", errorMessages);
            }
        }

        return error;
    }

    private String extractFileName(IFile sourceRessource) {
        String sourceFileName = sourceRessource.getName();
        int lastIndexOfDot = sourceFileName.lastIndexOf('.');
        int lastIndexOfSlash = sourceFileName.lastIndexOf(File.separator);
        sourceFileName = sourceFileName.substring(lastIndexOfSlash + 1, lastIndexOfDot);
        return sourceFileName;
    }

    private void invokeCompilers(String targetPlatform, IFile sourceRessource, String basePath,
            String sourceFileName, GenericCompilationHelper compiler) {
        AbstractTargetConfigurationProvider provider = ConfigurationService.getInstance()
                .getProvider(targetPlatform);
        List<AbstractCompilerConfiguration> configurations = provider
                .getConfigurations(_compilerOptionService);

        ErrorUnit errorUnit;
        for (AbstractCompilerConfiguration configuration : configurations) {
            String sourceFile = createFullFileName(basePath, configuration.getSourceFolder(),
                    sourceFileName, configuration.getSourceFileExtension());
            String targetFile = createFullFileName(basePath, configuration.getTargetFolder(),
                    sourceFileName, configuration.getTargetFileExtension());

            errorUnit = compiler
                    .compile(configuration.getCompilerParameters(sourceFile, targetFile),
                            configuration.getErrorPattern());

            if (errorUnit != null) {
                if (errorUnit.hasLineNumber()) {
                    _errorManager.markError(sourceRessource, errorUnit.getLineNumber(), errorUnit
                            .getMessage());
                } else {
                    _errorManager.createErrorFeedback("Compilation fails!", errorUnit.getMessage(),
                            errorUnit.getDetails());
                }
                break;
            }
        }
    }

    private void deleteFilesInGeneratedFolder(IProject project, IProgressMonitor progressMonitor) {
        IFolder folder = project.getFolder(SNLConstants.GENERATED_FOLDER.getValue());
        if (folder.exists()) {
            try {
                folder.delete(true, progressMonitor);
            } catch (CoreException e) {
                e.printStackTrace();
                ArrayList<String> details = new ArrayList<String>();
                details.add(e.getMessage());
                _errorManager.createErrorFeedback("Deletion failed",
                        "Could not delete generated files folder", details);
            }
        }
    }

    private List<String> checkDirectories(IProject baseDirectory, IProgressMonitor monitor) {
        List<String> result = new ArrayList<String>();
        IFolder folder = baseDirectory.getFolder(SNLConstants.GENERATED_FOLDER.getValue());
        if (!folder.exists()) {
            try {
                folder.create(true, true, monitor);
            } catch (CoreException e) {
                result.add("Not able to create " + SNLConstants.GENERATED_FOLDER.getValue()
                        + " folder");
            }
        }

        folder = baseDirectory.getFolder(SNLConstants.BIN_FOLDER.getValue());
        if (!folder.exists()) {
            try {
                folder.create(true, true, monitor);
            } catch (CoreException e) {
                result.add("Not able to create " + SNLConstants.BIN_FOLDER.getValue() + " folder");
            }
        }
        return result;
    }

    private String createFullFileName(String basePath, String folder, String fileName,
            String fileExtension) {
        String fullQualifiedSourceName = basePath + File.separator + folder + File.separator
                + fileName + fileExtension;
        return fullQualifiedSourceName;
    }

    private List<String> checkPreferenceConfiguration(ICompilerOptionsService compilerOptionsService) {
        List<String> errorMessages = new ArrayList<String>();

        String snCompilerPath = compilerOptionsService.getSNCompilerPath();
        if (snCompilerPath == null || snCompilerPath.trim().length() < 1) {
            errorMessages.add("The location of the SN-Compiler is not specified.");
        }

        String cCompilerPath = compilerOptionsService.getCCompilerPath();
        if (cCompilerPath == null || cCompilerPath.trim().length() < 1) {
            errorMessages.add("The location of the C-Compiler is not specified.");
        }

        String preCompilerPath = compilerOptionsService.getPreCompilerPath();
        if (preCompilerPath == null || preCompilerPath.trim().length() < 1) {
            errorMessages.add("The location of the precompiler is not specified.");
        }

        String applicationCompilerPath = compilerOptionsService.getApplicationCompilerPath();
        if (applicationCompilerPath == null || applicationCompilerPath.trim().length() < 1) {
            errorMessages.add("The location of the application compiler is not specified.");
        }

        String epicsPath = compilerOptionsService.getEpicsFolder();
        if (epicsPath == null || epicsPath.trim().length() < 1) {
            errorMessages.add("The location of the EPICS environment is not specified.");
        }

        String seqPath = compilerOptionsService.getSeqFolder();
        if (seqPath == null || seqPath.trim().length() < 1) {
            errorMessages.add("The location of the \"Seq\" folder is not specified.");
        }

        return errorMessages;
    }

    @Override
    protected IContentAssistProcessor getContentAssistProcessor() {
        return new KeywordContentAssistProcessor();
    }

}
