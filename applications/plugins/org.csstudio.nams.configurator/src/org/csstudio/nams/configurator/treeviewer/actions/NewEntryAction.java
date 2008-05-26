package org.csstudio.nams.configurator.treeviewer.actions;

import org.csstudio.nams.configurator.editor.ConfigurationEditor;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeitergruppenBean;
import org.csstudio.nams.configurator.treeviewer.model.AlarmtopicBean;
import org.csstudio.nams.configurator.treeviewer.model.FilterBean;
import org.csstudio.nams.configurator.treeviewer.model.FilterbedingungBean;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationRoot;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class NewEntryAction implements IObjectActionDelegate {

	private ConfigurationTreeView targetPart;
	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = (ConfigurationTreeView) targetPart;
	}

	public void run(IAction action) {

		if (!selection.isEmpty()) {
			IConfigurationModel model = this.targetPart.getModel();
			Assert.isNotNull(model);

			IConfigurationBean newElement = null;
			ConfigurationType configurationType = null;

			/*
			 * Falls neuer Eintrag für eine bestehende Gruppe ausgewählt wurde,
			 * wird die ausgewählte Gruppe als parent element verwendet.
			 * Andernfalls ist parent == null
			 */
			IConfigurationGroup groupNode = null;

			// new element auf root
			if (selection.getFirstElement() instanceof IConfigurationRoot) {
				IConfigurationRoot selectedNode = (IConfigurationRoot) selection
						.getFirstElement();
				configurationType = selectedNode.getConfigurationType();
			}

			// new element auf gruppe
			if (selection.getFirstElement() instanceof IConfigurationGroup) {
				groupNode = (IConfigurationGroup) selection.getFirstElement();
				configurationType = groupNode.getConfigurationType();
			}

			Assert.isNotNull(configurationType);

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

			// setze Gruppenzugehörigkeit, falls welche existiert
			newElement.setParent(groupNode);

			Assert.isNotNull(newElement);

			try {

				ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
						newElement, model);

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
			if (this.selection.getFirstElement() instanceof IConfigurationRoot) {
				isEnabled = true;
			}

			if (this.selection.getFirstElement() instanceof SortgroupNode) {
				isEnabled = true;
			}
		}

		action.setEnabled(isEnabled);
	}
}
