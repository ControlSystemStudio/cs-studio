package org.csstudio.nams.configurator.treeviewer.actions;

import org.csstudio.ams.configurationStoreService.util.TObject;
import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.treeviewer.treecomponents.CategoryNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewEntryAction extends Action {
	
	private ISelectionProvider _provider;

	public NewEntryAction(ISelectionProvider provider) {
		_provider = provider;
		this.setText("New");
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) _provider.getSelection();
		if (!selection.isEmpty()) {
			if (selection.getFirstElement() instanceof CategoryNode) {
				CategoryNode node = (CategoryNode) selection.getFirstElement();
				try {
					TObject newInstance = node.getCategory().getTObjectClass().newInstance();
					ConfigurationEditorInput editorInput = new ConfigurationEditorInput(newInstance);
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart activeEditor = activePage.getActiveEditor();
						if (activeEditor instanceof ConfigurationEditor) {
							activePage.closeEditor(activeEditor, true);
						}
					activePage.openEditor(editorInput, ConfigurationEditor.ID);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
