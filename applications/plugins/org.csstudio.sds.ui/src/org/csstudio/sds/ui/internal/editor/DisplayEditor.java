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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.LayerSupport;
import org.csstudio.sds.internal.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.internal.persistence.IDisplayModelLoadListener;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.internal.preferences.CategorizationType;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.IPropertyChangeListener;
import org.csstudio.sds.model.PropertyChangeAdapter;
import org.csstudio.sds.model.RulerModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.actions.ArrangeAction;
import org.csstudio.sds.ui.internal.actions.CopyWidgetsAction;
import org.csstudio.sds.ui.internal.actions.CreateGroupAction;
import org.csstudio.sds.ui.internal.actions.CutWidgetsAction;
import org.csstudio.sds.ui.internal.actions.DeleteWidgetsAction;
import org.csstudio.sds.ui.internal.actions.MoveToBackAction;
import org.csstudio.sds.ui.internal.actions.MoveToFrontAction;
import org.csstudio.sds.ui.internal.actions.PasteWidgetsAction;
import org.csstudio.sds.ui.internal.actions.RemoveGroupAction;
import org.csstudio.sds.ui.internal.actions.StepBackAction;
import org.csstudio.sds.ui.internal.actions.StepFrontAction;
import org.csstudio.sds.ui.internal.commands.AssociableCommandListener;
import org.csstudio.sds.ui.internal.editor.dnd.ProcessVariableAddressDropTargetListener;
import org.csstudio.sds.ui.internal.editor.dnd.ProcessVariableDropTargetListener;
import org.csstudio.sds.ui.internal.editor.dnd.ProcessVariablesDropTargetListener;
import org.csstudio.sds.ui.internal.editor.dnd.TextTransferDropTargetListener;
import org.csstudio.sds.ui.internal.editor.outline.ThumbnailViewOutlinePage;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.csstudio.sds.ui.internal.layers.ILayerManager;
import org.csstudio.sds.ui.internal.properties.view.IPropertySheetPage;
import org.csstudio.sds.ui.internal.properties.view.PropertySheetPage;
import org.csstudio.sds.ui.internal.properties.view.UndoablePropertySheetEntry;
import org.csstudio.sds.ui.internal.viewer.PatchedGraphicalViewer;
import org.csstudio.sds.util.SaveAsDialog;
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
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
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
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The editor for synoptic displays.
 * 
 * @author Sven Wende & Alexander Will
 * @version $Revision: 1.120 $
 * 
 */
