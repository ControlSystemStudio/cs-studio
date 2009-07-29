package org.csstudio.opibuilder.runmode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editor.OPIEditorContextMenuProvider;
import org.csstudio.opibuilder.editor.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class OPIRunner extends EditorPart {
	
	private DisplayModel displayModel;

	public OPIRunner() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}
	

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if(input instanceof RunnerInput)
			displayModel = ((RunnerInput)input).getDisplayModel();
		setPartName(displayModel.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		final PatchedScrollingGraphicalViewer viewer = 
			new PatchedScrollingGraphicalViewer();
		
		
		viewer.createControl(parent);
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.RUN_MODE));
		viewer.setContents(displayModel);
		
		//this will make viewer as a selection provider
		EditDomain editDomain = new DefaultEditDomain(this);
		editDomain.addViewer(viewer);
		
		//connect the CSS menu
		ContextMenuProvider cmProvider = 
			new OPIRunnerContextMenuProvider(viewer);
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
	}

	@Override
	public void setFocus() {

	}
	

}
