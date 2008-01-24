/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.internal.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.dialogs.SaveAsDialog;
import org.csstudio.platform.util.PerformanceUtil;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.RulerModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.layers.LayerSupport;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.csstudio.sds.model.properties.IPropertyChangeListener;
import org.csstudio.sds.model.properties.PropertyChangeAdapter;
import org.csstudio.sds.preferences.PreferenceConstants;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.actions.CopyWidgetsAction;
import org.csstudio.sds.ui.internal.actions.MoveToBackAction;
import org.csstudio.sds.ui.internal.actions.MoveToFrontAction;
import org.csstudio.sds.ui.internal.actions.PasteWidgetsAction;
import org.csstudio.sds.ui.internal.actions.StepBackAction;
import org.csstudio.sds.ui.internal.actions.StepFrontAction;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.csstudio.sds.ui.internal.feedback.GraphicalFeedbackContributionsService;
import org.csstudio.sds.ui.internal.layers.ILayerManager;
import org.csstudio.sds.ui.internal.properties.view.IPropertySheetPage;
import org.csstudio.sds.ui.internal.properties.view.PropertySheetPage;
import org.csstudio.sds.ui.internal.properties.view.UndoablePropertySheetEntry;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.gef.ui.actions.MatchWidthAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The editor for synoptic displays.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision$
 * 
 */
