package org.csstudio.sds.ui.internal.refactoringparticipants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class SdsFileDeleteParticipant extends DeleteParticipant {

    private IFile _deletedFile;

    public SdsFileDeleteParticipant() {
    }

    @Override
    protected boolean initialize(Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (file.getFileExtension().equalsIgnoreCase("css-sds")) {
                _deletedFile = file;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "SDS File Delete Participant";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        IWorkbenchPage activePage = findActiveWorkbenchPage();
        final IEditorPart editor = findOpenEditor(activePage, _deletedFile);
        if (editor != null) {
            closeOpenEditor(activePage, editor);
        }
        return status;
    }

    private void closeOpenEditor(final IWorkbenchPage activePage, final IEditorPart editor) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                activePage.closeEditor(editor, false);
            }
        });
    }

    private IEditorPart findOpenEditor(IWorkbenchPage activePage, IFile openFile) {
        IEditorPart editor = activePage
                .findEditor(new FileEditorInput(openFile));
        if (editor != null) {
            return editor;
        }
        return null;
    }

    private IWorkbenchPage findActiveWorkbenchPage() {
        IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench()
                .getWorkbenchWindows();
        for (IWorkbenchWindow window : workbenchWindows) {
            if (window.getActivePage() != null) {
                return window.getActivePage();
            }
        }
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return null;
    }

}
