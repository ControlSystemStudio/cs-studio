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
package de.desy.language.editor.ui.editor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.desy.language.editor.core.measurement.IMeasurementProvider;
import de.desy.language.editor.core.measurement.IUpdateListener;
import de.desy.language.editor.core.measurement.KeyValuePair;
import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.core.parser.Node;
import de.desy.language.editor.ui.EditorUIActivator;
import de.desy.language.editor.ui.editor.highlighting.AbstractRuleProvider;
import de.desy.language.editor.ui.eventing.UIEvent;
import de.desy.language.editor.ui.eventing.UIEventListener;
import de.desy.language.editor.ui.outline.LanguageOutlinePage;
import de.desy.language.editor.ui.preferences.PreferenceConstants;
import de.desy.language.libraries.utils.contract.Contract;

/**
 * This abstract class provides additional predefined behavior for source code
 * editing of structured programming languages.
 *
 * <strong>Note:</strong> You have to register your editor on the Eclipse
 * extension point 'org.eclipse.ui.editor' to make it accessible in Eclipse. If
 * you like to assign your editor to a specified content type you have to
 * register the editor
 *
 * To configure the behavior redefine (override) the corresponding
 * implementation-methods; all starting with the keyword 'do'. Cause this editor
 * inherits from TextEditor all configurations possible to a TextEditor also
 * apply to editors based in this class but be careful: please do first search
 * for a do-method before replacing behavior of the TextEditor class cause some
 * configurations used by this class and may be incompatible.
 *
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.2
 * @modifications-start
 * @modified 2008-01-20, MZ, Configuration moved from extension points to
 *           extending the editor to allow better control and to map to the
 *           SWT-default way of inheritance.
 * @modifications-end
 */
public abstract class LanguageEditor extends TextEditor {

    private final class MeasurementProvider implements IMeasurementProvider {
        private Set<IUpdateListener> _listener = new HashSet<IUpdateListener>();

        @Override
        public String getRessourceIdentifier() {
            IEditorInput input = getEditorInput();
            if (input instanceof FileEditorInput) {
                return ((FileEditorInput) getEditorInput())
                        .getName();
            }
            return "Test";
        }

        @Override
        public KeyValuePair[] getMeasuredData() {
            return _measurementData;
        }

        @Override
        public void addUpdateListener(IUpdateListener listener) {
            _listener.add(listener);
        }

        public void notifyListener() {
            for (IUpdateListener listener : _listener) {
                listener.update();
            }
        }
    }

    /**
     * The token scanner by default used for this editor.
     *
     * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
     * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
     * @version 0.2
     */
    public static class LanguagesEditorsTokenScanner extends RuleBasedScanner {

        /**
         * The rule provider the rules loaded and reloaded from.
         */
        private final AbstractRuleProvider _ruleProvider;

        /**
         * Creates a token scanner. The scanner will be automatically
         * initialized by getting the rules from the rule-provider during
         * constructor processing (the rule provider have to be fully
         * unidolized).
         *
         * @param ruleProvider
         *            The {@link AbstractRuleProvider}.
         */
        public LanguagesEditorsTokenScanner(
                final AbstractRuleProvider ruleProvider) {
            Contract.requireNotNull("ruleProvider", ruleProvider);
            this._ruleProvider = ruleProvider;
            this.createRules();
        }

        /**
         * Add rules to the scanner by catching them from the rule provider.
         */
        private void createRules() {
            final List<IRule> rules = this._ruleProvider.getCustomRules();
            final IRule[] configuredRules = rules.toArray(new IRule[rules
                    .size()]);
            this.setRules(configuredRules);
        }

        /**
         * Refreshes the rules by reloading the rules from the rule provider.
         */
        public final void refreshRules() {
            this.createRules();
        }

    }

    /**
     * The concrete token scanner to be used for highlighting.
     */
    private LanguagesEditorsTokenScanner _codeScanner;

    /**
     * The outline page used by this editor.
     */
    private LanguageOutlinePage _outlinePage;

    private MeasurementProvider _measurementProvider;

    /**
     * The current root-node to be shown in the outline-view or null if
     * currently no outline is avail.
     */
    private Node _rootNode;

