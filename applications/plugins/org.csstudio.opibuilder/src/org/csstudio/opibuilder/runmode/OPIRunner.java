package org.csstudio.opibuilder.runmode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.csstudio.opibuilder.editor.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class OPIRunner extends EditorPart {
	
	private DisplayModel displayModel;
	
	private DisplayOpenManager displayOpenManager;

	private PatchedScrollingGraphicalViewer viewer;

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
		InputStream inputStream;
		try {
			if(input instanceof RunnerInput){
				inputStream = ((RunnerInput)input).getFile().getContents();
				displayOpenManager = ((RunnerInput)input).getDisplayOpenManager();					
			}else
				inputStream = getInputStream();
			
			displayModel = new DisplayModel();
				
			XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel);
		}catch(Exception e) {
			CentralLogger.getInstance().error(
					this, "Failed to run file: " + ((RunnerInput)input).getFile(), e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
					"The file is not a correct OPI file! An empty OPI will be created instead.\n" + e);
		}
			
		if(input instanceof RunnerInput){
			MacrosInput macrosInput = ((RunnerInput)input).getMacrosInput();
				if(macrosInput != null)
					displayModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, macrosInput);
		}
		if(viewer != null){
			viewer.setContents(displayModel);
			setPartName(displayModel.getName());
		}
		
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
		viewer = new PatchedScrollingGraphicalViewer();
		
		
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
		setPartName(displayModel.getName());
	}

	
	@Override
	public void setFocus() {
		
	}
	
	public DisplayModel getDisplayModel() {
		return displayModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if(adapter == DisplayOpenManager.class){
			if(displayOpenManager == null)
				displayOpenManager = new DisplayOpenManager();
			return displayOpenManager;
		}
			
		return super.getAdapter(adapter);
	}
}
