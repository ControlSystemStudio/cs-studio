/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;


/**
 * A simple class which uses xor to draw selection. It is fast and optimized
 * to display selected region without flickering. However this implementation
 * won't work on MacOS, but it is a nice feature for Windows and Linux users.
 *  
 * @author Blaz Lipuscek 
 *
 * NOT USED for several reasons:
 * <ul>
 * <li> based on deprecated API, doesn't work on all platforms,
 *      most important OS X.
 * <li> Tighly bound to the Chart. Should be packaged like the
 *      org.eclipse.swt.widgets.Tracker so that one can use it as a
 *      general-purpose zoom/selection tool.
 * <li> Can only zoom within the plot area.
 *      Since it cannot draw outside of the plot window, it obviously can't
 *      zoom "out" like the basic Tracker. For some reason, this implementation
 *      was actually further limited to the plot's axes area.
 * </ul>
 * The Tracker is already there, portable, decoupled and allows us to zoom
 * "out". So why fix it if it ain't broke just for some useless bling-bling?
 */
public class CrossHairSelectionTool
{
    /*
        private final Cursor cursorCross;
        private final Cursor cursorDefault;
        
        private final Point EmptyPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        private final Rectangle EmptyRect = new Rectangle(0, 0, 0, 0);
        private final Color selectionColor;
        
        private Rectangle prevZoomRect = EmptyRect;
        private Point mouseLocation = EmptyPoint;
        private Point mousePoint = EmptyPoint;
        
        public CrossHairSelectionTool() 
        {   
            this.selectionColor = new Color(Chart.this.getDisplay(), 57, 41, 16);
            this.cursorCross = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
            this.cursorDefault = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
            
            addMouseListener(new MouseListener() 
            {
                public void mouseDown(MouseEvent e)
                {
                    if(plot_region.contains(e.x, e.y)) 
                    {
                        mousePoint = new Point(e.x, e.y);
                        Chart.this.redrawTracesWithBounds();
                    }
                }
                
                public void mouseUp(MouseEvent e)
                {
                    rubberZoom(getZoomRect());
                    mousePoint = EmptyPoint;
                    prevZoomRect = EmptyRect;
                    Chart.this.redrawTracesWithBounds();
                }

                public void mouseDoubleClick(MouseEvent e) {}
            });
            
            addMouseTrackListener(new MouseTrackListener() 
            {
                public void mouseEnter(MouseEvent e) { mouseLocation = EmptyPoint; } 
                
                public void mouseExit(MouseEvent e)
                { 
                    mouseLocation = EmptyPoint;
                    Chart.this.redrawTracesWithBounds();
                } 
                
                public void mouseHover(MouseEvent e) {}
            });
            
            addMouseMoveListener(new MouseMoveListener() {
                
                public void mouseMove(MouseEvent e) 
                {
                    if(plot_region.contains(e.x, e.y)) 
                    {
                        if(mouseLocation == EmptyPoint)
                        {
                            prevZoomRect = EmptyRect;
                            redrawTraces();
                        }
                        
                        setCursor(cursorCross);
                        // Let's paint it.
                        GC gc = new GC(Chart.this);
                        // First lets erase previous crosshair trace.
                        paintCrossHair(gc, false);
                        // Set new location.
                        mouseLocation = new Point(e.x, e.y);
                        // Redraw again.
                        paintCrossHair(gc, false);
                        // Let's clear resources.
                        gc.dispose();
                    }
                    else {
                        if(mouseLocation.x != Integer.MIN_VALUE || mouseLocation.y != Integer.MIN_VALUE)
                        {
                            mouseLocation = EmptyPoint;
                            prevZoomRect = EmptyRect;
                            Chart.this.redrawTracesWithBounds();
                        }
                        setCursor(cursorDefault);
                    }
                }
            });
            
            addPaintListener(new PaintListener() {
                public void paintControl(PaintEvent e) { paintCrossHair(e.gc, true); }
            });
        }
        
        public void paintCrossHair(GC gc, boolean completePaint) {
            try {
                if (mouseLocation == EmptyPoint)
                    return;

                // Lets get xor color.
                gc.setForeground(Chart.this.getBackground());

                // We enter xor mode.
                gc.setXORMode(true);
                gc.setLineStyle(SWT.LINE_DOT);

                // Draw vertical and horizontal position lines.
                if (mouseLocation.x != mousePoint.x)
                    gc.drawLine(mouseLocation.x, plot_region.y,
                            mouseLocation.x, plot_region.y + plot_region.height
                                    - 2);
                if (mouseLocation.y != mousePoint.y)
                    gc.drawLine(plot_region.x + 1, mouseLocation.y,
                            plot_region.x + plot_region.width - 2,
                            mouseLocation.y);

                if (mousePoint != EmptyPoint) {
                    if (completePaint) {
                        // Draw selected start position lines.
                        gc.drawLine(mousePoint.x, plot_region.y, mousePoint.x,
                                plot_region.y + plot_region.height - 2);
                        gc.drawLine(plot_region.x + 1, mousePoint.y,
                                plot_region.x + plot_region.width - 2,
                                mousePoint.y);
                    }

                    // Draw selection.
                    gc.setBackground(selectionColor);
                    Rectangle zoomRect = getZoomRect();

                    if (!completePaint) {
                        Region regIntersection = new Region();
                        Region regRepaint = new Region();

                        // Lets get tha part we'll not repaint.
                        regIntersection.add(prevZoomRect);
                        regIntersection.intersect(zoomRect);

                        // Now calculate repaint region.
                        regRepaint.add(zoomRect);
                        regRepaint.add(prevZoomRect);
                        regRepaint.subtract(regIntersection);

                        // Get repaint area union.
                        Rectangle redrawRect = new Rectangle(prevZoomRect.x,
                                prevZoomRect.y, prevZoomRect.width,
                                prevZoomRect.height);
                        redrawRect.add(zoomRect);

                        // Repaint with clipping.
                        Rectangle rectClip = gc.getClipping();
                        gc.setClipping(regRepaint);
                        gc.fillRectangle(Chart.this.plot_region);
                        gc.setClipping(rectClip);

                        // Set new rectangle.
                        prevZoomRect = zoomRect;

                        // Dispose resources.
                        regIntersection.dispose();
                        regRepaint.dispose();

                    } else {
                        // It is a simple repaint, just paint zoom rectangle.
                        gc.fillRectangle(zoomRect);
                    }
                }
                // Reset xor mode.
                gc.setXORMode(false);
            } catch (Exception e) {
                // Just catch any exceptions.
            }
        }
        
        private Rectangle getZoomRect() 
        {
            return getZoomRect(mousePoint, mouseLocation);
        }
        
        private Rectangle getZoomRect(Point p1, Point p2) {
            
            if(p1 == EmptyPoint || p2  == EmptyPoint) 
                return EmptyRect;
            
            int x = Math.min(p1.x, p2.x) + 1;
            int y = Math.min(p1.y, p2.y) + 1;
            int width = Math.max(0, Math.abs(p1.x - p2.x) - 1);
            int height = Math.max(0, Math.abs(p1.y - p2.y) - 1);
            
            return new Rectangle(x, y, width, height);
        }
    } 
    
    */
}