public final class DisplayEditor extends GraphicalEditorWithFlyoutPalette
		implements IDisplayEditor {

	/**
	 * The default value for the grid spacing property.
	 */
	public static final int GRID_SPACING = 12;

	/**
	 * The file extension for SDS display files.
	 */
	public static final String SDS_FILE_EXTENSION = "css-sds";

	/**
	 */
	private DisplayModel _displayModel = new DisplayModel();

	/**
	 * A DisplayListener.
	 */
	private IPropertyChangeListener _displayListener;

	/**
	 * The preference page listener for the grid space.
	 */
	private org.eclipse.jface.util.IPropertyChangeListener _gridSpacingListener;

	/**
	 * The palette root.
	 */
	private PaletteRoot _paletteRoot;

	/**
	 * The RulerComposite for the GraphicalViewer.
	 */
	private RulerComposite _rulerComposite;

	/**
	 * Constructor.
	 */
	public DisplayEditor() {
		PerformanceUtil.getInstance().constructorCalled(this);
		setEditDomain(new DefaultEditDomain(this));
	}

	/**
	 * Creates a new IPropertyChangeListener.
	 * 
	 * @return IPropertyChangeListener The new IPropertyChangeListener
	 */
	private IPropertyChangeListener createDisplayListener() {
		IPropertyChangeListener listener = new PropertyChangeAdapter() {
			@Override
			public void propertyValueChanged(final Object oldValue,
					final Object newValue) {
				if (newValue instanceof RGB) {
					new CheckedUiRunnable() {

						@Override
						protected void doRunInUi() {
							getGraphicalViewer().getControl().setBackground(
									CustomMediaFactory.getInstance().getColor(
											(RGB) newValue));
						}
					};
				}
			}
		};
		return listener;
	}

	// /**
	// * Registers DisplayPropertyListener to the properties.
	// *
	// * @param model
	// * The DisplayModel
	// */
	// private void addDisplayPropertyListener2(final DisplayModel model) {
	// if (model == null) {
	// return;
	// }
	// model.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
	// .addPropertyChangeListener(_displayListener);
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (_paletteRoot == null) {
			_paletteRoot = new PaletteRoot();
			_paletteRoot.add(createControlGroup(_paletteRoot));
		}
		return _paletteRoot;
	}

	public List<AbstractBaseEditPart> getSelectedEditParts() {
		List<AbstractBaseEditPart> result = new ArrayList<AbstractBaseEditPart>(
				getGraphicalViewer().getSelectedEditParts().size());
		for (Object obj : getGraphicalViewer().getSelectedEditParts()) {
			if (obj instanceof AbstractBaseEditPart) {
				result.add((AbstractBaseEditPart) obj);
			}
		}
		return result;
	}

	/**
	 * Installs standard tools in the specified palette root and returns a
	 * palette container.
	 * 
	 * @param root
	 *            the palette root
	 * @return a palette container
	 */
	@SuppressWarnings("unchecked")
	private static PaletteContainer createControlGroup(final PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("controlGroup"); //$NON-NLS-1$

		List entries = new ArrayList();

		ToolEntry toolEntry = new PanningSelectionToolEntry();
		entries.add(toolEntry);
		root.setDefaultEntry(toolEntry);

		WidgetModelFactoryService service = WidgetModelFactoryService
				.getInstance();

		for (String typeId : service.getWidgetTypes()) {
			String contributingPluginId = service
					.getContributingPluginId(typeId);

			String iconPath = service.getIcon(typeId);

			ImageDescriptor icon = CustomMediaFactory.getInstance()
					.getImageDescriptorFromPlugin(contributingPluginId,
							iconPath);
			toolEntry = new CreationToolEntry(service.getName(typeId), service
					.getDescription(typeId), new WidgetCreationFactory(typeId),
					icon, icon);

			Class toolClass = GraphicalFeedbackContributionsService
					.getInstance().getGraphicalFeedbackFactory(typeId)
					.getCreationTool();

			if (toolClass != null) {
				toolEntry.setToolClass(toolClass);
			}
			entries.add(toolEntry);
		}

		controlGroup.addAll(entries);
		return controlGroup;
	}

	/**
	 * Initializes the ActionBars.
	 */
	protected void init() {
		ActionRegistry registry = getActionRegistry();
		IActionBars bars = getEditorSite().getActionBars();

		String id = ActionFactory.UNDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.REDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));

		id = ActionFactory.DELETE.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
	}

	/**
	 * Returns the Point, which is the center of the Display.
	 * 
	 * @return Point The Point, which is the center of the Display
	 */
	public Point getDisplayCenterPosition() {
		ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer()
				.getRootEditPart();

		ZoomManager m = root.getZoomManager();

		Point center = m.getViewport().getBounds().getCenter();

		Rectangle x = new Rectangle(center.x, center.y, 10, 10);

		x.translate(m.getViewport().getViewLocation());
		m.getScalableFigure().translateFromParent(x);

		Point result = x.getLocation();

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createGraphicalViewer(final Composite parent) {
		_rulerComposite = new RulerComposite(parent, SWT.NONE);
		super.createGraphicalViewer(_rulerComposite);
		_rulerComposite
				.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
	}

	public Composite getParentComposite() {
		return _rulerComposite;
	}

	/**
	 * Configure the properties for the rulers.
	 */
	private void configureRuler() {
		// Ruler properties
		RulerProvider hprovider = new SDSRulerProvider(new RulerModel(true));
		RulerProvider vprovider = new SDSRulerProvider(new RulerModel(false));
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_HORIZONTAL_RULER, hprovider);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER,
				vprovider);
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_RULER_VISIBILITY, Boolean.TRUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control getGraphicalControl() {
		return _rulerComposite;
	}

	/**
	 * Creates a new {@link DisplayModel} and adds listener to it.
	 */
	private void prepareModel() {
		_displayModel = new DisplayModel();
		addModelListeners();
	}

	/**
	 * Adds a listener for the background color and one for the grid space.
	 */
	private void addModelListeners() {
		// add a listener for the background color
		_displayListener = createDisplayListener();
		_displayModel.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
				.addPropertyChangeListener(_displayListener);

		// add a listener for changes to the grid spacing preference setting
		_gridSpacingListener = new GridSpacingListener();
		SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(
				_gridSpacingListener);
	}

	/**
	 * Removes the listener for the model.
	 */
	private void removeModelListeners() {
		// remove the background color listener
		_displayModel.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND)
				.removePropertyChangeListener(_displayListener);

		// remove the grid spacing listener
		SdsUiPlugin.getCorePreferenceStore().removePropertyChangeListener(
				_gridSpacingListener);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeGraphicalViewer() {
		prepareModel();

		final GraphicalViewer viewer = getGraphicalViewer();

		// initialize context menu
		ContextMenuProvider cmProvider = new DisplayContextMenuProvider(viewer,
				getActionRegistry());

		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);

		// initialize the connection router
		// ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart)
		// viewer
		// .getRootEditPart();

		loadModelAsynchroniously();
		// loadModelSynchroniously();

		viewer.addDropTargetListener(new EditorDropTargetListener(viewer));

		// this.refreshCustomLayer();
		this.getGraphicalViewer().getControl().setBackground(
				CustomMediaFactory.getInstance().getColor(
						_displayModel.getBackgroundColor()));
	}

	/**
	 * Loads the DisplayModel asynchronously.
	 */
	private void loadModelAsynchroniously() {
		getGraphicalViewer().setContents(_displayModel);

		final InputStream inputStream = getInputStream();

		if (inputStream != null) {
			setPartName(getEditorInput().getName());
			PersistenceUtil.asyncFillModel(_displayModel, inputStream, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();
		viewer.setEditPartFactory(new WidgetEditPartFactory(
				ExecutionMode.EDIT_MODE));
		viewer.getControl().setBackground(ColorConstants.listBackground);

		final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart() {
			/**
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Object getAdapter(final Class key) {
				if (key == AutoexposeHelper.class) {
					return new ViewportAutoexposeHelper(this);
				}
				return super.getAdapter(key);
			}
		};
		viewer.setRootEditPart(root);

		this.configureRuler();

		// configure zoom actions
		ZoomManager zm = root.getZoomManager();

		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		zm.setZoomLevelContributions(zoomLevels);

		zm.setZoomLevels(createZoomLevels());

		if (zm != null) {
			IAction zoomIn = new ZoomInAction(zm);
			IAction zoomOut = new ZoomOutAction(zm);
			getActionRegistry().registerAction(zoomIn);
			getActionRegistry().registerAction(zoomOut);
		}

		/* scroll-wheel zoom */
		getGraphicalViewer().setProperty(
				MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);

		/* grid actions */
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
				false);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				false);
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
				false);

		// TODO dynamisieren
		int spacing = SdsPlugin.getDefault().getPluginPreferences().getInt(
				PreferenceConstants.PROP_GRID_SPACING);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
				new Dimension(spacing, spacing));

		// Ruler Actions
		IAction showRulers = new ToggleRulerVisibilityAction(
				getGraphicalViewer());
		getActionRegistry().registerAction(showRulers);

		IAction a = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(a);

		/* snap to geometriy */
		a = new ToggleSnapToGeometryAction(getGraphicalViewer());
		// a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
		// SdsUiPlugin.PLUGIN_ID, "icons/snap2geometry.png"));
		getActionRegistry().registerAction(a);
		hookGraphicalViewer();

		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
	}

	/**
	 * Refreshes the GridLayer and sets the new spacing value.
	 * 
	 * @param spacing
	 *            The new value for the spacing property from the GridLayer
	 */
	public void refreshGridLayer(final int spacing) {
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
				new Dimension(spacing, spacing));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void createActions() {
		super.createActions();

		IKeyBindingService keyBindingService = getSite().getKeyBindingService();

		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new CopyTemplateAction(this);
		registry.registerAction(action);

		action = new MatchWidthAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new MatchHeightAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new DirectEditAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		/*
		 * XXX Warum wird die DirectEditAction 2x eingebunden???
		 */
		action = new DirectEditAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new CopyWidgetsAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		keyBindingService.registerAction(action);

		action = new PasteWidgetsAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		keyBindingService.registerAction(action);

		action = new MoveToFrontAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new MoveToBackAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new StepBackAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new StepFrontAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		/* delete action (registered in superclass) */

		String id = ActionFactory.DELETE.getId();
		action = getActionRegistry().getAction(id);
		action.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
		keyBindingService.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.LEFT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.RIGHT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.TOP);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.BOTTOM);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.CENTER);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart) this,
				PositionConstants.MIDDLE);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commandStackChanged(final EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		if (getEditorInput() instanceof FileEditorInput
				|| getEditorInput() instanceof FileStoreEditorInput) {
			performSave();
		} else {
			doSaveAs();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getEditorSite().getShell());
		saveAsDialog.setFileExtension(SDS_FILE_EXTENSION); //$NON-NLS-1$
		int ret = saveAsDialog.open();

		try {
			if (ret == Window.OK) {
				IPath targetPath = saveAsDialog.getResult();
				IFile targetFile = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(targetPath);

				if (!targetFile.exists()) {
					targetFile.create(null, true, null);
				}

				FileEditorInput editorInput = new FileEditorInput(targetFile);

				setInput(editorInput);

				setPartName(targetFile.getName());

				performSave();
			}
		} catch (CoreException e) {
			MessageDialog.openError(getSite().getShell(), "IO Error", e
					.getMessage());
			CentralLogger.getInstance().error(this, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Save the edited model to the underlying <code>IFile</code> resource.
	 */
	private void performSave() {
		try {
			if (_displayModel.isLoading()) {
				MessageDialog
						.openInformation(
								getSite().getShell(),
								"Information",
								"The displayed model isn't completeley loaded, yet. It can not be saved until this operation is completed.");
			} else {
				// getInputStream().setContents(is, false, false, null);
				if (getEditorInput() instanceof FileEditorInput) {
					InputStream is = PersistenceUtil
							.createStream(_displayModel);
					((FileEditorInput) getEditorInput()).getFile().setContents(
							is, false, false, null);
				} else if (getEditorInput() instanceof FileStoreEditorInput) {
					File file = URIUtil.toPath(
							((FileStoreEditorInput) getEditorInput()).getURI())
							.toFile();
					String content = PersistenceUtil
							.createString(_displayModel);
					try {
						FileWriter fileWriter = new FileWriter(file, false);
						BufferedWriter writer = new BufferedWriter(fileWriter);
						writer.write(content);
						writer.flush();
						writer.close();
					} catch (IOException e) {
						MessageDialog.openError(getSite().getShell(),
								"IO Error", e.getMessage());
						CentralLogger.getInstance().error(this, e);
					}
				}

				getCommandStack().flush();

				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		} catch (CoreException e) {
			MessageDialog.openError(getSite().getShell(), "IO Error", e
					.getMessage());
			CentralLogger.getInstance().error(this, e);
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

	/**
	 * Returns the path for this editor´s input data.
	 * 
	 * @return the path for this editor´s input data
	 */
	public IPath getFilePath() {
		IPath result = null;
		IEditorInput editorInput = getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			result = ((FileEditorInput) editorInput).getFile().getLocation();
		} else if (editorInput instanceof FileStoreEditorInput) {
			result = URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
		}

		return result;
	}

	/**
	 * Returns the name of the display.
	 * 
	 * @return String The name
	 */
	public String getDisplayName() {
		return getEditorInput().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public DisplayModel getDisplayModel() {
		return _displayModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			PropertySheetPage page = new PropertySheetPage();
			page
					.setRootEntry(new UndoablePropertySheetEntry(
							getCommandStack()));
			return page;
		} else if (adapter == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
		} else if (adapter == ILayerManager.class) {
			return new ILayerManager() {
				public LayerSupport getLayerSupport() {
					return getDisplayModel().getLayerSupport();
				}

				public CommandStack getCommandStack() {
					return DisplayEditor.this.getCommandStack();
				}
			};
		}

		return super.getAdapter(adapter);
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
			level = level + 0.05;
		}

		zoomLevelList.add(1.0);
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

	@Override
	public void dispose() {
		removeModelListeners();
		_displayModel = null;
		System.out.println("Editor Dispose");
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		PerformanceUtil.getInstance().finalizedCalled(this);
		super.finalize();
	}

	final class GridSpacingListener implements
			org.eclipse.jface.util.IPropertyChangeListener {
		public void propertyChange(final PropertyChangeEvent event) {
			if (event.getProperty().equals(
					PreferenceConstants.PROP_GRID_SPACING)) {
				IEditorReference[] references = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getEditorReferences();
				for (IEditorReference ref : references) {
					IEditorPart editor = ref.getEditor(false);
					if (editor instanceof DisplayEditor) {
						Integer spacing;
						if (event.getNewValue() instanceof String) {
							spacing = new Integer((String) event.getNewValue());
						} else {
							spacing = (Integer) event.getNewValue();
						}
						if (spacing != null) {
							((DisplayEditor) editor).refreshGridLayer(spacing);
						}
					}
				}
			}
		}
	}

}
