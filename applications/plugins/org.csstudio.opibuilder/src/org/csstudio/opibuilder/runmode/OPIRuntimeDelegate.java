package org.csstudio.opibuilder.runmode;

import java.io.InputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.RefreshOPIAction;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PartStack;

/**
 * The delegate to run an OPI in an editor or view.
 * 
 * @author Xihui Chen
 * @author Takashi Nakamoto @ Cosylab (Enhanced to calculate frame rate)
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
	
	

	private ZoomManager zoomManager;
	
	public OPIRuntimeDelegate(IOPIRuntime opiRuntime){
		this.opiRuntime = opiRuntime;		
	}
	
	public void init(final IWorkbenchPartSite site, final IEditorInput input) throws PartInitException{
		displayID++;
		setEditorInput(input);
		if (viewer != null) {
			SingleSourceHelper.removePaintListener(viewer.getControl(),errorMessagePaintListener);
		}
		displayModel = new DisplayModel();
		displayModel.setOpiFilePath(getOPIFilePath());
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
			if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
				OPIBuilderPlugin.getLogger().log(Level.WARNING,	
						"Failed to open OPI file: " + input+ "\n" + e.getMessage()); //$NON-NLS-2$
			else
				ErrorHandlerUtil.handleError("Failed to open opi file: " + input, e, true, true);
			throw new PartInitException("Failed to run OPI file: " + input, e);
		}
		
		

		
		// if it was an opened editor
		if (viewer != null && displayModelFilled) {			
			viewer.setContents(displayModel);
			updateEditorTitle();
			displayModel.setViewer(viewer);
			displayModel.setOpiRuntime(opiRuntime);
		}
		
		getActionRegistry().registerAction(new RefreshOPIAction(opiRuntime));
		SingleSourceHelper.registerRCPRuntimeActions(getActionRegistry(), opiRuntime);
		

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
			displayModel.setOpiRuntime(opiRuntime);
			updateEditorTitle();
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

		/*
		 * When Figure instance which corresponds to RootEditPart is updated,
		 * calculate the frame rate and set the measured rate to "frame_rate"
		 * property of the corresponding DisplayModel instance.
		 * 
		 * By default, org.eclipse.draw2d.DeferredUpdateManager is used. This update
		 * manager queues update requests from figures and others, and it repaints
		 * requested figures at once when GUI thread is ready to repaint. notifyPainting()
		 * method of UpdateLister is called when it repaints. The frame rate is
		 * calculated based on the timing of notifyPainting().
		 *
		 * Note that the update manager repaints only requested figures. It does not 
		 * repaint all figures at once. For example, if there are only two widgets
		 * in one display, these widgets might be repainted alternately. In that case,
		 * the frame rate indicates the inverse of the time between the repainting of one
		 * widget and the repainting of the other widget, which is different from our
		 * intuition. Thus, you have to be careful about the meaning of "frame rate"
		 * calculated by the following code.
		 */
		if (displayModelFilled && displayModel.isFreshRateEnabled()){
			UpdateManager updateManager = root.getFigure().getUpdateManager();
			updateManager.addUpdateListener(new UpdateListener() {

				private long updateCycle = -1; // in milliseconds
				private Date previousDate = null;

				@Override
				public void notifyPainting(Rectangle damage,
						@SuppressWarnings("rawtypes") Map dirtyRegions) {
					Date currentDate = new Date();

					if (previousDate == null) {
						previousDate = currentDate;
						return;
					}

					synchronized (previousDate) {
						updateCycle = currentDate.getTime() - previousDate.getTime();
						displayModel.setFrameRate(1000.0 / updateCycle);
						previousDate = currentDate;
					}
				}

				@Override
				public void notifyValidating() {
					// Do nothing
				}
			});
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
		return ResourceUtil.getPathInEditor(editorInput);
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
		while (level <=0.9) {
			zoomLevelList.add(level);
			level = level + 0.1;
		}
		zoomLevelList.add(1.0);
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
								SingleSourceHelper.addPaintListener(
										viewer.getControl(),loadingMessagePaintListener);	
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
											SingleSourceHelper.removePaintListener(
													viewer.getControl(), loadingMessagePaintListener);									
										}													
										XMLUtil.fillDisplayModelFromInputStream(
												stream, displayModel);	
										displayModelFilled = true;
										addRunnerInputMacros(input);													
										if(viewer != null){
											viewer.setContents(displayModel);
											displayModel.setViewer(viewer);
											displayModel.setOpiRuntime(opiRuntime);
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
					display.asyncExec(new Runnable() {
						public void run() {
							if (viewer != null && viewer.getControl() !=null) {											
								SingleSourceHelper.removePaintListener(
										viewer.getControl(), loadingMessagePaintListener);				
								SingleSourceHelper.addPaintListener(viewer.getControl(),
										errorMessagePaintListener);
								viewer.getControl().redraw();
							}
							if(OPIBuilderPlugin.isRAP()){
								String message = 
										"Failed to open OPI file: " + input+ "\n" + //$NON-NLS-2$
										"Please check if the file exists."
										+ "\n" + e.getMessage(); //$NON-NLS-1$
								OPIBuilderPlugin.getLogger().log(Level.WARNING,	message);
								MessageDialog.openError(null, "Open File Error",message);		
							}
							else
								ErrorHandlerUtil.handleError("Failed to open opi file: " + input, e, true, true);
							
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