public final class DisplayEditor extends GraphicalEditorWithFlyoutPalette implements
        ITabbedPropertySheetPageContributor {

	private static final Logger LOG = LoggerFactory.getLogger(DisplayEditor.class);

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
    
    private boolean isModelLoaded;
    private final List<IDisplayModelLoadListener> modelLoadedListeners;
    
	/**
     * A DisplayListener.
     */
    private Map<String, IPropertyChangeListener> _propertyChangeListeners;
    
    /**
     * The preference page listener for the grid space.
     */
    private org.eclipse.jface.util.IPropertyChangeListener _gridSpacingListener;
    
    /**
     * The palette root.
     */
    private PaletteRoot _paletteRoot;
    
    private TabbedPropertySheetPage propertyPage;
    
    /**
     * The RulerComposite for the GraphicalViewer.
     */
    private RulerComposite _rulerComposite;
    
    private IContentOutlinePage _outlinePage;
    
    private static KeyListenerAdapter keyAdapter = new KeyListenerAdapter();
    
    /**
     * Constructor.
     */
    public DisplayEditor() {
        setEditDomain(new DefaultEditDomain(this));
        
        initPropertyChangeListeners();
        initCommandStackListeners();
        
        isModelLoaded = false;
        modelLoadedListeners = new ArrayList<IDisplayModelLoadListener>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PaletteRoot getPaletteRoot() {
        // if (_paletteRoot == null) {
        _paletteRoot = new PaletteRoot();
        // _paletteRoot.add(createControlGroup(_paletteRoot));
        createControlGroup(_paletteRoot);
        // }
        return _paletteRoot;
    }
    
    /**
     * Returns the currently selected {@link AbstractBaseEditPart}s.
     * 
     * @return a list of {@link AbstractBaseEditPart}s
     */
    public List<AbstractBaseEditPart> getSelectedEditParts() {
        List<AbstractBaseEditPart> result = new ArrayList<AbstractBaseEditPart>(getGraphicalViewer()
                .getSelectedEditParts().size());
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
    private static void createControlGroup(final PaletteRoot root) {
        PaletteGroup controlGroup = new PaletteGroup("controlGroup"); //$NON-NLS-1$
        
        ToolEntry toolEntry = new PanningSelectionToolEntry();
        controlGroup.add(toolEntry);
        root.add(controlGroup);
        
        WidgetModelFactoryService service = WidgetModelFactoryService.getInstance();
        
        PaletteEntryCreator paletteEntryCreator = new PaletteEntryCreator(service, keyAdapter);
        
        String string = SdsPlugin.getDefault().getPluginPreferences()
                .getString(PreferenceConstants.PROP_WIDGET_CATEGORIZATION);
        
        CategorizationType categorization = CategorizationType.getTypeForId(string);
        paletteEntryCreator.createEntries(root, categorization);
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
    public Point getEditorCenterPosition() {
        ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer()
                .getRootEditPart();
        ZoomManager zoomManager = root.getZoomManager();
        
        Point center = zoomManager.getViewport().getBounds().getCenter();
        center = translateCursorLocation(center);
        
        return center;
    }
    
    /**
     * Translates a point on the editor viewport to the actual point on the display.
     * 
     * Doesn't manipulate <code>cursorLocation</code>, but returns a new translated point.
     * 
     * @param cursorLocation The position on the editor viewport.
     * @return Point The point, on which the given location points on the display.  
     */
    public Point translateCursorLocation(Point cursorLocation) {
        ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) getGraphicalViewer()
                .getRootEditPart();
        ZoomManager zoomManager = root.getZoomManager();
        Point viewportLocation = root.getZoomManager().getViewport().getViewLocation();
        
        Point locationCopy = cursorLocation.getCopy();
        locationCopy.translate(viewportLocation);
        zoomManager.getScalableFigure().translateFromParent(locationCopy);
        
        return locationCopy;
    }
    
    /**
     * {@inheritDoc}
     * 
     * We overide the behaviour of the superclass totally. Don´t add a super
     * call in future!!
     */
    @Override
    protected void createGraphicalViewer(final Composite parent) {
        _rulerComposite = new RulerComposite(parent, SWT.NONE);
        
        GraphicalViewer viewer = new PatchedGraphicalViewer();
        viewer.createControl(_rulerComposite);
        setGraphicalViewer(viewer);
        configureGraphicalViewer();
        hookGraphicalViewer();
        initializeGraphicalViewer();
        
        _rulerComposite.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
        WorkbenchHelpSystem.getInstance().setHelp(_rulerComposite,
                                                  SdsUiPlugin.PLUGIN_ID
                                                          + ".synoptic_display_studio");
        
        viewer.getControl().addKeyListener(keyAdapter);
    }
    
    /**
     * Returns the main composite of the editor.
     * 
     * @return the main composite of the editor
     */
    public Composite getParentComposite() {
        return _rulerComposite;
    }
    
    /**
     * Adds a listener that is called when the model has been loaded. 
     * The Listener is called once and automatically removed afterwards
     */
    public void addModelLoadedListener(IDisplayModelLoadListener displayModelLoadListener) {
		assert displayModelLoadListener != null : "Precondition failed: displayModelLoadListener != null";
		
		if(isModelLoaded) {
			// Fire listener directly if model is already loaded
			displayModelLoadListener.onDisplayModelLoaded();
		}
		else {
			modelLoadedListeners.add(displayModelLoadListener);
		}
    }
    
    /**
     * Configure the properties for the rulers.
     */
    private void configureRuler() {
        // Ruler properties
        RulerProvider hprovider = new SDSRulerProvider(new RulerModel(true));
        RulerProvider vprovider = new SDSRulerProvider(new RulerModel(false));
        getGraphicalViewer().setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, hprovider);
        getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, vprovider);
        getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, Boolean.FALSE);
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
        // register all property change listeners
        for (String propertyId : _propertyChangeListeners.keySet()) {
            WidgetProperty property = _displayModel.getPropertyInternal(propertyId);
            
            if (property != null) {
                property.addPropertyChangeListener(_propertyChangeListeners.get(propertyId));
            }
        }
        
        // FIXME: 2008-07-24: Sven Wende: Entfernen, sobald Grid-Einstellungen
        // mit Model persistiert werden
        _gridSpacingListener = new GridSpacingListener();
        SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(_gridSpacingListener);
    }
    
    /**
     * Removes the listener for the model.
     */
    private void removeModelListeners() {
        // remove all property change listeners
        // register all property change listeners
        for (String propertyId : _propertyChangeListeners.keySet()) {
            WidgetProperty property = _displayModel.getPropertyInternal(propertyId);
            
            if (property != null) {
                property.removePropertyChangeListener(_propertyChangeListeners.get(propertyId));
            }
        }
        
        // FIXME: 2008-07-24: Sven Wende: Entfernen, sobald Grid-Einstellungen
        // mit Model persistiert werden
        SdsUiPlugin.getCorePreferenceStore().removePropertyChangeListener(_gridSpacingListener);
        
    }
    
    /**
     * Creates all property change listeners that should be registered to the
     * loaded display.
     */
    private void initPropertyChangeListeners() {
        _propertyChangeListeners = new HashMap<String, IPropertyChangeListener>();
        
        // ... background color
        IPropertyChangeListener listener = new PropertyChangeAdapter() {
            @Override
            public void propertyValueChanged(final Object oldValue, final Object newValue) {
                if (newValue instanceof String) {
                    new CheckedUiRunnable() {
                        
                        @Override
                        protected void doRunInUi() {
                            getGraphicalViewer().getControl().setBackground(SdsUiPlugin
                                    .getDefault().getColorAndFontService()
                                    .getColor((String) newValue));
                        }
                    };
                }
            }
        };
        
        _propertyChangeListeners.put(AbstractWidgetModel.PROP_COLOR_BACKGROUND, listener);
        
        // ... grid visibility
        listener = new PropertyChangeAdapter() {
            @Override
            public void propertyValueChanged(final Object oldValue, final Object newValue) {
                final boolean visible = (Boolean) newValue;
                
                new CheckedUiRunnable() {
                    @Override
                    protected void doRunInUi() {
                        // update viewer settings
                        getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, visible);
                        getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, visible);
                        
                        // update toolbar action state
                        getActionRegistry().getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY)
                                .setChecked(visible);
                    }
                };
            }
        };
        
        _propertyChangeListeners.put(DisplayModel.PROP_GRID_ON, listener);
        
        // ... ruler visibility
        listener = new PropertyChangeAdapter() {
            @Override
            public void propertyValueChanged(final Object oldValue, final Object newValue) {
                final boolean visible = (Boolean) newValue;
                
                new CheckedUiRunnable() {
                    @Override
                    protected void doRunInUi() {
                        // update viewer settings
                        getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY,
                                                         visible);
                        
                        // update toolbar action state
                        getActionRegistry().getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY)
                                .setChecked(visible);
                    }
                };
            }
        };
        
        _propertyChangeListeners.put(DisplayModel.PROP_RULER_ON, listener);
        
        // ... snap to geometry active
        listener = new PropertyChangeAdapter() {
            @Override
            public void propertyValueChanged(final Object oldValue, final Object newValue) {
                final boolean active = (Boolean) newValue;
                
                new CheckedUiRunnable() {
                    @Override
                    protected void doRunInUi() {
                        // update viewer settings
                        getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
                                                         active);
                        
                        // update toolbar action state
                        getActionRegistry().getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY)
                                .setChecked(active);
                    }
                };
            }
        };
        _propertyChangeListeners.put(DisplayModel.PROP_GEOMETRY_ON, listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeGraphicalViewer() {
        prepareModel();
        
        final GraphicalViewer viewer = getGraphicalViewer();
        
        // initialize context menu
        ContextMenuProvider cmProvider = new DisplayContextMenuProvider(viewer, getActionRegistry());
        viewer.setContextMenu(cmProvider);
        getSite().registerContextMenu(cmProvider, viewer);
        
        // load the model
        loadModelAsynchroniously();
        
        // initialize drop support (order matters!)
        viewer.addDropTargetListener(new ProcessVariablesDropTargetListener(viewer));
        viewer.addDropTargetListener(new ProcessVariableDropTargetListener(viewer));
        viewer.addDropTargetListener(new TextTransferDropTargetListener(viewer));
        viewer.addDropTargetListener(new ProcessVariableAddressDropTargetListener(viewer));
        viewer.addDropTargetListener(new LibraryElementDropTargetListener(viewer));
        
    }
    
    private void initCommandStackListeners() {
        getCommandStack()
                .addCommandStackEventListener(new AssociableCommandListener(getCommandStack()));
    }
    
    /**
     * Loads the DisplayModel asynchronously.
     */
    private void loadModelAsynchroniously() {
        getGraphicalViewer().setContents(_displayModel);
        
        final InputStream inputStream = getInputStream();
        
        IDisplayModelLoadListener modelLoadListener = new DisplayModelLoadAdapter() {
            @Override
            public void onDisplayModelLoaded() {
                new CheckedUiRunnable() {
                    @Override
                    protected void doRunInUi() {
                        GraphicalViewer viewer = getGraphicalViewer();
                        
                        // setup grid and ruler states on the graphical editor
                        viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
                                           _displayModel.getGridState());
                        viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
                                           _displayModel.getGridState());
                        
                        viewer.setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY,
                                           _displayModel.getRulerState());
                        viewer.setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
                                           _displayModel.getGeometryState());
                        
                        // refresh the checked state on the according editor
                        // actions
                        IAction action;
                        
                        action = getActionRegistry()
                                .getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY);
                        action.setChecked(action.isChecked());
                        
                        action = getActionRegistry()
                                .getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY);
                        action.setChecked(action.isChecked());
                        
                        action = getActionRegistry()
                                .getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY);
                        action.setChecked(action.isChecked());
                        
                        isModelLoaded = true;
                        for (IDisplayModelLoadListener modelLoadListener : modelLoadedListeners) {
							modelLoadListener.onDisplayModelLoaded();
						}
                        modelLoadedListeners.clear();
                    }
                };
            }
        };
        
        if (inputStream != null) {
            setPartName(getEditorInput().getName());
            PersistenceUtil.asyncFillModel(_displayModel, inputStream, modelLoadListener);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();
        ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();
        viewer.setEditPartFactory(new WidgetEditPartFactory(ExecutionMode.EDIT_MODE));
        viewer.getControl().setBackground(ColorConstants.listBackground);
        
        final SDSRootEditPart root = new SDSRootEditPart();
        viewer.setRootEditPart(root);
        
        this.configureRuler();
        
        // configure zoom actions
        configureZoomManager(root);
        
        // FIXME: 2008-07-24: Sven Wende: Entfernen, sobald Grid-Einstellungen
        // mit Model persistiert werden
        int spacing = SdsPlugin.getDefault().getPluginPreferences()
                .getInt(PreferenceConstants.PROP_GRID_SPACING);
        getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
                                         new Dimension(spacing, spacing));
        
        // Ruler Actions
        IAction showRulers = new ToggleRulerVisibilityAction(getGraphicalViewer()) {
            @Override
            public void run() {
                super.run();
                DisplayModel displayModel = getDisplayModel();
                SetPropertyCommand cmd = new SetPropertyCommand(displayModel,
                                                                DisplayModel.PROP_RULER_ON,
                                                                isChecked());
                getCommandStack().execute(cmd);
            }
        };
        getActionRegistry().registerAction(showRulers);
        
        // Grid Action
        IAction a = new ToggleGridAction(getGraphicalViewer()) {
            @Override
            public void run() {
                super.run();
                DisplayModel displayModel = getDisplayModel();
                SetPropertyCommand cmd = new SetPropertyCommand(displayModel,
                                                                DisplayModel.PROP_GRID_ON,
                                                                isChecked());
                getCommandStack().execute(cmd);
                
            }
        };
        getActionRegistry().registerAction(a);
        
        /* snap to geometry */
        a = new ToggleSnapToGeometryAction(getGraphicalViewer()) {
            @Override
            public void run() {
                super.run();
                DisplayModel displayModel = getDisplayModel();
                SetPropertyCommand cmd = new SetPropertyCommand(displayModel,
                                                                DisplayModel.PROP_GEOMETRY_ON,
                                                                isChecked());
                getCommandStack().execute(cmd);
            }
        };
        getActionRegistry().registerAction(a);
        
        hookGraphicalViewer();
        
        viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
        
    }
    
    @SuppressWarnings("unchecked")
    private void configureZoomManager(final SDSRootEditPart rootEditPart) {
        SDSZoomManager zm = rootEditPart.getZoomManager();
        
        List<String> zoomLevels = new ArrayList<String>(3);
        zoomLevels.add(ZoomManager.FIT_ALL);
        zoomLevels.add(ZoomManager.FIT_WIDTH);
        zoomLevels.add(ZoomManager.FIT_HEIGHT);
        zm.setZoomLevelContributions(zoomLevels);
        
        zm.setZoomLevels(createZoomLevels());
        
        if (zm != null) {
            IAction zoomIn = new ZoomInAction(zm);
            getSelectionActions().add(zoomIn.getId());
            getActionRegistry().registerAction(zoomIn);
            IAction zoomOut = new ZoomOutAction(zm);
            getActionRegistry().registerAction(zoomOut);
        }
        
        zm.addZoomFinishedListener(new ZoomListener() {
            
            @Override
            public void zoomChanged(double zoom) {
                List<AbstractBaseEditPart> selectedEditParts = getSelectedEditParts();
                if (!selectedEditParts.isEmpty()) {
                    getGraphicalViewer()
                            .reveal(selectedEditParts.get(selectedEditParts.size() - 1));
                }
            }
        });
        
        /* scroll-wheel zoom */
        getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
                                         MouseWheelZoomHandler.SINGLETON);
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
        
        action = new CreateGroupAction(this, getGraphicalViewer());
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new RemoveGroupAction(this, getGraphicalViewer());
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new DeleteWidgetsAction((IWorkbenchPart) this);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
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
        
        action = new CopyWidgetsAction(this);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        keyBindingService.registerAction(action);
        
        action = new CutWidgetsAction(this);
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
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new ArrangeAction(this,
                                   getGraphicalViewer(),
                                   getCommandStack(),
                                   Arrange.HORIZONTAL);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
        
        action = new ArrangeAction(this, getGraphicalViewer(), getCommandStack(), Arrange.VERTICAL);
        registry.registerAction(action);
        getSelectionActions().add(action.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void commandStackChanged(final EventObject event) {
        firePropertyChange(IEditorPart.PROP_DIRTY);
        
        if (propertyPage != null && !propertyPage.getControl().isDisposed()) {
            // .. update the property page (this is necessary to allow dynamic
            // visibility changes of properties)
            List<EditPart> selectedEditParts = getGraphicalViewer().getSelectedEditParts();
            if (selectedEditParts.isEmpty()) {
                EditPart focusEditPart = getGraphicalViewer().getFocusEditPart();
                selectedEditParts = new ArrayList<EditPart>(1);
                selectedEditParts.add(focusEditPart);
            }
            
            propertyPage.selectionChanged(this, new StructuredSelection(selectedEditParts) {
                @Override
                public boolean equals(Object o) {
                    return false;
                }
                
                @Override
                public int hashCode() {
                    return UUID.randomUUID().hashCode();
                }
            });
        }
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
                IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetPath);
                
                if (!targetFile.exists()) {
                    targetFile.create(null, true, null);
                }
                
                FileEditorInput editorInput = new FileEditorInput(targetFile);
                
                setInput(editorInput);
                
                setPartName(targetFile.getName());
                
                performSave();
            }
        } catch (CoreException e) {
            MessageDialog.openError(getSite().getShell(), "IO Error", e.getMessage());
            LOG.error(e.toString());
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
                        .openInformation(getSite().getShell(),
                                         "Information",
                                         "The displayed model isn't completeley loaded, yet. It can not be saved until this operation is completed.");
            } else {
                // getInputStream().setContents(is, false, false, null);
                if (getEditorInput() instanceof FileEditorInput) {
                    InputStream is = PersistenceUtil.createStream(_displayModel);
                    ((FileEditorInput) getEditorInput()).getFile().setContents(is,
                                                                               false,
                                                                               false,
                                                                               null);
                } else if (getEditorInput() instanceof FileStoreEditorInput) {
                    File file = URIUtil.toPath(((FileStoreEditorInput) getEditorInput()).getURI())
                            .toFile();
                    String content = PersistenceUtil.createString(_displayModel);
                    try {
                        FileWriter fileWriter = new FileWriter(file, false);
                        BufferedWriter writer = new BufferedWriter(fileWriter);
                        writer.write(content);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        MessageDialog.openError(getSite().getShell(), "IO Error", e.getMessage());
                        LOG.error(e.toString());
                    }
                }
                
                getCommandStack().flush();
                
                firePropertyChange(IEditorPart.PROP_DIRTY);
            }
        } catch (CoreException e) {
            MessageDialog.openError(getSite().getShell(), "IO Error", e.getMessage());
            LOG.error(e.toString());
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
                result = ((FileEditorInput) editorInput).getFile().getContents();
            } catch (CoreException e) {
                e.printStackTrace();
            }
        } else if (editorInput instanceof FileStoreEditorInput) {
            IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput).getURI());
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
            result = ((FileEditorInput) editorInput).getFile().getFullPath();
        } else if (editorInput instanceof FileStoreEditorInput) {
            result = URIUtil.toPath(((FileStoreEditorInput) editorInput).getURI());
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
        
        if (adapter == org.eclipse.ui.views.properties.IPropertySheetPage.class) {
            propertyPage = new TabbedPropertySheetPage(this);
            return propertyPage;
        } else if (adapter == IPropertySheetPage.class) {
            PropertySheetPage page = new PropertySheetPage();
            page.setRootEntry(new UndoablePropertySheetEntry(getCommandStack()));
            return page;
        } else if (adapter == ZoomManager.class) {
            return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart())
                    .getZoomManager();
        } else if (adapter == ILayerManager.class) {
            return new ILayerManager() {
                public LayerSupport getLayerSupport() {
                    return getDisplayModel().getLayerSupport();
                }
                
                public CommandStack getCommandStack() {
                    return DisplayEditor.this.getCommandStack();
                }
            };
        } else if (adapter == IContentOutlinePage.class) {
            if (_outlinePage == null) {
                _outlinePage = new ThumbnailViewOutlinePage(getGraphicalViewer());
            }
            
            return _outlinePage;
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
        super.dispose();
    }
    
    final class GridSpacingListener implements org.eclipse.jface.util.IPropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent event) {
            if (event.getProperty().equals(PreferenceConstants.PROP_GRID_SPACING)) {
                IEditorReference[] references = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage().getEditorReferences();
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
    
    public String getContributorId() {
        return getSite().getId();
    }
    
    @Override
    public GraphicalViewer getGraphicalViewer() {
        return super.getGraphicalViewer();
    }
    
    @Override
    public CommandStack getCommandStack() {
        return super.getCommandStack();
    }
}
