package org.csstudio.opibuilder.runmode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.email.EMailSender;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.actions.SendEMailAction;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editor.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.ContextMenuProvider;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PartStack;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The delegate to run an OPI in an editor or view.
 * 
 * @author Xihui Chen
 * 
 */
@SuppressWarnings("restriction")
public class OPIRuntimeDelegate implements IAdaptable{

	private DisplayModel displayModel;

	private boolean displayModelFilled;

	private DisplayOpenManager displayOpenManager;

	private PatchedScrollingGraphicalViewer viewer;

	private ActionRegistry actionRegistry;
	
	private IEditorInput editorInput;
	
	private static int displayID = 0;

	/**
	 * The workbench part where the OPI is running on.
	 */	
	private IOPIRuntime opiRuntime;
	
	
	private PaintListener errorMessagePaintListener = new PaintListener() {

		public void paintControl(PaintEvent e) {
			e.gc.setForeground(CustomMediaFactory.getInstance().getColor(255,
					0, 0));
			e.gc.drawString("Failed to load opi " + getEditorInput(), 0, 0);
		}

	};

	private PaintListener loadingMessagePaintListener = new PaintListener() {

		public void paintControl(PaintEvent e) {
			e.gc.setForeground(CustomMediaFactory.getInstance().getColor(255,
					0, 0));
			e.gc.drawString("Loading...", 0, 0);
		}

	};
	
	private ControlListener zoomListener = new ControlAdapter() {
		@Override
		public void controlResized(ControlEvent e) {
			Point size = ((FigureCanvas) e.getSource()).getSize();
			if (size.x * size.y > 0)
				zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
		}
	};

	private ZoomManager zoomManager;
	
	public OPIRuntimeDelegate(IOPIRuntime opiRuntime){
		this.opiRuntime = opiRuntime;		
	}
	
	public void init(final IWorkbenchPartSite site, final IEditorInput input) throws PartInitException{
		displayID++;
		setEditorInput(input);
		if (viewer != null) {
			viewer.getControl().removePaintListener(errorMessagePaintListener);
			viewer.getControl().removeControlListener(zoomListener);
		}
		displayModel = new DisplayModel();
		displayModel.setDisplayID(displayID);
		displayModelFilled = false;
		InputStream inputStream = null;
		try {
			if (input instanceof IRunnerInput) {
				if(ResourceUtil.isURL(((IRunnerInput)input).getPath().toString())){
					final Display display = site.getShell().getDisplay();
					fillDisplayModelInJob(input, display, site);
				}else{
					inputStream = ((IRunnerInput)input).getInputStream();
				}				 
				displayOpenManager = ((IRunnerInput) input)
						.getDisplayOpenManager();
			} else
				inputStream = ResourceUtil.getInputStreamFromEditorInput(input);

			if (inputStream != null){
				XMLUtil.fillDisplayModelFromInputStream(inputStream,
						displayModel);
				displayModelFilled = true;
				if (input instanceof IRunnerInput) {
					addRunnerInputMacros(input);
				}
			}

		} catch (Exception e) {
			ErrorHandlerUtil.handleError("Failed to run opi " + input, e, true, true);
			throw new PartInitException("Failed to run OPI file: " + input, e);
		}
		
		displayModel.setOpiFilePath(getOPIFilePath());

		
		// if it was an opened editor
		if (viewer != null && displayModelFilled) {			
			viewer.setContents(displayModel);
			updateEditorTitle();
			displayModel.setViewer(viewer);
			hookZoomListener();
		}

		getActionRegistry().registerAction(new PrintDisplayAction(opiRuntime));

		if (SendToElogAction.isElogAvailable())
			getActionRegistry().registerAction(new SendToElogAction(opiRuntime));
		if (EMailSender.isEmailSupported())
			getActionRegistry().registerAction(new SendEMailAction(opiRuntime));

		// hide close button
		hideCloseButton(site);
		
		
	}
	
	public void createGUI(Composite parent) {
		viewer = new PatchedScrollingGraphicalViewer();

		ScalableFreeformRootEditPart root = new PatchedScalableFreeformRootEditPart() {

			// In Run mode, clicking the Display or container should de-select
			// all widgets.
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
		viewer.setEditPartFactory(new WidgetEditPartFactory(
				ExecutionMode.RUN_MODE));

		// viewer.addDropTargetListener(new
		// ProcessVariableNameTransferDropPVTargetListener(viewer));
		// viewer.addDropTargetListener(new
		// TextTransferDropPVTargetListener(viewer));
		//Add drag listener will make click feel stagnant.
		//viewer.addDragSourceListener(new DragPVSourceListener(viewer));
		// this will make viewer as a selection provider
		EditDomain editDomain = new EditDomain() {

			@Override
			public void loadDefaultTool() {
				setActiveTool(new RuntimePatchedSelectionTool());
			}

		};
		editDomain.addViewer(viewer);

		// connect the CSS menu
		ContextMenuProvider cmProvider = new OPIRunnerContextMenuProvider(
				viewer, opiRuntime);
		viewer.setContextMenu(cmProvider);

		opiRuntime.getSite().registerContextMenu(cmProvider, viewer);		
		if(displayModelFilled){
			viewer.setContents(displayModel);
			displayModel.setViewer(viewer);
			updateEditorTitle();
			hookZoomListener();
		}

		
		zoomManager = root.getZoomManager();

		if (zoomManager != null) {
			List<String> zoomLevels = new ArrayList<String>(3);
			zoomLevels.add(ZoomManager.FIT_ALL);
			zoomLevels.add(ZoomManager.FIT_WIDTH);
			zoomLevels.add(ZoomManager.FIT_HEIGHT);
			zoomManager.setZoomLevelContributions(zoomLevels);

			zoomManager.setZoomLevels(createZoomLevels());

//			IAction zoomIn = new ZoomInAction(zoomManager);
//			IAction zoomOut = new ZoomOutAction(zoomManager);
//			getActionRegistry().registerAction(zoomIn);
//			getActionRegistry().registerAction(zoomOut);
		}

		/* scroll-wheel zoom */
		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);

		
	}

