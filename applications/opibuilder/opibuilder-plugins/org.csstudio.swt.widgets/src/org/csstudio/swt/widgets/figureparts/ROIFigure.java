/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;

import org.csstudio.swt.widgets.figures.IntensityGraphFigure;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.GraphArea;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.ICroppedDataSizeListener;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.IROIInfoProvider;
import org.csstudio.swt.widgets.figures.IntensityGraphFigure.IROIListener;
import org.csstudio.ui.util.ColorConstants;
import org.csstudio.ui.util.Draw2dSingletonUtil;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

public class ROIFigure extends Figure {



        abstract class CommonDragger extends MouseMotionListener.Stub implements MouseListener {
            protected Point start;
            protected Rectangle startROIBounds;
            protected boolean armed;
            public void mousePressed(MouseEvent me) {
                start = me.getLocation();
                startROIBounds = roiGeoBounds.getCopy();
                me.consume();
            }

            public void mouseDoubleClicked(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
                if(armed){
                    armed = false;
                    updateROIBounds(me);
                    fireROIUpdated();
                    me.consume();
                }
            }

            protected abstract void updateROIBounds(MouseEvent me);

            @Override
            public void mouseDragged(MouseEvent me) {
                armed = true;
                if (start != null) {
                    updateROIBounds(me);
                } else {
                    start = me.getLocation();
                }
                me.consume();
            }

        }

        class ROIRectDragger extends CommonDragger {
            @Override
            public void mousePressed(MouseEvent me) {
                requestFocus();
                super.mousePressed(me);
            }

            /**
             * @param me
             */
            protected void updateROIBounds(MouseEvent me) {
                int dx = me.x - start.x;
                int dy = me.y - start.y;
                RECT_SINGLETON.setBounds(startROIBounds.x + dx, startROIBounds.y + dy,
                        startROIBounds.width, startROIBounds.height);
                setROIGeoBounds(RECT_SINGLETON.x, RECT_SINGLETON.y,
                            RECT_SINGLETON.width, RECT_SINGLETON.height);
            }

        }

        class HandlerBoundsCalulator {
                public Rectangle calcBoundsFromROIBounds(Rectangle roiBounds){
                    //the bounds for handler 0 on left top corner
                    return new Rectangle(roiBounds.x - HANDLE_SIZE/2,
                            roiBounds.y-HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
                }
        }
        class ResizeHandler extends RectangleFigure{

