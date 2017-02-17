/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.opibuilder.datadefinition.DisplayScaleData;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComplexDataProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.VersionProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalViewer;
import org.osgi.framework.Version;

/**
 * The root model for an OPI Display.
 *
 * @author Alexander Will, Sven Wende, Kai Meyer (class of same name in SDS)
 * @author Xihui Chen
 * @author Takashi Nakamoto @ Cosylab (Property for frame rate)
 *
 */
public class DisplayModel extends AbstractContainerModel {

    /**
     * The type ID of this model.
     */
    public static final String ID = "org.csstudio.opibuilder.Display"; //$NON-NLS-1$

    /**
     * Space of grid in pixels.
     */
    public static final String PROP_GRID_SPACE = "grid_space"; //$NON-NLS-1$
    /**
     * If the grid should be visible.
     */
    public static final String PROP_SHOW_GRID = "show_grid"; //$NON-NLS-1$
    /**
     * If the ruler should be visible.
     */
    public static final String PROP_SHOW_RULER = "show_ruler"; //$NON-NLS-1$
    /**
     * If the moving widgets will be snapped to the geometry of other widgets.
     */
    public static final String PROP_SNAP_GEOMETRY = "snap_to_geometry"; //$NON-NLS-1$
    /**
     * If the dash boundary line of the display should be visible.
     */
    public static final String PROP_SHOW_EDIT_RANGE = "show_edit_range"; //$NON-NLS-1$

    /**
     * If the tab close button should be hidden.
     */
    public static final String PROP_SHOW_CLOSE_BUTTON = "show_close_button"; //$NON-NLS-1$

    public static final String PROP_BOY_VERSION = "boy_version"; //$NON-NLS-1$

    /**
     * Auto scale all the widgets as the window resizes. If this is set to true,
     * zoom operation will not work.
     */
    public static final String PROP_AUTO_ZOOM_TO_FIT_ALL = "auto_zoom_to_fit_all"; //$NON-NLS-1$

    /**
     * Automatically scale all widgets when window resizes.
     */
    public static final String PROP_AUTO_SCALE_WIDGETS = "auto_scale_widgets"; //$NON-NLS-1$

    /**
     * Frame rate in Hz.
     * This is the hidden property which can be referred only from scripts.
     * The value is valid only when running mode. In edit mode, it is always -1.
     */
    public static final String PROP_FRAME_RATE = "frame_rate"; //$NON-NLS-1$

    public static final Point NULL_LOCATION = new Point(-1, -1);

    private GraphicalViewer viewer;

    private IOPIRuntime opiRuntime;

    private IPath opiFilePath;

    private boolean FreshRateEnabled= false;

    private int displayID;

    private DisplayModel parentDisplayModel;
    
    private static AtomicInteger displayIDCounter = new AtomicInteger(0);

    /**
     * Create a Display Model which is the root model for an OPI.
     * Only use this constructor if this model doesn't relate to
     * any real opi file. Otherwise, please use {@link #DisplayModel(IPath)}.
     */
    public DisplayModel(){
        this(null);
    }

    /**Create a Display Model which is the root model for an OPI.
     * @param opiFilePath path of the OPI. It can only be null if this model doesn't relate to
     * any real opi file.
     */
    public DisplayModel(IPath opiFilePath) {
        super();
        setLocation(NULL_LOCATION);
        setSize(800, 600);
        setOpiFilePath(opiFilePath);
        displayID = displayIDCounter.incrementAndGet();
    }

