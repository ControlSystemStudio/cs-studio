package org.csstudio.sds.language.script.ui.editor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.sds.language.script.parser.ScriptParser;
import org.csstudio.sds.language.script.ui.rules.RuleProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.part.FileEditorInput;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;

import de.desy.language.editor.core.parser.AbstractLanguageParser;
import de.desy.language.editor.ui.editor.LanguageEditor;
import de.desy.language.editor.ui.editor.highlighting.AbstractRuleProvider;

public class ScriptEditor extends LanguageEditor {

    private DefaultDamagerRepairer _defaultDamagerRepairer;

    public ScriptEditor() {
    }

    @Override
    protected AbstractLanguageParser doGetLanguageParser() {
        return new ScriptParser();
    }

    @Override
    protected IPresentationDamager doGetPresentationDamager(
            ITokenScanner codeScannerUsedForHighligthing) {
        return new ScriptPresentationDamager();
    }

    @Override
    protected IPresentationRepairer doGetPresentationRepairer(
            ITokenScanner codeScannerUsedForHighligthing) {
        return this.getDefaultDamagerRepairer(codeScannerUsedForHighligthing);
    }

    @Override
    protected AbstractRuleProvider doGetRuleProviderForHighlighting() {
        return new RuleProvider();
    }

    private DefaultDamagerRepairer getDefaultDamagerRepairer(
            ITokenScanner codeScannerUsedForHighligthing) {
        if (_defaultDamagerRepairer == null) {
            _defaultDamagerRepairer = new DefaultDamagerRepairer(
                    codeScannerUsedForHighligthing);
        }
        return _defaultDamagerRepairer;
    }

    @Override
    protected void determineAdditionalErrors() {
        doHandleSourceModifiedAndSaved(null);
    }

    @Override
    protected void doHandleSourceModifiedAndSaved(
            IProgressMonitor progressMonitor) {
        IFile sourceRessource = ((FileEditorInput) getEditorInput()).getFile();
        try {
            InputStream contents = sourceRessource.getContents();

            String scriptString = ""; //$NON-NLS-1$

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    contents));
            while (reader.ready()) {
                scriptString = scriptString + reader.readLine() + System.getProperty("line.separator");
            }
            reader.close();

            Context scriptContext = Context.enter();
            ImporterTopLevel scriptScope = new ImporterTopLevel(scriptContext);

            try {
                scriptContext.evaluateString(scriptScope, scriptString,
                        sourceRessource.getName(), 1, null); //$NON-NLS-1$
            } catch (RhinoException re) {
                IMarker errorMarker = sourceRessource
                        .createMarker(IMarker.PROBLEM);
                errorMarker.setAttribute(IMarker.SEVERITY,
                        IMarker.SEVERITY_ERROR);
                errorMarker.setAttribute(IMarker.LINE_NUMBER, re.lineNumber());
                errorMarker.setAttribute(IMarker.MESSAGE, re.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }
    }

    @Override
    protected IContentAssistProcessor getContentAssistProcessor() {
        // TODO Auto-generated method stub
        return null;
    }
}
