/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.Draw2dSingletonUtil;
import org.csstudio.ui.util.SWTConstants;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;

/**The editpart for the root display.
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 */
public class DisplayEditpart extends AbstractContainerEditpart {

    private ControlListener zoomListener, scaleListener;

    private org.eclipse.swt.graphics.Point originSize, oldSize;

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();

        // disallows the removal of this edit part from its parent
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new RootComponentEditPolicy());
        removeEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE);
    }

    @Override
    public void activate() {
        super.activate();
        initProperties();

        if(getExecutionMode() == ExecutionMode.RUN_MODE &&
                getWidgetModel().getDisplayScaleData().isAutoScaleWidgets()){
            originSize = new org.eclipse.swt.graphics.Point(
                    getWidgetModel().getWidth(), getWidgetModel().getHeight());
            oldSize=originSize;
            scaleListener = new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    if(getViewer() == null || getViewer().getControl().isDisposed())
                        return;
                    org.eclipse.swt.graphics.Point size =
                            getViewer().getControl().getSize();
                    if(size.equals(oldSize))
                        return;
                    //In RAP, each revalidate will enlarge the shell by 1000, see
                    //org.eclipse.rwt.internal.textsize.TextSizeRecalculation.enlargeShell(Shell shell)
                    if(OPIBuilderPlugin.isRAP() && (size.x - oldSize.x) == 1000 && (size.y - oldSize.y) == 1000)
                        return;
                    double widthRatio = size.x /(double)originSize.x;
                    double heightRatio = size.y/(double)originSize.y;
                    oldSize = size;
                    getWidgetModel().scale(widthRatio, heightRatio);
//                    oldSize = size;
                    //make sure the display figure repaint everything especially for RAP.
                    if(OPIBuilderPlugin.isRAP())
                        UIBundlingThread.getInstance().addRunnable(new Runnable() {

                            @Override
                            public void run() {
                                getFigure().revalidate();
                                getFigure().repaint();

                            }
                        });
                }
            };
            UIBundlingThread.getInstance().addRunnable(new Runnable() {

                @Override
                public void run() {
                    scaleListener.controlResized(null);
                }
            });
            getViewer().getControl().addControlListener(scaleListener);
        }

        if(getExecutionMode() == ExecutionMode.RUN_MODE && getWidgetModel().isAutoZoomToFitAll()){
            originSize = new org.eclipse.swt.graphics.Point(
                    getWidgetModel().getWidth(), getWidgetModel().getHeight());
            oldSize=originSize;
            zoomListener = new ControlAdapter() {
                    @Override
                    public void controlResized(ControlEvent e) {
                        if(!isActive() || getViewer() == null || getViewer().getControl().isDisposed())
                            return;
                        org.eclipse.swt.graphics.Point size =
                                ((Canvas)getViewer().getControl()).getSize();
                        if(size.equals(oldSize))
                            return;
                        //In RAP, each revalidate will enlarge the shell by 1000, see
                        //org.eclipse.rwt.internal.textsize.TextSizeRecalculation.enlargeShell(Shell shell)
                        if(OPIBuilderPlugin.isRAP() && (size.x - oldSize.x) == 1000 && (size.y - oldSize.y) == 1000)
                            return;
                        if (size.x * size.y > 0)
                            ((ScalableFreeformRootEditPart)getRoot()).getZoomManager().setZoomAsText(
                                    Draw2dSingletonUtil.ZoomManager_FIT_ALL);
                        oldSize = size;

                    }
                };
            UIBundlingThread.getInstance().addRunnable(new Runnable() {

                @Override
                public void run() {
                    zoomListener.controlResized(null);
                }
            });
            getViewer().getControl().addControlListener(zoomListener);
        }
        UIBundlingThread.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                if(getViewer()!=null && getViewer().getControl() != null && !getViewer().getControl().isDisposed())
                    getViewer().getControl().forceFocus();
            }
        });

    }

    @Override
    public void deactivate() {
        final Control control = getViewer().getControl();
        if (getViewer() != null && !control.isDisposed()) {
            //This needs to be executed in UI Thread
            final ZoomManager zoomManager = ((ScalableFreeformRootEditPart) getRoot()).getZoomManager();
            control.getDisplay().asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (zoomListener != null && !control.isDisposed()) {
                        // recover zoom
                        zoomManager.setZoom(1.0);
                        control.removeControlListener(zoomListener);
                    }

                    if (scaleListener != null && !control.isDisposed()) {
                        control.removeControlListener(scaleListener);
                    }
                }
            });

        }
        super.deactivate();

    }

    @Override
    public DisplayModel getWidgetModel() {
        return (DisplayModel) super.getWidgetModel();
    }

    private void initProperties() {
        for(String prop_id : getWidgetModel().getAllPropertyIDs()){
            getWidgetModel().getProperty(prop_id).firePropertyChange(null,
                    getWidgetModel().getPropertyValue(prop_id));
        }
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler backColorHandler = new IWidgetPropertyChangeHandler(){
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                figure.setBackgroundColor(((OPIColor)newValue).getSWTColor());
                getViewer().getControl().setBackground(
                        CustomMediaFactory.getInstance().getColor(((OPIColor)newValue).getRGBValue()));
                return false;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND, backColorHandler);

        //grid
        if(getExecutionMode() == ExecutionMode.EDIT_MODE){
            IWidgetPropertyChangeHandler gridColorHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    ((ScalableFreeformRootEditPart)getViewer().getRootEditPart())
                    .getLayer(LayerConstants.GRID_LAYER).setForegroundColor(CustomMediaFactory.getInstance()
                            .getColor(((OPIColor)newValue).getRGBValue()));
                    return false;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_COLOR_FOREGROUND, gridColorHandler);

            IWidgetPropertyChangeHandler gridSpaceHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    getViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
                            new Dimension((Integer)newValue, (Integer)newValue));
                    return false;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_GRID_SPACE, gridSpaceHandler);

            IWidgetPropertyChangeHandler showGridHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    getViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, (Boolean)newValue);
                    getViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, (Boolean)newValue);
                    return false;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_SHOW_GRID, showGridHandler);

            IWidgetPropertyChangeHandler showRulerHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    getViewer().setProperty(
                            RulerProvider.PROPERTY_RULER_VISIBILITY, (Boolean)newValue);
                    return false;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_SHOW_RULER, showRulerHandler);

            IWidgetPropertyChangeHandler snapGeoHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    getViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, (Boolean)newValue);
                    return false;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_SNAP_GEOMETRY, snapGeoHandler);

            IWidgetPropertyChangeHandler showEditRangeHandler = new IWidgetPropertyChangeHandler(){
                public boolean handleChange(Object oldValue, Object newValue,
                        IFigure figure) {
                    figure.repaint();
                    return true;
                }
            };
            setPropertyChangeHandler(DisplayModel.PROP_SHOW_EDIT_RANGE, showEditRangeHandler);
        }
    }

    @Override
    protected IFigure doCreateFigure() {

        Figure f = new FreeformLayer(){
            @Override
            protected void paintFigure(Graphics graphics) {
                super.paintFigure(graphics);
                if(OPIBuilderPlugin.isRAP()){
                    if(!getBackgroundColor().getRGB().equals(CustomMediaFactory.COLOR_WHITE)){
                        graphics.setBackgroundColor(getBackgroundColor());
                        graphics.fillRectangle(getBounds());
                    }
                }
                if(getExecutionMode() == ExecutionMode.EDIT_MODE &&
                        ((DisplayModel)getModel()).isShowEditRange()){
                    graphics.pushState();
                    graphics.setLineStyle(SWTConstants.LINE_DASH);
                    graphics.setForegroundColor(ColorConstants.black);
                    graphics.drawRectangle(
                            new Rectangle(new Point(0, 0), getWidgetModel().getSize()));
                    graphics.popState();
                }
            }
        };
//        f.setBorder(new MarginBorder(3));
        f.setLayoutManager(new FreeformLayout());

        return f;
    }

    @Override
    protected synchronized void doRefreshVisuals(IFigure refreshableFigure) {
        super.doRefreshVisuals(refreshableFigure);
        figure.repaint();
    }



    @Override
    public EditPart getWidget(String name) throws Exception {
        try {
            return super.getWidget(name);
        } catch (Exception e) {
            //search from connection widgets
            for(ConnectionModel conn : getWidgetModel().getConnectionList()){
                if(conn.getName().equals(name))
                    return (EditPart) getViewer().getEditPartRegistry().get(conn);
            }
            throw e;
        }
    }

}
