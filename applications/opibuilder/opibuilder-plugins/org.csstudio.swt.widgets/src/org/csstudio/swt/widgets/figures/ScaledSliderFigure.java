/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;



import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figureparts.AlphaLabel;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.swt.widgets.util.OPITimer;
import org.csstudio.swt.widgets.util.RepeatFiringBehavior;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.csstudio.swt.xygraph.linearscale.LinearScaledMarker;
import org.csstudio.ui.util.ColorConstants;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A Scaled Slider figure
 * @author Xihui Chen
 *
 */
public class ScaledSliderFigure extends AbstractLinearMarkedFigure {

    private Color fillColor = BLUE_COLOR;

    private Color fillBackgroundColor = GRAY_COLOR;
    private Color thumbColor = CustomMediaFactory.getInstance().getColor(
            new RGB(172,172, 172));
    private boolean effect3D = true;
    private boolean horizontal = false;
    private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_WHITE);

    //border color for track and thumb
    private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);

    private final static Color GREEN_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GREEN);
    private final static Color BLUE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_BLUE);
    private final static Color LABEL_COLOR = CustomMediaFactory.getInstance().getColor(
            new RGB(255, 255, 150));

    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    private static final int DISABLED_ALPHA = 100;
    private Track track;

    private Thumb thumb;

    private AlphaLabel label;

    private double stepIncrement = 1;

    private double pageIncrement = 10;

    /**
     * Listeners that react on slider events.
     */
    private List<IManualValueChangeListener> listeners =
        new ArrayList<IManualValueChangeListener>();

    public ScaledSliderFigure() {

        super();
        scale.setScaleLineVisible(false);
        scale.setTickLableSide(LabelSide.Secondary);

        if(horizontal) {
            ((LinearScale)scale).setOrientation(Orientation.HORIZONTAL);
            scale.setTickLableSide(LabelSide.Primary);
            marker.setLabelSide(LabelSide.Secondary);
        }else {
            ((LinearScale)scale).setOrientation(Orientation.VERTICAL);
            scale.setTickLableSide(LabelSide.Secondary);
            marker.setLabelSide(LabelSide.Primary);
        }

        track = new Track();
        thumb = new Thumb();

        label = new AlphaLabel();
        label.setBackgroundColor(LABEL_COLOR);
        //label.setOpaque(true);
        label.setBorder(new LineBorder(GRAY_COLOR));
        label.setVisible(false);
        setLayoutManager(new XSliderLayout());
        add(scale, XSliderLayout.SCALE);
        add(marker, XSliderLayout.MARKERS);
        add(track, XSliderLayout.TRACK);
        add(thumb, XSliderLayout.THUMB);
        add(label, "label");

        addFigureListener(new FigureListener() {
            public void figureMoved(IFigure source) {
                revalidate();
            }
        });

        addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent ke) {
                    if((ke.keycode == SWT.ARROW_DOWN && !horizontal) ||
                            (ke.keycode == SWT.ARROW_LEFT && horizontal) )
                        stepDown();
                    else if((ke.keycode == SWT.ARROW_UP && !horizontal) ||
                            (ke.keycode == SWT.ARROW_RIGHT && horizontal) )
                        stepUp();
                    else if(ke.keycode == SWT.PAGE_UP)
                        pageUp();
                    else if(ke.keycode == SWT.PAGE_DOWN)
                        pageDown();
                }

                public void keyReleased(KeyEvent ke) {
                }
            });

        addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent fe) {
                    repaint();
                }

                public void focusLost(FocusEvent fe) {
                    repaint();
                }
        });

    }



    /**
     * Add a slider listener.
     *
     * @param listener
     *            The slider listener to add.
     */
    public void addManualValueChangeListener(final IManualValueChangeListener listener) {
        listeners.add(listener);
    }

    /**Remove a manual value change listener.
     * @param listener the listner to be removed.
     */
    public void removeManualValueChangeListener(final IManualValueChangeListener listener){
        if(listeners.contains(listener))
            listeners.remove(listener);
    }

    /**Convert the difference of two points to the corresponding value to be changed.
     * @param difference the difference between two points.
     *  difference = endPoint - startPoint
     * @param oldValue the old value before this change
     * @return the value to be changed
     */
    private double calcValueChange(Dimension difference, double oldValue) {
        double change;
        double dragRange = ((LinearScale)scale).getTickLength();
        if(scale.isLogScaleEnabled()) {
                double c = dragRange/(
                        Math.log10(scale.getRange().getUpper()) -
                        Math.log10(scale.getRange().getLower()));
                if(horizontal)
                    change = oldValue * (Math.pow(10, difference.width/c) - 1);
                else
                    change = oldValue * (Math.pow(10, -difference.height/c) - 1);
        } else {
            if(horizontal)
                change = (scale.getRange().getUpper() - scale.getRange().getLower())
                        * difference.width / dragRange;
            else
                change = -(scale.getRange().getUpper() - scale.getRange().getLower())
                        * difference.height / dragRange;
        }
        return change;
    }

    /**
     * Inform all slider listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    private void fireManualValueChange(final double newManualValue) {

            for (IManualValueChangeListener l : listeners) {
                l.manualValueChanged(newManualValue);
            }
    }




    /**
     * @return the fillBackgroundColor
     */
    public Color getFillBackgroundColor() {
        return fillBackgroundColor;
    }

    /**
     * @return the fillColor
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * @return the pageIncrement
     */
    public double getPageIncrement() {
        return pageIncrement;
    }

    /**
     * @return the stepIncrement
     */
    public double getStepIncrement() {
        return stepIncrement;
    }

    /**
     * @return the thumbColor
     */
    public Color getThumbColor() {
        return thumbColor;
    }

    /**
     * @return the effect3D
     */
    public boolean isEffect3D() {
        return effect3D;
    }

    /**
     * @return the horizontal
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    //override this to avoid revalidate the whole FigureCanvas.
    @Override
    public void revalidate() {
        invalidate();
        getUpdateManager().addInvalidFigure(this);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    /**Set Value from manual control of the widget. Value will be coerced in range.
     * @param value
     */
    public void manualSetValue(double value){
        setValue(getCoercedValue(value));
//                value < minimum ? minimum : (value > maximum ? maximum : value));
    }

    public void pageDown(){
        manualSetValue(getValue() - pageIncrement);
        fireManualValueChange(getValue());
    }

    public void pageUp(){
        manualSetValue(getValue() + pageIncrement);
        fireManualValueChange(getValue());
    }

    @Override
    protected void paintClientArea(Graphics graphics) {
        super.paintClientArea(graphics);
        if(hasFocus()){
            graphics.setForegroundColor(ColorConstants.black);
            graphics.setBackgroundColor(ColorConstants.white);

            Rectangle area = getClientArea();
            graphics.drawFocus(area.x, area.y, area.width-1, area.height-1);
        }
        if(!isEnabled()) {
            graphics.setAlpha(DISABLED_ALPHA);
            graphics.setBackgroundColor(GRAY_COLOR);
            graphics.fillRectangle(bounds);
        }
    }

    /**
     * @param effect3D the effect3D to set
     */
    public void setEffect3D(boolean effect3D) {
        if(this.effect3D == effect3D)
            return;
        this.effect3D = effect3D;
        repaint();
    }


    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        repaint();

    }

    /**
     * @param fillBackgroundColor the fillBackgroundColor to set
     */
    public void setFillBackgroundColor(Color fillBackgroundColor) {
        if(this.fillBackgroundColor !=null && this.fillBackgroundColor.equals(fillBackgroundColor))
            return;
        this.fillBackgroundColor = fillBackgroundColor;
        repaint();
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(Color fillColor) {
        if(this.fillColor != null && this.fillColor.equals(fillColor))
            return;
        this.fillColor = fillColor;
        repaint();
    }


    /**
     * @param horizontal the horizontal to set
     */
    public void setHorizontal(boolean horizontal) {
        if(this.horizontal == horizontal)
            return;
        this.horizontal = horizontal;
        if(horizontal) {
            ((LinearScale)scale).setOrientation(Orientation.HORIZONTAL);
            scale.setTickLableSide(LabelSide.Primary);
            marker.setLabelSide(LabelSide.Secondary);
        }else {
            ((LinearScale)scale).setOrientation(Orientation.VERTICAL);
            scale.setTickLableSide(LabelSide.Secondary);
            marker.setLabelSide(LabelSide.Primary);
        }
        revalidate();
    }


    public void setPageIncrement(double pageIncrement) {
        this.pageIncrement = pageIncrement;
    }


    /**
     * @param stepIncrement the stepIncrement to set
     */
    public void setStepIncrement(double stepIncrement) {
        this.stepIncrement = stepIncrement;
    }

    /**
     * @param thumbColor the thumbColor to set
     */
    public void setThumbColor(Color thumbColor) {
        if(this.thumbColor != null && this.thumbColor.equals(thumbColor))
            return;
        this.thumbColor = thumbColor;
        repaint();
    }

    @Override
    public void setValue(double value) {
        super.setValue(value);
        revalidate();
    }

    public void stepDown(){
        manualSetValue(getValue() - stepIncrement);
        fireManualValueChange(getValue());
    }

    public void stepUp(){
        manualSetValue(getValue() + stepIncrement);
        fireManualValueChange(getValue());
    }


    class Thumb extends Polygon {
        class ThumbDragger
        extends MouseMotionListener.Stub
        implements MouseListener {
            protected Point start;

                protected boolean armed;

                private OPITimer timer;

                public void mouseDoubleClicked(MouseEvent me) {

                }

                public void mouseDragged(MouseEvent me) {
                    if (!armed)
                        return;
                    Dimension difference = me.getLocation().getDifference(start);
                    double valueChange = calcValueChange(difference, value);
                    double oldValue = value;
                    if(stepIncrement <= 0 || Math.abs(valueChange) > stepIncrement/2.0) {
                        if(stepIncrement > 0)
                            manualSetValue(value + stepIncrement * Math.round(valueChange/stepIncrement));
                        else
                            manualSetValue(value + valueChange);
                        label.setVisible(true);
                        double valuePosition =
                                ((LinearScale)scale).getValuePosition(getCoercedValue(), false);

                        // Throttle updates to a maximum of 10 Hz. This avoids a large number of
                        // updates being queued and continuing to update the PV value (and hence
                        // the slider position) after the drag has finished.
                        if(value != oldValue) {
                            if(timer == null) {
                                timer = new OPITimer();
                            }
                            if(timer.isDue()) {
                                timer.start(new Runnable() {
                                    @Override
                                    public void run() {
                                        // This call is what finally sets the PV value to the
                                        // latest cached value.
                                        fireManualValueChange(value);
                                    }
                                }, 100);
                            }
                        }
                        start = new Point(
                                    horizontal? valuePosition: 0,
                                    horizontal ? 0 : valuePosition);
                    }
                    me.consume();
                }



                public void mouseEntered(MouseEvent me) {
                    temp = thumbColor;
                    thumbColor = GREEN_COLOR;
                    repaint();
                }

                public void mouseExited(MouseEvent me) {
                    thumbColor = temp;
                    label.setVisible(false);
                    repaint();
                }

                public void mousePressed(MouseEvent me) {
                    if(me.button != 1)
                        return;
                    armed = true;
                    double valuePosition =
                        ((LinearScale)scale).getValuePosition(getCoercedValue(), false);
                    start = new Point(
                            horizontal? valuePosition: 0,
                            horizontal ? 0 : valuePosition);
                    label.setVisible(true);
                    if(!ScaledSliderFigure.this.hasFocus()){
                        ScaledSliderFigure.this.requestFocus();
                    }
                    me.consume();

                }

                public void mouseReleased(MouseEvent me) {
                    if(me.button != 1)
                        return;
                    if (!armed)
                        return;
                    armed = false;
                    me.consume();
                }
        }
        public static final  int LENGTH = 20;
        public static final int BREADTH = 11;
        public final PointList  horizontalThumbPointList = new PointList(new int[] {
                0,0,  0, BREADTH,  LENGTH*4/5, BREADTH,  LENGTH, BREADTH/2,
                LENGTH*4/5, 0}) ;
        public final PointList verticalThumbPointList = new PointList(new int[] {
                0,0,  0, LENGTH*4/5, BREADTH/2, LENGTH, BREADTH, LENGTH*4/5, BREADTH,
                0}) ;

        private Color temp;

        public Thumb() {
            super();
            setOutline(true);
            setFill(true);
            setCursor(Cursors.HAND);
            setForegroundColor(GRAY_COLOR);
            setLineWidth(1);
            ThumbDragger thumbDragger = new ThumbDragger();
            addMouseMotionListener(thumbDragger);
            addMouseListener(thumbDragger);
        }

        @Override
        protected void fillShape(Graphics g) {
            g.setAntialias(SWT.ON);
            g.setClip(new Rectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height));
            g.setBackgroundColor(WHITE_COLOR);
            super.fillShape(g);
            Point leftPoint = getPoints().getPoint(0);
            Point rightPoint;
            if(horizontal)
                rightPoint = getPoints().getPoint(4);
            else
                rightPoint = getPoints().getPoint(1);//.translate(0, -BREADTH/2);
            Pattern thumbPattern = null;
            boolean support3D = GraphicsUtil.testPatternSupported(g);
            if(effect3D && support3D) {
                thumbPattern = GraphicsUtil.createScaledPattern(g, Display.getCurrent(),
                    leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y, WHITE_COLOR, 0,
                    thumbColor, 255);
                g.setBackgroundPattern(thumbPattern);
            }else
                g.setBackgroundColor(thumbColor);

            g.fillPolygon(getPoints());

            if(effect3D && support3D)
                thumbPattern.dispose();

        }
    }
    class Track extends RectangleFigure {
            public static final int TRACK_BREADTH = 6;
            private RepeatFiringBehavior behavior = new RepeatFiringBehavior();
            private double pressedValue;
            private boolean pageUp;
            public Track() {
                super();

                setOutline(false);
                setForegroundColor(GRAY_COLOR);
                setCursor(Cursors.HAND);

                behavior.setRunTask(new Runnable() {

                    public void run() {
                        if(pageUp){
                            if(getValue() >=pressedValue)
                                behavior.suspend();
                            else
                                pageUp();

                        }else{
                            if(getValue() <= pressedValue)
                                behavior.suspend();
                            else
                                pageDown();
                        }
                    }
                });

                addMouseListener(new MouseListener.Stub(){
                    @Override
                    public void mousePressed(MouseEvent me) {
                        if(me.button != 1)
                            return;
                        Point start = me.getLocation();
                        if(horizontal)
                            start.x = start.x + thumb.getBounds().width/2;
                        else
                            start.y = start.y + thumb.getBounds().height/2;
                        pressedValue = ((LinearScale)scale).getPositionValue(horizontal? start.x : start.y, false);
                        if(pressedValue > getValue())
                            pageUp = true;
                        else
                            pageUp = false;
                        behavior.pressed();
                        if(!ScaledSliderFigure.this.hasFocus()){
                            ScaledSliderFigure.this.requestFocus();
                        }
                        me.consume();
                    }
                    @Override
                    public void mouseReleased(MouseEvent me) {
                        if(me.button != 1)
                            return;
                        behavior.released();
                    }

                });
            }

            @Override
            protected void fillShape(Graphics graphics) {

                graphics.setAntialias(SWT.ON);
                int valuePosition = ((LinearScale) scale).getValuePosition(getCoercedValue(), false);
                boolean support3D = GraphicsUtil.testPatternSupported(graphics);
                if(effect3D && support3D) {
                    //fill background
                    graphics.setBackgroundColor(fillBackgroundColor);
                    super.fillShape(graphics);
                    Pattern backGroundPattern;
                    if(horizontal)
                        backGroundPattern= GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
                            bounds.x, bounds.y,
                            bounds.x, bounds.y + bounds.height,
                            WHITE_COLOR, 255,
                            fillBackgroundColor, 0);
                    else
                        backGroundPattern= GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
                            bounds.x, bounds.y,
                            bounds.x + bounds.width, bounds.y,
                            WHITE_COLOR, 255,
                            fillBackgroundColor, 0);
                    graphics.setBackgroundPattern(backGroundPattern);
                    super.fillShape(graphics);
                    graphics.setForegroundColor(fillBackgroundColor);
                    outlineShape(graphics);
                    backGroundPattern.dispose();

                    //fill value
                    if(horizontal)
                        backGroundPattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
                            bounds.x, bounds.y,
                            bounds.x, bounds.y + bounds.height,
                            WHITE_COLOR, 255,
                            fillColor, 0);
                    else
                        backGroundPattern = GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(),
                            bounds.x, bounds.y,
                            bounds.x + bounds.width, bounds.y,
                            WHITE_COLOR, 255,
                            fillColor, 0);

                    graphics.setBackgroundColor(fillColor);
                    graphics.setForegroundColor(fillColor);
                    if(horizontal){
                        int fillWidth = valuePosition - bounds.x;
                        graphics.fillRectangle(new Rectangle(bounds.x,
                            bounds.y, fillWidth, bounds.height));
                        graphics.setBackgroundPattern(backGroundPattern);
                        graphics.fillRectangle(new Rectangle(bounds.x,
                            bounds.y, fillWidth, bounds.height));

                        graphics.drawRectangle(new Rectangle(bounds.x + lineWidth / 2,
                            bounds.y + lineWidth / 2,
                            fillWidth - Math.max(1, lineWidth),
                            bounds.height - Math.max(1, lineWidth)));


                    }else {
                        int fillHeight = bounds.height - (valuePosition - bounds.y);
                        graphics.fillRectangle(new Rectangle(bounds.x,
                            valuePosition, bounds.width, fillHeight));
                        graphics.setBackgroundPattern(backGroundPattern);
                        graphics.fillRectangle(new Rectangle(bounds.x,
                            valuePosition, bounds.width, fillHeight));

                        graphics.drawRectangle(new Rectangle(bounds.x + lineWidth / 2,
                            valuePosition+ lineWidth / 2,
                            bounds.width- Math.max(1, lineWidth),
                            fillHeight - Math.max(1, lineWidth)));
                    }

                    backGroundPattern.dispose();




                }else {
                    graphics.setBackgroundColor(fillBackgroundColor);
                    super.fillShape(graphics);
                    graphics.setBackgroundColor(fillColor);
                    if(horizontal)
                        graphics.fillRectangle(new Rectangle(bounds.x,
                                bounds.y,
                                valuePosition - bounds.x,
                                bounds.height));
                    else
                        graphics.fillRectangle(new Rectangle(bounds.x,
                                valuePosition,
                                bounds.width,
                                bounds.height - (valuePosition - bounds.y)));
    //                graphics.setForegroundColor(outlineColor);
                }
            }
        }
    class XSliderLayout extends AbstractLayout {

        private static final int GAP_BTW_THUMB_SCALE = 5;
        private static final int ADDITIONAL_MARGIN = 3;
        private static final int LABEL_MARGIN = 3;


        /** Used as a constraint for the scale. */
        public static final String SCALE = "scale";   //$NON-NLS-1$
        /** Used as a constraint for the pipe indicator. */
        public static final String TRACK = "track"; //$NON-NLS-1$
        /** Used as a constraint for the alarm ticks */
        public static final String MARKERS = "markers";      //$NON-NLS-1$
        /** Used as a constraint for the thumb */
        public static final String THUMB = "thumb";      //$NON-NLS-1$
        /** Used as a constraint for the label*/
        public static final String LABEL = "label";      //$NON-NLS-1$

        private LinearScale scale;
        private LinearScaledMarker marker;
        private Track track;
        private Thumb thumb;
        private AlphaLabel label;


        @Override
        protected Dimension calculatePreferredSize(IFigure container, int w,
                int h) {
            Insets insets = container.getInsets();
            Dimension d = new Dimension(64, 4*64);
            d.expand(insets.getWidth(), insets.getHeight());
            return d;
        }

        private void horizontalLayout(IFigure container) {
            Rectangle area = container.getClientArea().getCopy();
            area.x += ADDITIONAL_MARGIN;
            area.width -= 2*ADDITIONAL_MARGIN;
            Dimension scaleSize;
            Dimension markerSize;

            if(scale != null) {
                scaleSize = new Dimension(area.width, 0);
                if(scale.isVisible())
                    scaleSize = scale.getPreferredSize(area.width, -1);
                scale.setBounds(new Rectangle(area.x,
                        area.y + area.height/2 + Thumb.LENGTH/2 + GAP_BTW_THUMB_SCALE,
                        scaleSize.width, scaleSize.height));
            }

            if(marker != null && marker.isVisible()) {
                markerSize = marker.getPreferredSize();
                marker.setBounds(new Rectangle(marker.getScale().getBounds().x,
                        area.y + area.height/2 - markerSize.height - Thumb.LENGTH/2 - GAP_BTW_THUMB_SCALE,
                        markerSize.width, markerSize.height));
            }

            if(track != null) {
                track.setBounds(new Rectangle(
                        scale.getValuePosition(scale.getRange().getLower(), false) - track.getLineWidth(),
                        area.y + area.height/2 - Track.TRACK_BREADTH/2,
                        scale.getTickLength()+ 2*track.getLineWidth(),
                        Track.TRACK_BREADTH));
            }

            if(thumb != null) {
                PointList newPointList = thumb.verticalThumbPointList.getCopy();
                newPointList.translate(scale.getValuePosition(getCoercedValue(), false) - Thumb.BREADTH/2,
                        area.y + area.height/2 - Thumb.LENGTH/2 + 1
                        );
                thumb.setPoints(newPointList);
            }
            if(label != null && label.isVisible())
                setLabel();
        }

        public void layout(IFigure container) {
            if(horizontal)
                horizontalLayout(container);
            else
                verticalLayout(container);
        }


        @Override
        public void setConstraint(IFigure child, Object constraint) {
            if(constraint.equals(SCALE))
                scale = (LinearScale)child;
            else if (constraint.equals(MARKERS))
                marker = (LinearScaledMarker) child;
            else if (constraint.equals(TRACK))
                track = (Track) child;
            else if (constraint.equals(THUMB))
                thumb = (Thumb) child;
            else if (constraint.equals(LABEL))
                label = (AlphaLabel) child;
        }

        private void setLabel() {
            String text = getValueText();
            Dimension textSize = FigureUtilities.getStringExtents(text, label.getFont());
            label.setText(text);
            Rectangle thumbBounds = thumb.getBounds();
            Rectangle clientArea = getClientArea();
            if (horizontal) {
                int topY = thumbBounds.y - textSize.height - 2 * LABEL_MARGIN;
                if (topY > clientArea.y) { // show on top of thumb
                    label.setBounds(new Rectangle(thumbBounds.x + thumbBounds.width / 2
                            - (textSize.width + 2 * LABEL_MARGIN) / 2, topY, textSize.width + 2
                            * LABEL_MARGIN, textSize.height + LABEL_MARGIN));
                } else { // show on right of thumb
                    int rightX = thumbBounds.x + thumbBounds.width + LABEL_MARGIN;
                    if ((rightX + textSize.width + 2 * LABEL_MARGIN) > clientArea.getRight().x)
                        rightX = thumbBounds.x - textSize.width - 2 * LABEL_MARGIN;

                    label.setBounds(new Rectangle(rightX, thumbBounds.y, textSize.width + 2
                            * LABEL_MARGIN, textSize.height + LABEL_MARGIN));
                }
            } else {
                int leftX = thumbBounds.x - textSize.width - 2 * LABEL_MARGIN;
                if (leftX > clientArea.x) { // show on left of thumb
                    label.setBounds(new Rectangle(leftX, thumbBounds.y + thumbBounds.height / 2
                            - (textSize.height + LABEL_MARGIN) / 2, textSize.width + 2
                            * LABEL_MARGIN, textSize.height + LABEL_MARGIN));

                } else { // show on top of thumb
                    int topY = thumbBounds.y- textSize.height - LABEL_MARGIN;
                    if((topY - textSize.height - LABEL_MARGIN)<clientArea.y)
                        topY = thumbBounds.y+thumbBounds.height;
                    label.setBounds(new Rectangle(thumbBounds.x + thumbBounds.width / 2
                            - (textSize.width + 2 * LABEL_MARGIN) / 2, topY, textSize.width + 2 * LABEL_MARGIN,
                            textSize.height + LABEL_MARGIN));
                }
            }
        }

        private void verticalLayout(IFigure container) {
            Rectangle area = container.getClientArea();

            Dimension scaleSize;
            Dimension markerSize;

            if(scale != null) {
                scaleSize = new Dimension(0, area.height);
                if(scale.isVisible())
                    scaleSize = scale.getPreferredSize(-1, area.height);
                scale.setBounds(new Rectangle(area.x + area.width/2 + Thumb.LENGTH/2 + GAP_BTW_THUMB_SCALE,
                        area.y,
                        scaleSize.width, scaleSize.height));
            }

            if(marker != null && marker.isVisible()) {
                markerSize = marker.getPreferredSize();
                marker.setBounds(new Rectangle(
                        area.x + area.width/2 - markerSize.width - Thumb.LENGTH/2 - GAP_BTW_THUMB_SCALE,
                        marker.getScale().getBounds().y, markerSize.width, markerSize.height));
            }

            if(track != null) {
                track.setBounds(new Rectangle(
                        area.x + area.width/2 - Track.TRACK_BREADTH/2,
                        scale.getValuePosition(scale.getRange().getUpper(), false) - track.getLineWidth(),
                        Track.TRACK_BREADTH,
                        scale.getTickLength()+ 2*track.getLineWidth()));
            }

            if(thumb != null) {
                PointList newPointList = thumb.horizontalThumbPointList.getCopy();
                newPointList.translate(area.x + area.width/2 - Thumb.LENGTH/2 + 1,
                        scale.getValuePosition(getCoercedValue(), false) - Thumb.BREADTH/2);
                thumb.setPoints(newPointList);
            }
            if(label != null && label.isVisible())
                setLabel();
        }

    }
}

