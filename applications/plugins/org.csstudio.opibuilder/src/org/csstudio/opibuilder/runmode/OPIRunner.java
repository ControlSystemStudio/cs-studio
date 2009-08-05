package org.csstudio.opibuilder.runmode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.csstudio.opibuilder.editor.OPIEditorContextMenuProvider;
import org.csstudio.opibuilder.editor.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

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
		else {
			displayModel = new DisplayModel();
			try {
				XMLUtil.fillDisplayModelFromInputStream(getInputStream(), displayModel);
			} catch (Exception e) {
				MessageDialog.openError(getSite().getShell(), "File Open Error",
						"The file is not a correct OPI file! \n" + e);
				CentralLogger.getInstance().error(this, e);
			}		
		}
			
		setPartName(displayModel.getName());
	}
	

	/**
	 * Returns a stream which can be used to read this editors input data.
	 * 
	 * @return a stream which can be used to read this editors input data
	 */
	private InputStream getInputStream() {
		InputStream result = null;

		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			try {
				result = ((FileEditorInput) editorInput).getFile()
						.getContents();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (editorInput instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}
		}

		return result;
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
