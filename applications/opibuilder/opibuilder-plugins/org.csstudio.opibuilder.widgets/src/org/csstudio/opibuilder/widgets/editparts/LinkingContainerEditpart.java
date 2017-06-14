/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.csstudio.opibuilder.editparts.AbstractLinkingContainerEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.GeometryUtil;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.swt.widgets.figures.LinkingContainerFigure;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.ui.IActionFilter;

/**The Editpart Controller for a linking Container
 * @author Xihui Chen
 *
 */
public class LinkingContainerEditpart extends AbstractLinkingContainerEditpart{

    private static AtomicInteger linkingContainerID = new AtomicInteger();
    private static Logger log = Logger.getLogger(LinkingContainerEditpart.class.getName());

    private List<ConnectionModel> connectionList;
    private Map<ConnectionModel, PointList> originalPoints;
    private Point cropTranslation;

    @Override
    protected IFigure doCreateFigure() {
        LinkingContainerFigure f = new LinkingContainerFigure();
        f.setZoomToFitAll(getWidgetModel().isAutoFit());
        f.getZoomManager().addZoomListener(new ZoomListener() {

            @Override
            public void zoomChanged(double arg0) {
                if (getViewer() == null || getViewer().getControl() == null) {
                    //depending on the OPI and the current zoom value, the event
                    //can happen before the parent is set.
                    return;
                }
                getViewer().getControl().getDisplay().asyncExec(() -> updateConnectionList());

            }
        });
        return f;
    }

    @Override
    public void setParent(EditPart parent) {
        super.setParent(parent);
        updateConnectionList();
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
        installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
    }