    @Override
    protected void configureProperties() {
        addProperty(new IntegerProperty(PROP_GRID_SPACE, "Grid Space",
                WidgetPropertyCategory.Display, 6, 1, 1000));
        addProperty(new BooleanProperty(PROP_SHOW_GRID, "Show Grid",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_RULER, "Show Ruler",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SNAP_GEOMETRY, "Snap to Geometry",
                WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_EDIT_RANGE,
                "Show Edit Range", WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_AUTO_ZOOM_TO_FIT_ALL,
                "Auto Zoom to Fit All", WidgetPropertyCategory.Behavior, false));
        addProperty(new ComplexDataProperty(
                PROP_AUTO_SCALE_WIDGETS, "Auto Scale Widgets (at Runtime)",
                WidgetPropertyCategory.Behavior, new DisplayScaleData(this), "Scale Widgets as windows resizes"));

        addProperty(new BooleanProperty(PROP_SHOW_CLOSE_BUTTON,
                "Show Close Button", WidgetPropertyCategory.Display, true));
        Version version = new Version(0, 0, 0);
        addProperty(new VersionProperty(PROP_BOY_VERSION, "BOY Version",
                WidgetPropertyCategory.Basic, version.toString()));

        addProperty(new DoubleProperty(PROP_FRAME_RATE, "Frame Rate",
                WidgetPropertyCategory.Display, -1.0));

        setPropertyVisible(PROP_BORDER_COLOR, false);
        setPropertyVisible(PROP_BORDER_STYLE, false);
        setPropertyVisible(PROP_BORDER_WIDTH, false);
        setPropertyVisible(PROP_VISIBLE, false);
        setPropertyVisible(PROP_ENABLED, false);
        setPropertyVisible(PROP_TOOLTIP, false);
        setPropertyVisible(PROP_ACTIONS, false);
        setPropertyVisible(PROP_FONT, false);
        setPropertyVisibleAndSavable(PROP_FRAME_RATE, false, false);
        setPropertyVisibleAndSavable(PROP_BOY_VERSION, false, true);
        addProperty(new ActionsProperty(PROP_ACTIONS, "Actions",
                WidgetPropertyCategory.Behavior, false));
        setPropertyDescription(PROP_COLOR_FOREGROUND, "Grid Color");
        setPropertyValue(PROP_NAME, ""); //$NON-NLS-1$
        removeProperty(PROP_SCALE_OPTIONS);

    }

    /**
     * @return true if Children should be auto scaled when this container is resized.
     */
    public DisplayScaleData getDisplayScaleData(){
        return (DisplayScaleData)getPropertyValue(PROP_AUTO_SCALE_WIDGETS);
    }

    public boolean isShowGrid() {
        return (Boolean) getCastedPropertyValue(PROP_SHOW_GRID);
    }

    public boolean isShowRuler() {
        return (Boolean) getCastedPropertyValue(PROP_SHOW_RULER);
    }

    public boolean isSnapToGeometry() {
        return (Boolean) getCastedPropertyValue(PROP_SNAP_GEOMETRY);
    }

    public boolean isShowEditRange() {
        return (Boolean) getCastedPropertyValue(PROP_SHOW_EDIT_RANGE);
    }

    public boolean isShowCloseButton() {
        return (Boolean) getPropertyValue(PROP_SHOW_CLOSE_BUTTON);
    }