	private void hookZoomListener() {
		// auto zoom
		if (displayModel.isAutoZoomToFitAll()) {
			viewer.getControl().addControlListener(zoomListener);
		}
	}

	
	
	private void updateEditorTitle() {
		if (displayModel.getName() != null
				&& displayModel.getName().trim().length() > 0)
			opiRuntime.setWorkbenchPartName(displayModel.getName());
		else
			opiRuntime.setWorkbenchPartName(getEditorInput().getName());
	}
	
	public IPath getOPIFilePath() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {

			return ((FileEditorInput) editorInput).getFile().getFullPath();

		} else if (editorInput instanceof FileStoreEditorInput) {
			return URIUtil
					.toPath(((FileStoreEditorInput) editorInput).getURI());
		} else if (editorInput instanceof IRunnerInput)
			return ((IRunnerInput) editorInput).getPath();
		return null;
	}
	
	
	private void hideCloseButton(final IWorkbenchPartSite site) {
		if (!displayModel.isShowCloseButton()) {
			Display.getCurrent().asyncExec(new Runnable() {

				public void run() {
					
					PartPane currentEditorPartPane = ((PartSite) site)
							.getPane();
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
	
	
	
	public void setEditorInput(IEditorInput editorInput) {
		this.editorInput = editorInput;
	}
	
	public IEditorInput getEditorInput() {
		return editorInput;
	}
	
	public DisplayModel getDisplayModel() {
		return displayModel;
	}
	
	/**
	 * Lazily creates and returns the action registry.
	 * 
	 * @return the action registry
	 */
	protected ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
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

	
	private void addRunnerInputMacros(final IEditorInput input) {
		MacrosInput macrosInput = ((IRunnerInput) input).getMacrosInput();
		if (macrosInput != null) {
			macrosInput = macrosInput.getCopy();
			macrosInput.getMacrosMap().putAll(
					displayModel.getMacrosInput().getMacrosMap());
			displayModel.setPropertyValue(
					AbstractContainerModel.PROP_MACROS, macrosInput);
		}
	}
	
	private void fillDisplayModelInJob(final IEditorInput input,
			final Display display, final IWorkbenchPartSite site) {
		Job job = new Job("Loading OPI...") {						
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + input,
						IProgressMonitor.UNKNOWN);
				try {
					display.asyncExec(new Runnable() {
						public void run() {
							if(viewer != null){
								viewer.getControl().addPaintListener(loadingMessagePaintListener);	
								viewer.getControl().redraw();
							}
						}
					});
					
					final InputStream stream = ((IRunnerInput) input)
							.getInputStream();
					display.asyncExec(new Runnable() {

								public void run() {
									try {
										if(viewer != null){
											viewer.getControl().removePaintListener(loadingMessagePaintListener);									
										}													
										XMLUtil.fillDisplayModelFromInputStream(
												stream, displayModel);	
										displayModelFilled = true;
										addRunnerInputMacros(input);													
										if(viewer != null){
											viewer.setContents(displayModel);
											displayModel.setViewer(viewer);
											hookZoomListener();
										}
										updateEditorTitle();
										hideCloseButton(site);
									} catch (Exception e) {
										ErrorHandlerUtil.handleError(
												"Failed to load widget from " + input, e,
												true, true);
									}
								}
							});

				} catch (final Exception e) {								
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (viewer != null && viewer.getControl() !=null) {											
								viewer.getControl().removePaintListener(loadingMessagePaintListener);
								viewer.getControl().addPaintListener(errorMessagePaintListener);
								viewer.getControl().redraw();
							}
							ErrorHandlerUtil.handleError(
									"Failed to connect to " + input, e,
									true, true);
						}
					});

				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == DisplayOpenManager.class) {
			if (displayOpenManager == null)
				displayOpenManager = new DisplayOpenManager(opiRuntime);
			return displayOpenManager;
		}
		if (adapter == GraphicalViewer.class)
			return viewer;
		if (adapter == ActionRegistry.class)
			return getActionRegistry();
		if (adapter == CommandStack.class)
			return viewer.getEditDomain().getCommandStack();
		if (adapter == ZoomManager.class)
			return ((ScalableFreeformRootEditPart) viewer.getRootEditPart())
					.getZoomManager();
		return null;
	}
	

}
