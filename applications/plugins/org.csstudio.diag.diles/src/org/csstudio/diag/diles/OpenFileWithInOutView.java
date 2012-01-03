package org.csstudio.diag.diles;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenFileWithInOutView implements IObjectActionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(OpenFileWithInOutView.class);
    
	private IStructuredSelection _selection;
	private static Integer inOutViewID = 0;

	public OpenFileWithInOutView() {
		super();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void run(IAction action) {
		if (_selection != null) {
			Object element = _selection.getFirstElement();
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(file.getName());
				IEditorReference editorReference = getEditorReference(file,
						page);
				// open editor if it is not open.
				if (editorReference == null) {
					LOG.debug("editor is not open");
					try {
						IEditorPart openEditor = page.openEditor(
								new FileEditorInput(file), desc.getId());
						openInOutViewForEditor(page, openEditor);
					} catch (PartInitException e) {
						LOG.error("Can not open diles editor! ", e);
					}
				// open view if the editor is already open
				} else {
					IEditorPart editor = editorReference.getEditor(false);
					try {
						openInOutViewForEditor(page, editor);
					} catch (PartInitException e) {
						LOG.error("Can not open diles editor! ", e);
					}
				}
			}
		}
	}

	private void openInOutViewForEditor(IWorkbenchPage page,
			IEditorPart openEditor) throws PartInitException {
		if (openEditor instanceof DilesEditor) {
			DilesEditor dilesEditor = (DilesEditor) openEditor;
			LOG.debug("check if it is a diles editor, open in/out view");
			IViewPart view = page.showView(InOutView.ID, (inOutViewID++)
					.toString(), IWorkbenchPage.VIEW_ACTIVATE);
			if (view instanceof InOutView) {
				InOutView inOutView = (InOutView) view;
				dilesEditor.setInOutView(inOutView);
			} else {
				LOG.error("Cannot set input to In/out view because view is of unknown type");
			}

		} else {
			LOG.error("Cannot set input to In/out view because editor is of unknown type");
		}
	}

	private IEditorReference getEditorReference(IFile file, IWorkbenchPage page) {
		IEditorReference editorReference = null;
		FileEditorInput fileEditorInput = new FileEditorInput(file);
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (IEditorReference iEditorReference : editorReferences) {
			String fileName = "";
			try {
				fileName = iEditorReference.getEditorInput().getName();
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fileName.equals(fileEditorInput.getName())) {
				editorReference = iEditorReference;
				break;
			}
		}
		return editorReference;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selection = (IStructuredSelection) selection;
		}
	}

}
