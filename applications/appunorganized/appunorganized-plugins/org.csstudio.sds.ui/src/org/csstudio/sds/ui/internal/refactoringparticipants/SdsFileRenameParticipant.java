package org.csstudio.sds.ui.internal.refactoringparticipants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class SdsFileRenameParticipant extends RenameParticipant {

    private IFile _oldFile;

    public SdsFileRenameParticipant() {
    }

    @Override
    protected boolean initialize(Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (file.getFileExtension().equalsIgnoreCase("css-sds")) {
                _oldFile = file;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "SDS File Rename Participant";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        ResourceChangeChecker checker = (ResourceChangeChecker) context
                .getChecker(ResourceChangeChecker.class);
        IResourceChangeDescriptionFactory deltaFactory = checker
                .getDeltaFactory();
        IResourceDelta[] affectedChangedChildren = deltaFactory.getDelta()
                .getAffectedChildren(IResourceDelta.CHANGED);
        status = proceedDeltas(status, affectedChangedChildren);

        return status;
    }

    private RefactoringStatus proceedDeltas(RefactoringStatus status,
            IResourceDelta[] affectedChangedChildren) {
        for (IResourceDelta delta : affectedChangedChildren) {
            IResourceDelta[] children = delta.getAffectedChildren();
            IWorkbenchPage activePage = findActiveWorkbenchPage();
            boolean editorClosed = false;
            final IEditorPart editor = findOpenEditor(activePage, _oldFile);
            if (editor != null) {
                if (editor.isDirty()) {
                    status = RefactoringStatus
                            .createFatalErrorStatus("Display has unsaved changes. Please save the display before you rename the file.");
                } else {
                    closeOpenEditor(activePage, editor);
                    editorClosed = true;
                }
                if (editorClosed) {
                    createListenerToOpenNewFile(children, editor);
                }
            }
        }
        return status;
    }

    private void createListenerToOpenNewFile(IResourceDelta[] children,
            final IEditorPart editor) {
        String editorId = editor.getEditorSite().getId();
        IFile newFile = getNewFile(children);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
                new SDSFileResourceChangeListener(newFile, editorId));
    }

    private void closeOpenEditor(final IWorkbenchPage activePage,
            final IEditorPart editor) {
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

    private IFile getNewFile(IResourceDelta[] deltas) {
        for (IResourceDelta delta : deltas) {
            if (delta.getResource() instanceof IFile) {
                IFile newFile = (IFile) delta.getResource();
                if (!newFile.getFullPath().equals(_oldFile.getFullPath())) {
                    return newFile;
                }
            }
        }
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return null;
    }

    private static class SDSFileResourceChangeListener implements
            IResourceChangeListener {

        private final IFile file;
        private final String editorId;

        public SDSFileResourceChangeListener(IFile newFile, String editorId) {
            this.file = newFile;
            this.editorId = editorId;
        }

        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench()
                    .getWorkbenchWindows();
            for (IWorkbenchWindow window : workbenchWindows) {
                final IWorkbenchPage activePage = window.getActivePage();
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            activePage.openEditor(new FileEditorInput(file),
                                    editorId);
                        } catch (PartInitException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        }
    }

}
