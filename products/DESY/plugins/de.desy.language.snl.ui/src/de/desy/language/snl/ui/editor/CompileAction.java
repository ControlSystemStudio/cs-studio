package de.desy.language.snl.ui.editor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import de.desy.language.snl.ui.SNLUiActivator;
import de.desy.language.snl.ui.preferences.CompilerOptionsService;

public class CompileAction implements IEditorActionDelegate {

	private IFile _sourceRessource;
	private CompilerOptionsService _service;
	private File _snCompilerPath;
	private CCompilationHelper _helper;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null) {
			IEditorInput editorInput = targetEditor.getEditorInput();
			_helper = new CCompilationHelper();
			if (editorInput instanceof FileEditorInput) {
				_sourceRessource = ((FileEditorInput) editorInput).getFile();
				_service = new CompilerOptionsService(SNLUiActivator
						.getDefault().getPreferenceStore());
			}
		}
	}

	public void run(IAction action) {
		if (_service != null && _snCompilerPath != null
				&& _sourceRessource != null) {
			_helper.compileFile(_service, _sourceRessource);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do
	}

}