    public boolean isAutoZoomToFitAll() {
        return (Boolean) getPropertyValue(PROP_AUTO_ZOOM_TO_FIT_ALL);
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * @param opiFilePath
     *            the opiFilePath to set
     */
    public void setOpiFilePath(IPath opiFilePath) {
        this.opiFilePath = opiFilePath;
    }

    /**Set the {@link IOPIRuntime} on this display if it is in run mode.
     * @param opiRuntime
     */
    public void setOpiRuntime(IOPIRuntime opiRuntime) {
        this.opiRuntime = opiRuntime;
    }

    /**
     * @return the {@link IOPIRuntime} if it is in run mode.
     */
    public IOPIRuntime getOpiRuntime() {
        return opiRuntime;
    }

    /**
     * @return the opiFilePath
     */
    public IPath getOpiFilePath() {
        return opiFilePath;
    }

    /**
     * Set the viewer of the display model if this model belongs to a viewer.
     *
     * @param viewer
     *            the viewer to set
     */
    public void setViewer(GraphicalViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * @return the viewer, null if it has no viewer.
     */
    public GraphicalViewer getViewer() {
        return viewer;
    }

    /**
     * @param displayID
     *            the unique displayID to set
     */
    public void setDisplayID(int displayID) {
        this.displayID = displayID;
    }

    /**
     * @return the displayID
     */
    public int getDisplayID() {
        return displayID;
    }

    public Version getBOYVersion() {
        return new Version((String) getPropertyValue(PROP_BOY_VERSION));
    }

    /**
     * @return the list of connections belongs to this display model.
     */
    public List<ConnectionModel> getConnectionList() {
        return getConnectionList(this);
    }

    /**
     * In connections are spanning over multiple display models (e.g. via linking containers),
     * and if one of those sub display models is reloaded, all the links will become invalid -
     * the previously existing widgets will no longer exist. This methods intends to reconnect
     * such broken connections, by resetting the connectors sources and targets.
     */
    public void syncConnections() {
        List<AbstractWidgetModel> allDescendants = getAllDescendants();
        for(AbstractWidgetModel widget : allDescendants){
            if(!widget.getSourceConnections().isEmpty()){
                for(ConnectionModel connectionModel: widget.getSourceConnections()){
                    if(!allDescendants.contains(connectionModel.getTarget())){
                        //the target model no longer exists, perhaps it was reloaded
                        connectionModel.resync();
                    }
                }
            }
            if(!widget.getTargetConnections().isEmpty()){
                for(ConnectionModel connectionModel: widget.getTargetConnections()){
                    if(!allDescendants.contains(connectionModel.getSource())){
                        connectionModel.resync();
                    }
                }
            }
        }
    }

    private List<ConnectionModel> getConnectionList(AbstractContainerModel container){
        Set<ConnectionModel> connectionModels = new HashSet<ConnectionModel>();
        List<AbstractWidgetModel> allDescendants = getAllDescendants();
        for(AbstractWidgetModel widget : allDescendants){
            if(!widget.getSourceConnections().isEmpty()){
                for(ConnectionModel connectionModel: widget.getSourceConnections()){
                    if(allDescendants.contains(connectionModel.getTarget())){
                        connectionModels.add(connectionModel);
                    }
                }
            }
            if(!widget.getTargetConnections().isEmpty()){
                for(ConnectionModel connectionModel: widget.getTargetConnections()){
                    if(allDescendants.contains(connectionModel.getSource())){
                        connectionModels.add(connectionModel);
                    }
                }
            }
        }
        return new ArrayList<>(connectionModels);
    }

    public AbstractWidgetModel getWidgetFromWUID(String wuid) {
        return getWidgetFromWUID(this, wuid);
    }

    private static AbstractWidgetModel getWidgetFromWUID(
            AbstractContainerModel container, String wuid) {
        for (AbstractWidgetModel widget : container.getChildren()) {
            if (widget.getWUID().equals(wuid))
                return widget;
            else if (widget instanceof AbstractContainerModel) {
                AbstractWidgetModel w = getWidgetFromWUID(
                        (AbstractContainerModel) widget, wuid);
                if (w != null)
                    return w;
            }
        }
        return null;
    }


    @Override
    public void scale(double widthRatio, double heightRatio) {
        int minWidth = getDisplayScaleData().getMinimumWidth();
        if(minWidth < 0){
            minWidth = getWidth();
        }
        int minHeight = getDisplayScaleData().getMinimumHeight();
        if(minHeight < 0){
            minHeight = getHeight();
        }
        if(getWidth() *widthRatio < minWidth)
            widthRatio = minWidth/(double)getWidth();
        if(getHeight()*heightRatio < minHeight)
            heightRatio = minHeight/(double)getHeight();
        for(AbstractWidgetModel child : getChildren())
            child.scale(widthRatio, heightRatio);
    }

    /**!!! This is function only for test purpose. It might be removed in future!
     * @return true if calculating fresh rate is enabled.
     */
    public boolean isFreshRateEnabled() {
        return FreshRateEnabled;
    }

    /**
     * When a new frame rate is notified by GUI toolkit side, this method
     * shall be called to set the up-to-date frame rate.
     * @param rate Frame rate in Hz
     */
    public void setFrameRate(double rate) {
        setPropertyValue(PROP_FRAME_RATE, rate);
    }

    public void setParentDisplayModel(DisplayModel rootDisplayModel) {
        this.parentDisplayModel = rootDisplayModel;
    }

    public DisplayModel getParentDisplayModel() {
        return parentDisplayModel;
    }

}
