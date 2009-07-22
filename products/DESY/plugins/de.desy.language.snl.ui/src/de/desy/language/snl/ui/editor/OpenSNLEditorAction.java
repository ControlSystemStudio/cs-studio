package de.desy.language.snl.ui.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenSNLEditorAction extends Action implements
		IEditorActionDelegate, IViewActionDelegate {

	private IWorkbenchWindow window;

	/**
	 * Konstruktor, meldet die AccountEditorAction als SelectionListener an
	 */
	public OpenSNLEditorAction() {
		this.window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		Logger.getAnonymousLogger().log(Level.INFO,
				"AccountEditorAction.setActiveEditor(...)");
	}

	public void run(IAction action) {
//		if (_accountNumber != null) {
//			IWorkbenchPage page = this.window.getActivePage();
//
//			AccountEditorInput input = new AccountEditorInput(
//					this._accountNumber);
//			try {
//				page.openEditor(input, AccountEditor.ID);
//			} catch (PartInitException e) {
//				Logger.getAnonymousLogger().log(Level.SEVERE,
//						"Unable to open the AccountEditor", e);
//			}
//		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		setEnabled(selection != null);

		IStructuredSelection sel = (IStructuredSelection) selection;
	}

	public void init(IViewPart view) {
		// Logger.getAnonymousLogger().log(Level.INFO, "initView");
	}

}
