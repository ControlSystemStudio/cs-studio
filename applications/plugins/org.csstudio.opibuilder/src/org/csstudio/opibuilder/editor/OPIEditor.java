package org.csstudio.opibuilder.editor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.csstudio.opibuilder.palette.OPIEditorPaletteFactory;
import org.csstudio.opibuilder.palette.WidgetCreationFactory;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.gef.ui.actions.MatchWidthAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;


public class OPIEditor extends GraphicalEditorWithFlyoutPalette {

	private PaletteRoot paletteRoot;
	
	/** the undoable <code>IPropertySheetPage</code> */
	private PropertySheetPage undoablePropertySheetPage;
	
	private DisplayModel displayModel;

	private RulerComposite rulerComposite;
	
	public OPIEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		rulerComposite = new RulerComposite(parent, SWT.NONE);

		GraphicalViewer viewer = new PatchedScrollingGraphicalViewer();
		viewer.createControl(rulerComposite);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();

		rulerComposite
				.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
			
	}
	
	
	@Override
	protected Control getGraphicalControl() {
		return rulerComposite;
	}
	
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.EDIT_MODE));
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart() {
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
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		ContextMenuProvider cmProvider = 
			new OPIEditorContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);		
		
		// Grid Action
		IAction action = new ToggleGridAction(getGraphicalViewer());		
		getActionRegistry().registerAction(action);
		
		// Ruler Action
		configureRuler();
		action = new ToggleRulerVisibilityAction(getGraphicalViewer());		
		getActionRegistry().registerAction(action);
		
		// Snap to Geometry Action
		IAction geometryAction = new ToggleSnapToGeometryAction(getGraphicalViewer());		
		getActionRegistry().registerAction(geometryAction);
		
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

	}
	
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		
		initDisplayModel();
		viewer.setContents(displayModel);
		
		viewer.addDropTargetListener(createTransferDropTargetListener());

	}

	/**
	 * 
	 */
	private void initDisplayModel() {
		displayModel = new DisplayModel();
		displayModel.getProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					getGraphicalViewer().getControl().setBackground(CustomMediaFactory.getInstance()
							.getColor((RGB)evt.getNewValue()));
				}
			});
		displayModel.getProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart())
					.getLayer(LayerConstants.GRID_LAYER).setForegroundColor(CustomMediaFactory.getInstance()
							.getColor((RGB)evt.getNewValue()));
				}
			});
		displayModel.getProperty(DisplayModel.PROP_GRID_SPACE).
		addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING, 
						new Dimension((Integer)evt.getNewValue(), 
								(Integer)evt.getNewValue()));
			}
		});
	}
	
	public DisplayModel getDisplayModel(){
		return displayModel;
	}
	
	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}
	
	
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener, this will enable
				// model element creation by dragging a CombinatedTemplateCreationEntries 
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}
	
	/**
	 * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry
	 * tool in the palette, this will enable model element creation by dragging from the palette.
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			@SuppressWarnings("unchecked")
			protected CreationFactory getFactory(Object template) {
				return (WidgetCreationFactory)template;
			}
		};
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		if(paletteRoot == null)
			paletteRoot = OPIEditorPaletteFactory.createPalette();
		return paletteRoot;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}
	
	/**
	* Returns the undoable <code>PropertySheetPage</code> for
	* this editor.
	*
	* @return the undoable <code>PropertySheetPage</code>
	*/
	protected PropertySheetPage getPropertySheetPage(){
		if(undoablePropertySheetPage == null){
			undoablePropertySheetPage = new PropertySheetPage();
			undoablePropertySheetPage.setRootEntry(
					new UndoablePropertySheetEntry(getCommandStack()));
		}
		return undoablePropertySheetPage;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class type) {
		if(type == IPropertySheetPage.class)
			return getPropertySheetPage();
		else if (type == ZoomManager.class)
		return ((ScalableFreeformRootEditPart) getGraphicalViewer()
				.getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
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

		String id = ActionFactory.DELETE.getId();
		action = getActionRegistry().getAction(id);
		action.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
		keyBindingService.registerAction(action);

		id = ActionFactory.SELECT_ALL.getId();
		action = getActionRegistry().getAction(id);
		action.setActionDefinitionId("org.eclipse.ui.edit.selectAll");
		keyBindingService.registerAction(action);

		id = ActionFactory.UNDO.getId();
		action = getActionRegistry().getAction(id);
		action.setActionDefinitionId("org.eclipse.ui.edit.undo");
		keyBindingService.registerAction(action);

		id = ActionFactory.REDO.getId();
		action = getActionRegistry().getAction(id);
		action.setActionDefinitionId("org.eclipse.ui.edit.redo");
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
	 * Configure the properties for the rulers.
	 */
	private void configureRuler() {
		// Ruler properties
		RulerProvider hprovider = new OPIEditorRulerProvider(new RulerModel(true));
		RulerProvider vprovider = new OPIEditorRulerProvider(new RulerModel(false));
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_HORIZONTAL_RULER, hprovider);
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_VERTICAL_RULER, vprovider);
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_RULER_VISIBILITY, Boolean.TRUE);
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
}
