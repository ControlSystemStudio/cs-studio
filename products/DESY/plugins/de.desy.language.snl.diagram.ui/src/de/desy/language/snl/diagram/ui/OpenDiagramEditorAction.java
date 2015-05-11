package de.desy.language.snl.diagram.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class OpenDiagramEditorAction extends Action {

    private IWorkbenchWindow _window;
    private final IPath _stFilePath;

    public OpenDiagramEditorAction(IPath stFilePath) {
        assert stFilePath != null : "stFilePath != null";

        _stFilePath = stFilePath;
        _window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    public void run(IAction action) {
        IWorkbenchPage page = _window.getActivePage();

        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                _stFilePath);
        FileEditorInput input = new FileEditorInput(file);
        try {
            page.openEditor(input, Identifier.DIAGRAM_EDITOR_ID.getId());
        } catch (PartInitException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "Unable to open the SNL Diagram Editor", e);
        }
    }

}