    @Override
    public synchronized LinkingContainerModel getWidgetModel() {
        return (LinkingContainerModel)getModel();
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if(newValue != null && newValue instanceof IPath){
                    LinkingContainerModel widgetModel = getWidgetModel();
                    IPath absolutePath = (IPath)newValue;
                    if(!absolutePath.isAbsolute())
                        absolutePath = ResourceUtil.buildAbsolutePath(
                                getWidgetModel(), absolutePath);
                    if(oldValue != null && oldValue instanceof IPath){
                        widgetModel.setDisplayModel(null);
                    }else{
                        DisplayModel displayModel = new DisplayModel(resolveMacros(absolutePath));
                        if (widgetModel.getMacroMap().equals(displayModel.getMacroMap())) {
                            widgetModel.setDisplayModel(displayModel);
                        } else {
                            widgetModel.setDisplayModel(null);
                        }
                    }
                    configureDisplayModel();
                }
                return true;
            }
        };

        setPropertyChangeHandler(LinkingContainerModel.PROP_OPI_FILE, handler);

        //load from group
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                //loadWidgets(getWidgetModel(),true);
                configureDisplayModel();
                return false;
            }
        };

        setPropertyChangeHandler(LinkingContainerModel.PROP_GROUP_NAME, handler);

        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                if((int)newValue == LinkingContainerModel.ResizeBehaviour.SIZE_OPI_TO_CONTAINER.ordinal()) {
                    ((LinkingContainerFigure)figure).setZoomToFitAll(true);
                } else {
                    ((LinkingContainerFigure)figure).setZoomToFitAll(false);
                }
                ((LinkingContainerFigure)figure).updateZoom();

                if((int)newValue == LinkingContainerModel.ResizeBehaviour.SIZE_CONTAINER_TO_OPI.ordinal()) {
                    performAutosize();
                }
                return false;
            }
        };
        setPropertyChangeHandler(LinkingContainerModel.PROP_RESIZE_BEHAVIOUR, handler);
        //loadWidgets(getWidgetModel(),true);
        configureDisplayModel();
    }

    private static synchronized Integer getLinkingContainerID() {
        return linkingContainerID.incrementAndGet();
    }

    /**
     * Automatically set the container size according its children's geography size.
     */

    @Override
    public void performAutosize() {
        Rectangle childrenRange = GeometryUtil.getChildrenRange(this);

        if (connectionList != null) {
            for (ConnectionModel connModel : connectionList) {
                final PointList connectionPoints = connModel.getPoints();
                childrenRange.union(connectionPoints.getBounds());
            }
        }

        cropTranslation = new Point(-childrenRange.x, -childrenRange.y);

        getWidgetModel().setSize(new Dimension(
                childrenRange.width + figure.getInsets().left + figure.getInsets().right,
                childrenRange.height + figure.getInsets().top + figure.getInsets().bottom));

        for (Object editPart : getChildren()) {
            AbstractWidgetModel widget = ((AbstractBaseEditPart)editPart).getWidgetModel();
            widget.setLocation(widget.getLocation().translate(cropTranslation));
        }
    }

    /**
     * Replace all macros in the name of the given path and construct a new path from the resolved name.
     *
     * @param original the original path to resolve
     * @return the path with all macros substituted with real values
     */
    private IPath resolveMacros(IPath original) {
        String path = original.toString();
        path = OPIBuilderMacroUtil.replaceMacros(getWidgetModel(), path);
        return ResourceUtil.getPathFromString(path);
    }


    /**
     * @param path the path of the OPI file
     */
    private synchronized void configureDisplayModel() {
        //This need to be executed after GUI created.
        if(getWidgetModel().getDisplayModel() == null) {
            IPath path = resolveMacros(getWidgetModel().getOPIFilePath());
            log.info(path.toString());

            final DisplayModel tempDisplayModel = new DisplayModel(path);
            getWidgetModel().setDisplayModel(tempDisplayModel);
            try {
                if (! path.isEmpty())
                    XMLUtil.fillDisplayModelFromInputStream(
                            ResourceUtil.pathToInputStream(path), tempDisplayModel,
                            getViewer().getControl().getDisplay());
            } catch (Exception e) {
                OPIBuilderPlugin.getLogger().log(Level.WARNING, "Could not reload the linking container.",e);
            }
        }

        LinkingContainerModel widgetModel = getWidgetModel();
        DisplayModel displayModel = widgetModel.getDisplayModel();
        widgetModel.setDisplayModelViewer((GraphicalViewer) getViewer());
        widgetModel.setDisplayModelDisplayID(widgetModel.getRootDisplayModel(false).getDisplayID());

        UIBundlingThread.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                LinkingContainerModel widgetModel = getWidgetModel();
                widgetModel.setDisplayModelExecutionMode(getExecutionMode());
                widgetModel.setDisplayModelOpiRuntime(widgetModel.getRootDisplayModel(false).getOpiRuntime());
            }
        });

        updateConnectionListForLinkedOpi(displayModel);
        if (originalPoints != null && !originalPoints.isEmpty()) {
            //update connections after the figure is repainted.
            getViewer().getControl().getDisplay().asyncExec(() -> updateConnectionList());
        }

        UIBundlingThread.getInstance().addRunnable(() -> {
            layout();
            if(//getExecutionMode() == ExecutionMode.RUN_MODE &&
                    !getWidgetModel().isAutoFit() && !getWidgetModel().isAutoSize()){
                Rectangle childrenRange = GeometryUtil.getChildrenRange(LinkingContainerEditpart.this);
                getWidgetModel().setChildrenGeoSize(new Dimension(
                    childrenRange.width + childrenRange.x + figure.getInsets().left + figure.getInsets().right-1,
                    childrenRange.height +childrenRange.y+ figure.getInsets().top + figure.getInsets().bottom-1));
                getWidgetModel().scaleChildren();
            }
            ((LinkingContainerFigure)getFigure()).setShowScrollBars(getWidgetModel().isShowScrollBars());
            ((LinkingContainerFigure)getFigure()).setZoomToFitAll(getWidgetModel().isAutoFit());
            ((LinkingContainerFigure)getFigure()).updateZoom();
        });

        //Add scripts on display model
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            widgetModel
                    .getScriptsInput()
                    .getScriptList()
                    .addAll(widgetModel.getDisplayModel().getScriptsInput()
                            .getScriptList());
        }
        // tempDisplayModel.removeAllChildren();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        AbstractContainerModel loadTarget = displayModel;

        if(!widgetModel.getGroupName().trim().equals("")){ //$NON-NLS-1$
            AbstractWidgetModel group =
                displayModel.getChildByName(widgetModel.getGroupName());
            if(group != null && group instanceof AbstractContainerModel){
                loadTarget = (AbstractContainerModel) group;
            }
        }

        // Load "LCID" macro whose value is unique to this instance of Linking Container.
        if (widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE) {
            map.put("LCID", "LCID_" + getLinkingContainerID());
        }
        //Load system macro
        if(displayModel.getMacrosInput().isInclude_parent_macros()){
            map.putAll(
                    displayModel.getParentMacroMap());
        }
        //Load macro from its macrosInput
        map.putAll(displayModel.getMacrosInput().getMacrosMap());
        //It also include the macros on this linking container
        //which includes the macros from action and global macros if included
        //It will replace the old one too.
        map.putAll(widgetModel.getMacroMap());

        widgetModel.setMacroMap(map);

        widgetModel.removeAllChildren();
        widgetModel.addChildren(loadTarget.getChildren(), true);
        widgetModel.setDisplayModel(displayModel);

        DisplayModel parentDisplay = widgetModel.getRootDisplayModel();
        parentDisplay.syncConnections();
        DisplayModel parentDisplay2 = widgetModel.getRootDisplayModel(false);
        if (parentDisplay != parentDisplay2)
            parentDisplay2.syncConnections();

        if(getWidgetModel().isAutoSize()){
            performAutosize();
        }
    }

    private void updateConnectionList() {
        if (connectionList==null || originalPoints==null)
            return;
        double scaleFactor = ((LinkingContainerFigure) getFigure()).getZoomManager().getZoom();
        final Point tranlateSize = getRelativeToRoot();
        tranlateSize.scale(scaleFactor);
        log.log(Level.FINEST, String.format("Relative to root translation (scaled by %s): %s ", scaleFactor, tranlateSize));

        Point scaledCropTranslation = new Point();
        if (cropTranslation != null)
            scaledCropTranslation = cropTranslation.getCopy();
        scaledCropTranslation.scale(scaleFactor);

        for (ConnectionModel conn : connectionList) {
            PointList points = originalPoints.get(conn).getCopy();
            if (points == null)
                continue;

            log.log(Level.FINER, "Connector: " + conn.getName());
            for (int i = 0; i < points.size(); i++) {
                Point point = points.getPoint(i);
                if (getWidgetModel().isAutoSize()) {
                    point.translate(scaledCropTranslation);
                    // If translated connection falls outside the bounding box,
                    // then we move the connection to the edge of the bounding
                    // box
                    if (point.x() <= tranlateSize.x())
                        point.translate(conn.getLineWidth() / 2, 0);
                    if (point.y() <= tranlateSize.y())
                        point.translate(0, conn.getLineWidth() / 2);
                }
                point.scale(scaleFactor);
                points.setPoint(point, i);
            }
            conn.setPoints(points);
        }
    }

    /**
     * This method transforms the point to be absolute to the root Figure including max figure edge in path.
     * @param origin the origin {@link Point} in this Figure's relative coordinates.
     * @return The {@link Point} translate to the relative coordinates according to the root Figure.
     */
    private Point getRelativeToRoot() {
        Point cumulativeOffset = new Point(0, 0);
        IFigure parent = getFigure();
        while (parent.getParent() != null) {
            Point inherited = new Point(parent.getBounds().x, parent.getBounds().y);
            parent.translateToRelative(inherited);
            cumulativeOffset.translate(inherited);
            parent = parent.getParent();
        }
        return cumulativeOffset;
    }

    private void updateConnectionListForLinkedOpi(DisplayModel displayModel) {
        connectionList = displayModel.getConnectionList();
        if(!connectionList.isEmpty()){
            if(originalPoints != null)
                originalPoints.clear();
            else
                originalPoints = new HashMap<ConnectionModel, PointList>();
        }

        for (ConnectionModel conn : connectionList) {
            conn.setLoadedFromLinkedOpi(true);
            if(conn.getPoints()!=null)
                originalPoints.put(conn, conn.getPoints().getCopy());
            conn.setScrollPane(((LinkingContainerFigure)getFigure()).getScrollPane());
        }
    }

    /**
     * {@inheritDoc} Overidden, to set the selection behaviour of child
     * EditParts.
     */
    @Override
    protected final EditPart createChild(final Object model) {
        EditPart result = super.createChild(model);

        // setup selection behavior for the new child
        if (getExecutionMode() == ExecutionMode.EDIT_MODE &&
                result instanceof AbstractBaseEditPart) {
            ((AbstractBaseEditPart) result).setSelectable(false);
        }

        return result;
    }

    @Override
    public IFigure getContentPane() {
        return ((LinkingContainerFigure)getFigure()).getContentPane();
    }

     @Override
    public void layout() {
        AbstractLayoutEditpart layoutter = getLayoutWidget();
        if(layoutter != null && layoutter.getWidgetModel().isEnabled()){
            List<AbstractWidgetModel> modelChildren = new ArrayList<AbstractWidgetModel>();
            for(Object child : getChildren()){
                if(child instanceof AbstractBaseEditPart &&
                        !(child instanceof AbstractLayoutEditpart)){
                    modelChildren.add(((AbstractBaseEditPart)child).getWidgetModel());
                }
            }
            layoutter.layout(modelChildren, getFigure().getClientArea());
        }
    }

    @Override
    protected synchronized void doRefreshVisuals(IFigure refreshableFigure) {
        super.doRefreshVisuals(refreshableFigure);
        //update connections after the figure is repainted.
        getViewer().getControl().getDisplay().asyncExec(() ->updateConnectionList());

    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (adapter == IActionFilter.class)
            return new BaseEditPartActionFilter(){
            @Override
            public boolean testAttribute(Object target, String name,
                    String value) {
                if (name.equals("allowAutoSize") && value.equals("TRUE")) //$NON-NLS-1$ //$NON-NLS-2$
                    return getExecutionMode()==ExecutionMode.EDIT_MODE;
                return super.testAttribute(target, name, value);
            }
        };
        return super.getAdapter(adapter);
    }

    @Override
    public ScrollPane getScrollPane() {
        return ((LinkingContainerFigure)getFigure()).getScrollPane();
    }

}