    /**
     * The {@link UIEventListener} used by this editor to react on
     * configurations changes.
     */
    private UIEventListener _uiListener;
    /**
     * The {@link UIEventListener} used by this editor to react on refresh
     * requests.
     */
    private UIEventListener _refreshListener;

    private DefaultCharacterPairMatcher _pairMatcher;

    private KeyValuePair[] _measurementData;

    /**
     * Creates a language editor from the scope of concreting class.
     */
    protected LanguageEditor() {
        this.setSourceViewerConfiguration(new TextSourceViewerConfiguration() {
            @Override
            public String getConfiguredDocumentPartitioning(
                    final ISourceViewer sourceViewer) {
                final String documentPartitioning = LanguageEditor.this
                        .doGetPartitioningId();
                if (documentPartitioning == null) {
                    return super
                            .getConfiguredDocumentPartitioning(sourceViewer);
                }

                return documentPartitioning;
            }

            @Override
            public IPresentationReconciler getPresentationReconciler(
                    final ISourceViewer sourceViewer) {
                final PresentationReconciler result = new PresentationReconciler();

                final String documentPartitioning = LanguageEditor.this
                        .doGetPartitioningId();
                if (documentPartitioning != null) {
                    result.setDocumentPartitioning(documentPartitioning);
                }

                result.setDamager(LanguageEditor.this.getPresentationDamager(),
                        IDocument.DEFAULT_CONTENT_TYPE);
                result.setRepairer(LanguageEditor.this
                        .getPresentationRepairer(),
                        IDocument.DEFAULT_CONTENT_TYPE);

                return result;
            }

            @Override
            public IContentAssistant getContentAssistant(
                    ISourceViewer sourceViewer) {
                ContentAssistant assistant = new ContentAssistant();
                assistant.setContentAssistProcessor(
                        getContentAssistProcessor(),
                        IDocument.DEFAULT_CONTENT_TYPE);
                assistant
                        .setInformationControlCreator(getInformationControlCreator(sourceViewer));
                assistant.enableAutoActivation(true);
                assistant.setAutoActivationDelay(500);
                return assistant;
            }

        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // The configuration has to happen after the PartControl is created,
        // because during the creation the SourceViewerDecorationSupport is
        // initialized.
        IPreferenceStore preferenceStore = EditorUIActivator.getDefault()
                .getPreferenceStore();
        if (preferenceStore != null) {
            SourceViewerDecorationSupport support = this
                    .getSourceViewerDecorationSupport(this.getSourceViewer());
            _pairMatcher = new DefaultCharacterPairMatcher(new char[] { '{',
                    '}', '(', ')', '[', ']' });
            support.setCharacterPairMatcher(_pairMatcher);

            support.setMatchingCharacterPainterPreferenceKeys(
                    PreferenceConstants.MATCHING_CHARACTER_ENABLE
                            .getPreferenceStoreId(),
                    PreferenceConstants.MATCHING_CHARACTER_COLOR
                            .getPreferenceStoreId());
            support.setCursorLinePainterPreferenceKeys(
                    PreferenceConstants.CURSOR_LINE_ENABLE
                            .getPreferenceStoreId(),
                    PreferenceConstants.CURSOR_LINE_COLOR
                            .getPreferenceStoreId());
            support.setMarginPainterPreferenceKeys(
                    PreferenceConstants.MARGIN_PAINTER_ENABLE
                            .getPreferenceStoreId(),
                    PreferenceConstants.MARGIN_PAINTER_COLOR
                            .getPreferenceStoreId(),
                    PreferenceConstants.MARGIN_COLUMNS.getPreferenceStoreId());
            support.uninstall();
            support.install(preferenceStore);
        } else {
            System.out
                    .println("LanguageEditor.configureSourceViewerDecorationSupport() No PreferenceStore");
        }
    }

    public Node getRootNode() {
        return _rootNode;
    }

    /**
     * Disposes this editor an unregister on UI events. This method have to be
     * called, if you need to do additional disposing, redefine
     * {@link #doAdditionalDispose()}; the method will be called at the end of
     * processing.
     */
    @Override
    public void dispose() {
        setPreferenceStore(null);
        super.dispose();

        UIEvent.HIGHLIGTHING_RULE_CHANGED.removeListener(this._uiListener);
        UIEvent.TEXT_ATTRIBUTE_CHANGED.removeListener(this._uiListener);
        UIEvent.HIGHLIGHTING_REFRESH_REQUEST
                .removeListener(this._refreshListener);

        this.doAdditionalDispose();
    }

    /**
     * Redefine this method to offer additional dispose of things to be disposed
     * on editors lifecycle end.
     */
    protected void doAdditionalDispose() {
        // by default do nothing.
    }

    /**
     * Redefine this method to offer additional initializing behavior.
     */
    protected void doAdditionalInitializing() {
        // by default nothing to do.
    }

    /**
     * Returns the parser of the language viewed in this editor.
     *
     * @return The parser to be used, not null.
     */
    protected abstract AbstractLanguageParser doGetLanguageParser();

    /**
     * Returns the id for the partitioner to use.
     *
     * @return The id of the partitioner or null if no partitioner should be
     *         used.
     */
    protected String doGetPartitioningId() {
        return null;
    }

    /**
     * Returns the presentation damager to be used for this editor. If you
     * haven't a special damager, we prefer to return an instance of
     * {@link DefaultDamagerRepairer}, this instance should be the same like the
     * one returned by {@link #doGetPresentationRepairer(ITokenScanner)} .
     *
     * Example (damagerRepairer may be a field):
     *
     * <pre>
     * if (this.damagerRepairer == null) {
     *     this.damagerRepairer = new DefaultDamagerRepairer(
     *             codeScannerUsedForHighligthing);
     * }
     * return this.damagerRepairer;
     * </pre>
     *
     * @param codeScannerUsedForHighligthing
     *            The code scanner used internally by the editor; this instance
     *            is passed cause the default damage-repairer requires the token
     *            scanner.
     * @return Return the presentation damager to be used, not null.
     */
    protected abstract IPresentationDamager doGetPresentationDamager(
            ITokenScanner codeScannerUsedForHighligthing);

    /**
     * Returns the presentation repairer to be used for this editor. If you
     * haven't a special repairer, we prefer to return an instance of
     * {@link DefaultDamagerRepairer}, this instance should be the same like the
     * one returned by {@link #doGetPresentationDamager(ITokenScanner)}.
     *
     * Example (damagerRepairer may be a field):
     *
     * <pre>
     * if (this.damagerRepairer == null) {
     *     this.damagerRepairer = new DefaultDamagerRepairer(
     *             codeScannerUsedForHighligthing);
     * }
     * return this.damagerRepairer;
     * </pre>
     *
     * @param codeScannerUsedForHighligthing
     *            The code scanner used internally by the editor; this instance
     *            is passed cause the default damage-repairer requires the token
     *            scanner.
     * @return Return the presentation repairer to be used, not null.
     */
    protected abstract IPresentationRepairer doGetPresentationRepairer(
            ITokenScanner codeScannerUsedForHighligthing);

    /**
     * Returns the rule provider for text parsing while highlighting to be used
     * for this editor. By default you may create a rule provider which returns
     * an empty list of rules, than you'll get no highlighting.
     *
     * @return Return the rule provider to be used, not null.
     */
    protected abstract AbstractRuleProvider doGetRuleProviderForHighlighting();

    /**
     * Redefine this method to offer an own token scanner implementation.
     *
     * @return A valid token scanner for highlighting, not null.
     */
    protected LanguagesEditorsTokenScanner doGetTokenScanner(
            final AbstractRuleProvider ruleProvider) {
        return new LanguagesEditorsTokenScanner(ruleProvider);
    }

    /**
     * Redefine to add save behavior.
     */
    protected void doHandleAdditionalSaveProcessing() {
        // by default nothing to do.
    }

    /**
     * Offers the ability of additional handling after specified editor input
     * has been set.
     */
    protected void doHandleDoSetInputHasBeenCalled(final IEditorInput input) {
        // do nothing by default.
    }

    /**
     * Redefine this method to get informed on a committed/saved source
     * modification.
     */
    protected void doHandleSourceModifiedAndSaved(
            final IProgressMonitor progressMonitor) {
        // Nothing to do in default implementation.
    }

    /**
     * Handles the save request. To add additional save behavior redefine
     * {@link #doHandleAdditionalSaveProcessing()}.
     */
    @Override
    public final void doSave(final IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
        this.refreshParsedTree(progressMonitor);
        this.doHandleAdditionalSaveProcessing();
        this.doHandleSourceModifiedAndSaved(progressMonitor);
    }

    /**
     * Sets the editor input. To offer additional behavior after the input has
     * been set, redefine {@link #doHandleDoSetInputHasBeenCalled(IEditorInput)}
     * .
     *
     * For further information see {@link TextEditor#doSetInput(IEditorInput)}.
     */
    @Override
    protected final void doSetInput(final IEditorInput input)
            throws CoreException {
        this._codeScanner = this.doGetTokenScanner(this
                .getRuleProviderForHighlighting());
        super.doSetInput(input);

        this.doHandleDoSetInputHasBeenCalled(input);

        this.refreshParsedTree(this.getProgressMonitor());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object getAdapter(final Class adapter) {
        if (IContentOutlinePage.class.equals(adapter)) {
            if (_outlinePage == null) {
                _outlinePage = new LanguageOutlinePage(
                        new HighlightingListener() {
                            @Override
                            protected void doHighlightRegion(
                                    final int startOffset, final int endOffset,
                                    final boolean activateEditor) {
                                LanguageEditor.this.setHighlightRange(
                                        startOffset, endOffset - startOffset
                                                + 1, true);
                                if (activateEditor) {
                                    LanguageEditor.this.setFocus();
                                }
                            }
                        });
                getSourceViewer().getTextWidget()
                        .addCaretListener(_outlinePage);
            }
            this.refreshParsedTree(this.getProgressMonitor());
            return this._outlinePage;
        } else if (IMeasurementProvider.class.equals(adapter)) {
            if (_measurementProvider == null) {
                _measurementProvider = new MeasurementProvider();
            }
            return _measurementProvider;
        }
        return super.getAdapter(adapter);
    }

    /**
     * Returns the presentation damager to be used for this editor.
     *
     * @return Return the presentation damager to be used, not null.
     */
    public final IPresentationDamager getPresentationDamager() {
        final IPresentationDamager result = this
                .doGetPresentationDamager(this._codeScanner);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Returns the presentation repairer to be used for this editor.
     *
     * @return Return the presentation repairer to be used, not null.
     */
    public final IPresentationRepairer getPresentationRepairer() {
        final IPresentationRepairer result = this
                .doGetPresentationRepairer(this._codeScanner);

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Returns the rule provider for text parsing while highlighting to be used
     * for this editor.
     *
     * @return Return the rule provider to be used, not null.
     */
    public final AbstractRuleProvider getRuleProviderForHighlighting() {
        final AbstractRuleProvider result = this
                .doGetRuleProviderForHighlighting();

        Contract.ensureResultNotNull(result);
        return result;
    }

    /**
     * Initializes the editor. To add additional initialize behavior redefine
     * {@link #doAdditionalInitializing()}.
     *
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
     */
    @Override
    protected final void initializeEditor() {
        super.initializeEditor();
        this._uiListener = new UIEventListener() {
            public void eventOccourred() {
                LanguageEditor.this.refresh();
            }
        };

        this._refreshListener = new UIEventListener() {
            public void eventOccourred() {
                LanguageEditor.this.refreshHighlighting();
            }
        };

        // UIEvent.HIGHLIGTHING_RULE_CHANGED.addListener(this._uiListener);
        // UIEvent.TEXT_ATTRIBUTE_CHANGED.addListener(this._uiListener);
        // UIEvent.HIGHLIGHTING_REFRESH_REQUEST.addListener(this._refreshListener);

        this.doAdditionalInitializing();
    }

    /**
     * Refreshed the editors UI by resetting the editor input.
     */
    protected void refresh() {
        final IProgressMonitor progressMonitor = this.getProgressMonitor();
        this._codeScanner.refreshRules();
        super.getSourceViewer().invalidateTextPresentation();
        this.refreshParsedTree(progressMonitor);
    }

    protected void refreshHighlighting() {
        if (!this.isDirty()) {
            IEditorInput editorInput = this.getEditorInput();
            IEditorSite editorSite = this.getEditorSite();
            int topIndex = this.getSourceViewer().getTextWidget().getTopIndex();
            int caretOffset = this.getSourceViewer().getTextWidget()
                    .getCaretOffset();
            try {
                this.internalInit(editorSite.getWorkbenchWindow(), editorSite,
                        editorInput);
                this.getSourceViewer().getTextWidget().setCaretOffset(
                        caretOffset);
                this.getSourceViewer().getTextWidget().setTopIndex(topIndex);
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

    protected void determineAdditionalErrors() {

    }

    /**
     * Refreshes the outline view.
     */
    protected void refreshParsedTree(final IProgressMonitor progressMonitor) {

        final IEditorInput input = this.getEditorInput();
        if (input != null) {
            final AbstractLanguageParser languageParser = this
                    .doGetLanguageParser();
            if (languageParser != null) {
                final IDocument document = this.getDocumentProvider()
                        .getDocument(input);
                if (document != null) {
                    IFile sourceRessource = null;
                    if (input instanceof FileEditorInput) {
                        sourceRessource = ((FileEditorInput) getEditorInput())
                                .getFile();
                    }
                    // final Runnable parserJob = new Runnable() {
                    // public void run() {
                    LanguageEditor.this._rootNode = languageParser.parse(
                            document.get(), sourceRessource, progressMonitor);
                    _measurementData = languageParser.getMeasurementData();

                    if (_measurementProvider != null) {
                        _measurementProvider.notifyListener();
                    }

                    if (LanguageEditor.this._outlinePage != null) {
                        LanguageEditor.this._outlinePage
                                .setEditorInput(LanguageEditor.this._rootNode);
                    }
                    if (input instanceof IFileEditorInput) {
                        // search for "marker extension" in the eclipse
                        // local
                        final IFileEditorInput fileInput = (IFileEditorInput) input;
                        IMarker marker;
                        try {
                            fileInput.getFile().deleteMarkers(IMarker.PROBLEM,
                                    true, IResource.DEPTH_INFINITE);
                            for (final Node node : LanguageEditor.this._rootNode
                                    .getAllWarningNodes()) {
                                for (final String warning : node
                                        .getWarningMessages()) {
                                    marker = fileInput.getFile().createMarker(
                                            IMarker.PROBLEM);
                                    marker.setAttribute(IMarker.SEVERITY,
                                            IMarker.SEVERITY_WARNING);
                                    marker.setAttribute(IMarker.MESSAGE,
                                            warning);
                                    final int lineNumber = document
                                            .getLineOfOffset(node
                                                    .getStatementStartOffset()) + 1;
                                    marker.setAttribute(IMarker.LINE_NUMBER,
                                            lineNumber);
                                    marker.setAttribute(IMarker.CHAR_START,
                                            node.getStatementStartOffset());
                                    marker.setAttribute(IMarker.CHAR_END, node
                                            .getStatementEndOffset());
                                }
                            }
                            for (final Node node : LanguageEditor.this._rootNode
                                    .getAllErrorNodes()) {
                                for (final String error : node
                                        .getErrorMessages()) {
                                    marker = fileInput.getFile().createMarker(
                                            IMarker.PROBLEM);
                                    marker.setAttribute(IMarker.SEVERITY,
                                            IMarker.SEVERITY_ERROR);
                                    marker.setAttribute(IMarker.MESSAGE, error);
                                    final int lineNumber = document
                                            .getLineOfOffset(node
                                                    .getStatementStartOffset()) + 1;
                                    marker.setAttribute(IMarker.LINE_NUMBER,
                                            lineNumber);
                                }
                            }
                            this.determineAdditionalErrors();
                        } catch (final CoreException e) {
                            // do nothing
                        } catch (final BadLocationException e) {
                            // do nothing
                        }

                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setHighlightRange(final int offset, final int length,
            final boolean moveCursor) {
        super.setHighlightRange(offset, length, moveCursor);
        this.getSourceViewer().setSelectedRange(offset, length);
    }

    @Override
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        super.handlePreferenceStoreChanged(event);
    }

    /**
     *
     * @return the {@link IContentAssistProcessor} for this
     *         {@link LanguageEditor} or null for non.
     */
    protected abstract IContentAssistProcessor getContentAssistProcessor();
}