            private HandlerBoundsCalulator handlerBoundsCalulator;
            /**Constructor.
             * @param index index of the handler. Count from 0 on clockwise.
             */
            public ResizeHandler(int index) {
                super();
                setFill(true);
                setBackgroundColor(ColorConstants.black);
                setOutline(true);
                setForegroundColor(ColorConstants.white);
                Cursor cursor = null;
                CommonDragger dragger = null;
                switch (index) {
                case 0:
                    cursor = Cursors.SIZENW;
                    dragger = new CommonDragger() {
                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            int dy = me.y - start.y;
                            setROIGeoBounds(me.x, me.y,
                                    startROIBounds.width - dx, startROIBounds.height - dy);
                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator();
                    break;
                case 1:
                    cursor = Cursors.SIZEN;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dy = me.y - start.y;
                            setROIGeoBounds(startROIBounds.x, me.y,
                                    startROIBounds.width, startROIBounds.height - dy);                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(roiBounds.width/2, 0);
                        }
                    };
                    break;
                case 2:
                    cursor = Cursors.SIZENE;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            int dy = me.y - start.y;
                            setROIGeoBounds(startROIBounds.x, startROIBounds.y + dy,
                                    startROIBounds.width + dx, startROIBounds.height - dy);                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(roiBounds.width, 0);
                        }
                    };
                    break;
                case 3:
                    cursor = Cursors.SIZEE;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            setROIGeoBounds(startROIBounds.x, startROIBounds.y,
                                    startROIBounds.width + dx, startROIBounds.height);
                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(
                                    roiBounds.width, roiBounds.height/2);
                        }
                    };
                    break;
                case 4:
                    cursor = Cursors.SIZESE;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            int dy = me.y - start.y;
                            setROIGeoBounds(startROIBounds.x, startROIBounds.y,
                                    startROIBounds.width + dx, startROIBounds.height + dy);                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(roiBounds.width, roiBounds.height);
                        }
                    };
                    break;
                case 5:
                    cursor = Cursors.SIZES;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dy = me.y - start.y;
                            setROIGeoBounds(startROIBounds.x, startROIBounds.y,
                                    startROIBounds.width, startROIBounds.height+dy);
                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(
                                    roiBounds.width/2, roiBounds.height);
                        }
                    };
                    break;
                case 6:
                    cursor = Cursors.SIZESW;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            int dy = me.y - start.y;
                            setROIGeoBounds(startROIBounds.x +dx, startROIBounds.y,
                                    startROIBounds.width - dx, startROIBounds.height + dy);
                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(0, roiBounds.height);
                        }
                    };
                    break;
                case 7:
                    cursor = Cursors.SIZEW;
                    dragger = new CommonDragger() {

                        @Override
                        protected void updateROIBounds(MouseEvent me) {
                            int dx = me.x - start.x;
                            setROIGeoBounds(startROIBounds.x + dx, startROIBounds.y,
                                    startROIBounds.width-dx, startROIBounds.height);
                        }
                    };
                    handlerBoundsCalulator = new HandlerBoundsCalulator(){
                        @Override
                        public Rectangle calcBoundsFromROIBounds(
                                Rectangle roiBounds) {
                            return super.calcBoundsFromROIBounds(roiBounds).translate(0, roiBounds.height/2);
                        }
                    };
                    break;
                default:
                    break;
                }

                setCursor(cursor);
                addMouseListener(dragger);
                addMouseMotionListener(dragger);

            }

            public HandlerBoundsCalulator getHandlerBoundsCalulator() {
                return handlerBoundsCalulator;
            }


        }
        private static final int HANDLERS_COUNT = 8;
        protected static final int HANDLE_SIZE = 5;
        private static final Rectangle RECT_SINGLETON = new Rectangle();

        /**
         * Geometry bounds of ROI.
         */
        private PrecisionRectangle roiGeoBounds;

        /**
         * Data index bounds of ROI based on whole data array.
         */
        private Rectangle roiDataBounds;
        /**
         * The handlers on left-top and right-bottom corners.
         */
        private ResizeHandler[] resizeHandlers;

        private RectangleFigure roiRectFigure;
        private IROIListener roiListener;
        private IROIInfoProvider roiInfoProvider;
        private String name;
        private IntensityGraphFigure intensityGraphFigure;


        /**Constructor of ROI figure.
         * @param name name of the ROI. It must be unique for this graph.
         * @param color color of the ROI.
         * @param roiListener listener on ROI updates. Can be null.
         * @param roiInfoProvider provides information for the ROI. Can be null.
         */
        public ROIFigure(IntensityGraphFigure intensityGraphFigure, String name, Color color, IROIListener roiListener,
                IROIInfoProvider roiInfoProvider) {
            this.intensityGraphFigure = intensityGraphFigure;
            this.name = name;
            this.roiListener = roiListener;
            this.roiInfoProvider = roiInfoProvider;
            setToolTip(new Label(name));
            setBackgroundColor(ColorConstants.white);
            setForegroundColor(ColorConstants.black);
            roiRectFigure = new RectangleFigure(){
                public boolean containsPoint(int x, int y) {
                    if (!super.containsPoint(x, y))
                        return false;
                    return !Draw2dSingletonUtil.getRectangle().setBounds(getBounds())
                            .shrink(3, 3).contains(x, y);
                }
            };
            roiRectFigure.setCursor(Cursors.SIZEALL);
            roiRectFigure.setFill(false);
            roiRectFigure.setOutline(true);
            roiRectFigure.setForegroundColor(color);
            ROIRectDragger roiRectDragger = new ROIRectDragger();
            roiRectFigure.addMouseListener(roiRectDragger);
            roiRectFigure.addMouseMotionListener(roiRectDragger);
            setFocusTraversable(true);
            setRequestFocusEnabled(true);
            resizeHandlers = new ResizeHandler[HANDLERS_COUNT];
            add(roiRectFigure);
            for(int i=0; i<HANDLERS_COUNT; i++){
                resizeHandlers[i] = new ResizeHandler(i);
                add(resizeHandlers[i]);
            }

            addFocusListener(new FocusListener(){
                public void focusGained(FocusEvent fe) {
                    for(Figure handler : resizeHandlers){
                        handler.setVisible(true);
                    }
                }
                public void focusLost(FocusEvent fe) {
                    for(Figure handler : resizeHandlers){
                        handler.setVisible(false);
                    }
                }
            });
            intensityGraphFigure.addCroppedDataSizeListener(new ICroppedDataSizeListener() {

                public void croppedDataSizeChanged(int croppedDataWidth,
                        int croppedDataHeight) {
                    updateROIGeoBounds();
                    updateChildrenBounds();
                }
            });

        }



        @Override
        public boolean containsPoint(int x, int y) {
            x = x - getBounds().x;
            y = y - getBounds().y;
            boolean contain = false;
            for(ResizeHandler handler : resizeHandlers){
                contain = contain || handler.containsPoint(x, y);
            }
            return contain || roiRectFigure.containsPoint(x, y) ;
        }

        public String getName() {
            return name;
        }

        @Override
        protected void layout() {
            if(roiDataBounds == null){
                return;
            }
            updateROIGeoBounds();
            updateChildrenBounds();

        }

        @Override
        protected void paintFigure(Graphics graphics) {
            if(roiInfoProvider!=null && roiDataBounds!=null){
                String text = roiInfoProvider.getROIInfo(roiDataBounds.x, roiDataBounds.y,
                        roiDataBounds.width, roiDataBounds.height);
                Dimension size = Draw2dSingletonUtil.getTextUtilities().getTextExtents(text, getFont());
                graphics.pushState();
                graphics.translate(getLocation());
                graphics.fillRectangle(roiGeoBounds.x, roiGeoBounds.y - size.height, size.width, size.height);
                graphics.drawText(text, roiGeoBounds.x, roiGeoBounds.y - size.height);
                graphics.popState();
            }
            super.paintFigure(graphics);

        }

        /**
         *
         */
        protected void updateChildrenBounds() {
            roiRectFigure.setBounds(roiGeoBounds);
            for(ResizeHandler handler : resizeHandlers){
                handler.setBounds(
                        handler.getHandlerBoundsCalulator().calcBoundsFromROIBounds(roiGeoBounds));
            }
            if(roiInfoProvider!=null)
                repaint();
        }

        @Override
        protected boolean useLocalCoordinates() {
        return true;
        }

        public void setROIGeoBounds(int x, int y, int w, int h){
            if(w <=0)
                w=1;

            if(h <=0)
                h=1;

            roiGeoBounds.setBounds(x, y, w, h);
            roiDataBounds = getROIFromGeoBounds(new PrecisionRectangle(roiGeoBounds.preciseX() + getBounds().x,
                    roiGeoBounds.preciseY() + getBounds().y, roiGeoBounds.preciseWidth(), roiGeoBounds.preciseHeight()));
            if(roiDataBounds.width <1 || roiDataBounds.height <1 ){
                if(roiDataBounds.width <1)
                    roiDataBounds.width =1;
                if(roiDataBounds.height <1)
                    roiDataBounds.height =1;
                roiGeoBounds = getGeoBoundsFromROI(roiDataBounds);
            }
            updateChildrenBounds();
        }

        public void setROIDataBounds(int xIndex, int yIndex, int width, int height){
            RECT_SINGLETON.setBounds(xIndex,yIndex,width,height);
            if(RECT_SINGLETON.equals(roiDataBounds))
                return;
            if(roiDataBounds == null)
                roiDataBounds = new PrecisionRectangle();
            roiDataBounds.setBounds(xIndex, yIndex, width, height);
            updateROIGeoBounds();
            updateChildrenBounds();
        }

        public void setROIDataBoundsX(int xIndex){
            if(roiDataBounds == null){
                roiDataBounds = new PrecisionRectangle();
            }
            setROIDataBounds(xIndex, roiDataBounds.y, roiDataBounds.width, roiDataBounds.height);
        }
        public void setROIDataBoundsY(int yIndex){
            if(roiDataBounds == null){
                roiDataBounds = new PrecisionRectangle();
            }
            setROIDataBounds(roiDataBounds.x, yIndex, roiDataBounds.width, roiDataBounds.height);
        }
        public void setROIDataBoundsW(int width){
            if(roiDataBounds == null){
                roiDataBounds = new PrecisionRectangle();
            }
            setROIDataBounds(roiDataBounds.x, roiDataBounds.y, width, roiDataBounds.height);
        }
        public void setROIDataBoundsH(int height){
            if(roiDataBounds == null){
                roiDataBounds = new PrecisionRectangle();
            }
            setROIDataBounds(roiDataBounds.x, roiDataBounds.y, roiDataBounds.width, height);
        }

        private void updateROIGeoBounds(){
            roiGeoBounds = getGeoBoundsFromROI(roiDataBounds);
        }

        public void fireROIUpdated() {
            if(roiListener !=null){
                roiListener.roiUpdated(roiDataBounds.x, roiDataBounds.y, roiDataBounds.width, roiDataBounds.height);
            }
        }

        private Rectangle getROIFromGeoBounds(PrecisionRectangle roiBounds){
            PrecisionPoint lt = ((GraphArea)getParent()).getDataLocation(roiBounds.preciseX(), roiBounds.preciseY());
            PrecisionPoint rb = ((GraphArea)getParent()).getDataLocation(roiBounds.preciseX() + roiBounds.preciseWidth(),
                    roiBounds.preciseY() + roiBounds.preciseHeight());
            return new Rectangle((int)Math.round(lt.preciseX()) + intensityGraphFigure.getCropLeft(),
                    (int)Math.round(lt.preciseY()) +intensityGraphFigure.getCropTop(),
                    (int)Math.ceil(rb.preciseX() - lt.preciseX()), (int)Math.ceil(rb.preciseY() - lt.preciseY()));
        }

        private PrecisionRectangle getGeoBoundsFromROI(Rectangle roiDataBounds){
            PrecisionPoint lt = ((GraphArea)getParent()).getGeoLocation(roiDataBounds.preciseX()-intensityGraphFigure.getCropLeft(),
                    roiDataBounds.preciseY()-intensityGraphFigure.getCropTop());
            PrecisionPoint rb = ((GraphArea)getParent()).getGeoLocation(
                    roiDataBounds.preciseX() + roiDataBounds.preciseWidth() -intensityGraphFigure.getCropLeft(),
                    roiDataBounds.preciseY() + roiDataBounds.preciseHeight() -intensityGraphFigure.getCropTop());
            return new PrecisionRectangle(lt.preciseX()-getBounds().x, lt.preciseY()-getBounds().y, rb.preciseX() - lt.preciseX(), rb.preciseY() - lt.preciseY());
        }

        public void setROIColor(Color roiColor) {
            roiRectFigure.setForegroundColor(roiColor);
        }
    }

