package org.csstudio.nams.configurator.treeviewer.actions;

import java.util.Collection;

import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeitergruppenBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeitergruppenNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmtopicBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmtopicNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
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
	private final ConfigurationModel model;

	public NewEntryAction(ISelectionProvider provider, ConfigurationModel model) {
		_provider = provider;
		this.model = model;
		this.setText("New");
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) _provider
				.getSelection();
		if (!selection.isEmpty()) {
			if (selection.getFirstElement() instanceof IConfigurationNode) {

				IConfigurationNode selectedNode = (IConfigurationNode) selection
						.getFirstElement();

				ConfigurationBean newElement = null;

				/*
				 * prüfe, welches Element neu angelegt werden soll
				 */
				if (selectedNode instanceof AlarmbearbeiterNode) {
					newElement = new AlarmbearbeiterBean();
				} else if (selectedNode instanceof AlarmbearbeitergruppenNode) {
					newElement = new AlarmbearbeitergruppenBean();
				} else if (selectedNode instanceof AlarmtopicNode) {
					newElement = new AlarmtopicBean();
				}

				Collection<String> sortgroupNames = this.model
						.getSortgroupNames();

				try {

					ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
							newElement, sortgroupNames);

					IWorkbenchPage activePage = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					IEditorPart activeEditor = activePage.getActiveEditor();
					if (activeEditor instanceof ConfigurationEditor) {
						activePage.closeEditor(activeEditor, true);
					}
					activePage.openEditor(editorInput, ConfigurationEditor.ID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
