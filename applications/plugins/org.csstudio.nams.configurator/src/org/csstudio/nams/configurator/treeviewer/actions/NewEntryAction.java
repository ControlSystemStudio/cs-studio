package org.csstudio.nams.configurator.treeviewer.actions;

import java.util.Collection;

import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeitergruppenBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmtopicBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.FilterBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.FilterbedingungBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewEntryAction implements IObjectActionDelegate {

	private Collection<String> groupNames;
	private ConfigurationTreeView targetPart;
	private IStructuredSelection selection;

	// public NewEntryAction(Collection<String> groupNames) {
	// this.groupNames = groupNames;
	// }

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = (ConfigurationTreeView) targetPart;
	}

	public void run(IAction action) {

		if (!selection.isEmpty()) {
			this.groupNames = this.targetPart.getGroupNames();

			ConfigurationBean newElement = null;
			ConfigurationType configurationType = null;

			// new element auf root
			if (selection.getFirstElement() instanceof IConfigurationNode) {
				IConfigurationNode selectedNode = (IConfigurationNode) selection
						.getFirstElement();
				configurationType = selectedNode.getConfigurationType();
			}

			// new element auf gruppe
			if (selection.getFirstElement() instanceof SortgroupNode) {
				SortgroupNode node = (SortgroupNode) selection.getFirstElement();
				configurationType = node.getGroupType();
			}

			/*
			 * prüfe, welches Element neu angelegt werden soll
			 */
			switch (configurationType) {
			case ALARMBEATERBEITER:
				newElement = new AlarmbearbeiterBean();
				break;
			case ALARMBEATERBEITERGRUPPE:
				newElement = new AlarmbearbeitergruppenBean();
				break;
			case ALARMTOPIC:
				newElement = new AlarmtopicBean();
				break;
			case FILTER:
				newElement = new FilterBean();
				break;
			case FILTERBEDINGUNG:
				newElement = new FilterbedingungBean();
				break;
			}

			try {

				ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
						newElement, this.groupNames);

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

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection) selection;
		boolean isEnabled = false;

		// enable/disable selection
		if (!this.selection.isEmpty()) {
			if (this.selection.getFirstElement() instanceof IConfigurationNode) {
				isEnabled = true;
			}

			if (this.selection.getFirstElement() instanceof SortgroupNode) {
				isEnabled = true;
			}
		}

		action.setEnabled(isEnabled);
	}
}
