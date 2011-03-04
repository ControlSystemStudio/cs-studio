/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.CompactModeAction;
import org.csstudio.opibuilder.actions.FullScreenAction;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.actions.SendEMailAction;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editor.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PartStack;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**The editor for running of OPI.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class OPIRunner extends EditorPart {

	private DisplayModel displayModel;

	private DisplayOpenManager displayOpenManager;

	private PatchedScrollingGraphicalViewer viewer;

	private ActionRegistry actionRegistry;

	public static final String ID = "org.csstudio.opibuilder.OPIRunner"; //$NON-NLS-1$

	public OPIRunner() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}


	@Override
	public void init(final IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		InputStream inputStream;
		try {
			if(input instanceof IRunnerInput){
				inputStream = ((IRunnerInput)input).getInputStream();
				displayOpenManager = ((IRunnerInput)input).getDisplayOpenManager();
			}else
				inputStream = getInputStream(input);

			displayModel = new DisplayModel();

			XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel);
			displayModel.setOpiFilePath(getOPIFilePath());
		}catch(Exception e) {
		    OPIBuilderPlugin.getLogger().log(Level.WARNING,
		            "Failed to run file: " + input, e); //$NON-NLS-1$
			String message = input + " is not a correct OPI file!\n" + e;
			MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error",
					message);
			ConsoleService.getInstance().writeError(message);
			getSite().getPage().closeEditor(this, false);
		}

		if(input instanceof IRunnerInput){
			MacrosInput macrosInput = ((IRunnerInput)input).getMacrosInput();
			if(macrosInput != null){
				macrosInput.getMacrosMap().putAll(displayModel.getMacrosInput().getMacrosMap());
				displayModel.setPropertyValue(AbstractContainerModel.PROP_MACROS, macrosInput);
			}
		}
		if(viewer != null){
			viewer.setContents(displayModel);
			setPartName(displayModel.getName());
		}

		getActionRegistry().registerAction(new PrintDisplayAction(this));
		getActionRegistry().registerAction(new FullScreenAction(this));
		getActionRegistry().registerAction(new CompactModeAction(this));
		if(SendToElogAction.isElogAvailable())
			getActionRegistry().registerAction(new SendToElogAction(this));
		getActionRegistry().registerAction(new SendEMailAction(this));


		//hide close button
		if(!displayModel.isShowCloseButton()){
			Display.getCurrent().asyncExec(new Runnable() {

				public void run() {
					PartPane currentEditorPartPane = ((PartSite) site).getPane();
					PartStack stack = currentEditorPartPane.getStack();
					Control control = stack.getControl();
					if (control instanceof CTabFolder) {
						CTabFolder tabFolder = (CTabFolder) control;
						tabFolder.getSelection().setShowClose(false);
					}
				}
			});
		}
	}

	private IPath getOPIFilePath() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {

			return ((FileEditorInput) editorInput).getFile().getFullPath();

		} else if (editorInput instanceof FileStoreEditorInput) {
			return URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
		}else if (editorInput instanceof IRunnerInput)
			return ((IRunnerInput)editorInput).getPath();
		return null;
	}
	/**
	 * Returns a stream which can be used to read this editors input data.
	 * @param editorInput
	 *
	 * @return a stream which can be used to read this editors input data
	 */
	private InputStream getInputStream(IEditorInput editorInput) {
		InputStream result = null;
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

		ScalableFreeformRootEditPart root = new PatchedScalableFreeformRootEditPart(){

			//In Run mode, clicking the Display or container should de-select all widgets.
			@Override
			public DragTracker getDragTracker(Request req) {
				return new DragEditPartsTracker(this);
			}

			@Override
			public boolean isSelectable() {
				return false;
			}
		};
		viewer.createControl(parent);
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.RUN_MODE));

		//viewer.addDropTargetListener(new ProcessVariableNameTransferDropPVTargetListener(viewer));
		//viewer.addDropTargetListener(new TextTransferDropPVTargetListener(viewer));

		//this will make viewer as a selection provider
		EditDomain editDomain = new DefaultEditDomain(this){

		@Override
		public void loadDefaultTool() {
			setActiveTool(new RuntimePatchedSelectionTool());
		}

		};
		editDomain.addViewer(viewer);

		//connect the CSS menu
		ContextMenuProvider cmProvider =
			new OPIRunnerContextMenuProvider(viewer);
		viewer.setContextMenu(cmProvider);

		getSite().registerContextMenu(cmProvider, viewer);

		viewer.setContents(displayModel);

		setPartName(displayModel.getName());
		// configure zoom actions
		final ZoomManager zm = root.getZoomManager();

		if (zm != null) {
		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		zm.setZoomLevelContributions(zoomLevels);

		zm.setZoomLevels(createZoomLevels());

		IAction zoomIn = new ZoomInAction(zm);
		IAction zoomOut = new ZoomOutAction(zm);
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		}

		/* scroll-wheel zoom */
		viewer.setProperty(
				MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);

		//auto zoom
		if(displayModel.isAutoZoomToFitAll()){
			viewer.getControl().addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					Point size = ((FigureCanvas)e.getSource()).getSize();
					if(size.x * size.y > 0)
						zm.setZoomAsText(ZoomManager.FIT_ALL);
				}
			});
		}
	}

	/**
	 * Create a double array that contains the pre-defined zoom levels.
	 *
	 * @return A double array that contains the pre-defined zoom levels.
	 */
	private double[] createZoomLevels() {
		List<Double> zoomLevelList = new ArrayList<Double>();

		double level = 0.1;
		while (level < 1.0) {
			zoomLevelList.add(level);
			level = level + 0.1;
		}

		zoomLevelList.add(1.1);
		zoomLevelList.add(1.2);
		zoomLevelList.add(1.3);
		zoomLevelList.add(1.5);
		zoomLevelList.add(2.0);
		zoomLevelList.add(2.5);
		zoomLevelList.add(3.0);
		zoomLevelList.add(3.5);
		zoomLevelList.add(4.0);
		zoomLevelList.add(4.5);
		zoomLevelList.add(5.0);

		double[] result = new double[zoomLevelList.size()];
		for (int i = 0; i < zoomLevelList.size(); i++) {
			result[i] = zoomLevelList.get(i);
		}

		return result;
	}

	/**
	 * Lazily creates and returns the action registry.
	 * @return the action registry
	 */
	protected ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
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
		if (adapter == GraphicalViewer.class)
			return viewer;
		if(adapter == ActionRegistry.class)
			return getActionRegistry();
		if (adapter == CommandStack.class)
			return viewer.getEditDomain().getCommandStack();
		if (adapter == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) viewer
				.getRootEditPart()).getZoomManager();
		return super.getAdapter(adapter);
	}
}
