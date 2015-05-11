package org.csstudio.sds.components.ui.internal.figures;



import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.ui.internal.figureparts.AlphaLabel;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.csstudio.swt.xygraph.linearscale.LinearScaledMarker;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
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
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A Scaled Slider figure
 * @author Xihui Chen
 *
 */
public class ScaledSliderFigure extends AbstractLinearMarkedFigure {

    private Color fillColor;
    private Color fillBackgroundColor;
    private Color thumbColor = CustomMediaFactory.getInstance().getColor(
            new RGB(172,172, 172));
    private Color outlineColor;
    private boolean effect3D;
    private boolean horizontal;

    private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_WHITE);
    //border color for track and thumb
    private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);
    private final static Color GREEN_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GREEN);
    private final static Color LABEL_COLOR = CustomMediaFactory.getInstance().getColor(
            new RGB(255, 255, 150));

    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    private static final int DISABLED_ALPHA = 100;

    private final Track track;
    private final Thumb thumb;
    private final AlphaLabel label;

    private double increment = 1;

    /**
     * Listeners that react on slider events.
     */
    private final List<IScaledSliderListener> _sliderListeners =
        new ArrayList<IScaledSliderListener>();

    public ScaledSliderFigure() {

        super();
        scale.setScaleLineVisible(false);
        scale.setForegroundColor(outlineColor);
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
            public void figureMoved(final IFigure source) {
                revalidate();
            }
        });
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        Rectangle figureBounds = getBounds().getCopy();
        paintAdapter(graphics);
    }

    /**
     * Add a slider listener.
     *
     * @param listener
     *            The slider listener to add.
     */
    public void addSliderListener(final IScaledSliderListener listener) {
        _sliderListeners.add(listener);
    }

    /**
     * Inform all slider listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    private void fireManualValueChange(final double newManualValue) {

            for (IScaledSliderListener l : _sliderListeners) {
                l.sliderValueChanged(newManualValue);
            }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public void setEnabled(final boolean value) {
        super.setEnabled(value);
        repaint();

    }

    /**
     * @param increment the increment to set
     */
    public void setIncrement(final double increment) {
        this.increment = increment;
    }

    @Override
    protected void paintClientArea(final Graphics graphics) {
        super.paintClientArea(graphics);
        if(!isEnabled()) {
            graphics.setAlpha(DISABLED_ALPHA);
            graphics.setBackgroundColor(GRAY_COLOR);
            graphics.fillRectangle(bounds);
        }
    }

    @Override
    public void setForegroundColor(final Color fg) {
        super.setForegroundColor(fg);
        outlineColor = fg;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(final Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @param fillBackgroundColor the fillBackgroundColor to set
     */
    public void setFillBackgroundColor(final Color fillBackgroundColor) {
        this.fillBackgroundColor = fillBackgroundColor;
    }

    /**
     * @param thumbColor the thumbColor to set
     */
    public void setThumbColor(final Color thumbColor) {
        this.thumbColor = thumbColor;
    }

    /**
     * @param effect3D the effect3D to set
     */
    public void setEffect3D(final boolean effect3D) {
        this.effect3D = effect3D;
    }

    @Override
    public void setValue(final double value) {
        super.setValue(value);
        revalidate();
    }


    /**
     * @param horizontal the horizontal to set
     */
    public void setHorizontal(final boolean horizontal) {
        if(this.horizontal == horizontal) {
            return;
        }
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

    /**Convert the difference of two points to the corresponding value to be changed.
     * @param difference the difference between two points.
     *  difference = endPoint - startPoint
     * @param oldValue the old value before this change
     * @return the value to be changed
     */
    private double calcValueChange(final Dimension difference, final double oldValue) {
        double change;
        double dragRange = ((LinearScale)scale).getTickLength();
        if(scale.isLogScaleEnabled()) {
                double c = dragRange/(
                        Math.log10(scale.getRange().getUpper()) -
                        Math.log10(scale.getRange().getLower()));
                if(horizontal) {
                    change = oldValue * (Math.pow(10, difference.width/c) - 1);
                } else {
                    change = oldValue * (Math.pow(10, -difference.height/c) - 1);
                }
        } else {
            if(horizontal) {
                change = (scale.getRange().getUpper() - scale.getRange().getLower())
                        * difference.width / dragRange;
            } else {
                change = -(scale.getRange().getUpper() - scale.getRange().getLower())
                        * difference.height / dragRange;
            }
        }
        return change;
    }


    /**
     * Definition of listeners that react on slider events.
     *
     * @author Xihui Chen
     *
     */
    public interface IScaledSliderListener {
        /**
         * React on a slider event.
         *
         * @param newValue
         *            The new slider value.
         */
        void sliderValueChanged(double newValue);
    }

    class Track extends RectangleFigure {
        public static final int TRACK_BREADTH = 6;
        public Track() {
            super();
            setOutline(false);
            setForegroundColor(GRAY_COLOR);
            Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
            setCursor(handCursor);
            addMouseListener(new MouseListener.Stub(){
                @Override
                public void mousePressed(final MouseEvent me) {
                    Point start = thumb.getLocation();
                    if(horizontal) {
                        start.x = start.x + thumb.getBounds().width/2;
                    } else {
                        start.y = start.y + thumb.getBounds().height/2;
                    }

                    Dimension difference = me.getLocation().getDifference(start);

                    double valueChange = calcValueChange(difference, value);

                    if((increment <= 0) || (Math.abs(valueChange) > increment/2.0)) {
                        if(increment > 0) {
                            setValue(value + increment * Math.round(valueChange/increment));
                        } else {
                            setValue(value + valueChange);
                        }

                        fireManualValueChange(value);
                        ScaledSliderFigure.this.revalidate();
                        ScaledSliderFigure.this.repaint();
                    }
                }
            });
        }

        @Override
        protected void fillShape(final Graphics graphics) {

            graphics.setAntialias(SWT.ON);
            int valuePosition = ((LinearScale) scale).getValuePosition(value, false);
            if(effect3D) {
                setOutline(false);
                //fill background
                graphics.setBackgroundColor(fillBackgroundColor);
                super.fillShape(graphics);
                Pattern backGroundPattern;
                if(horizontal) {
                    backGroundPattern= new Pattern(Display.getCurrent(),
                        bounds.x, bounds.y,
                        bounds.x, bounds.y + bounds.height,
                        WHITE_COLOR, 255,
                        fillBackgroundColor, 0);
                } else {
                    backGroundPattern= new Pattern(Display.getCurrent(),
                        bounds.x, bounds.y,
                        bounds.x + bounds.width, bounds.y,
                        WHITE_COLOR, 255,
                        fillBackgroundColor, 0);
                }
                graphics.setBackgroundPattern(backGroundPattern);
                super.fillShape(graphics);
                graphics.setForegroundColor(fillBackgroundColor);
                outlineShape(graphics);
                backGroundPattern.dispose();

                //fill value
                if(horizontal) {
                    backGroundPattern = new Pattern(Display.getCurrent(),
                        bounds.x, bounds.y,
                        bounds.x, bounds.y + bounds.height,
                        WHITE_COLOR, 255,
                        fillColor, 0);
                } else {
                    backGroundPattern = new Pattern(Display.getCurrent(),
                        bounds.x, bounds.y,
                        bounds.x + bounds.width, bounds.y,
                        WHITE_COLOR, 255,
                        fillColor, 0);
                }

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
                    int fillHeight = bounds.height - (valuePosition - bounds.y) - getLineWidth();
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
                setOutline(true);
                graphics.setBackgroundColor(fillBackgroundColor);
                super.fillShape(graphics);
                graphics.setBackgroundColor(fillColor);
                if(horizontal) {
                    graphics.fillRectangle(new Rectangle(bounds.x + lineWidth,
                            bounds.y + lineWidth,
                            valuePosition - bounds.x - lineWidth,
                            bounds.height - 2*lineWidth));
                } else {
                    graphics.fillRectangle(new Rectangle(bounds.x + lineWidth,
                            valuePosition,
                            bounds.width - 2* lineWidth,
                            bounds.height - (valuePosition - bounds.y)));
                }
                graphics.setForegroundColor(outlineColor);
            }
        }
    }

    class Thumb extends Polygon {
        public static final  int LENGTH = 20;
        public static final int BREADTH = 11;
        public final PointList  horizontalThumbPointList = new PointList(new int[] {
                0,0,  0, BREADTH,  LENGTH*4/5, BREADTH,  LENGTH, BREADTH/2,
                LENGTH*4/5, 0}) ;
        public final PointList verticalThumbPointList = new PointList(new int[] {
                0,0,  0, LENGTH*4/5, BREADTH/2, LENGTH, BREADTH, LENGTH*4/5, BREADTH,
                0}) ;
        private Color temp;

        class ThumbDragger
        extends MouseMotionListener.Stub
        implements MouseListener {
            private static final int LABEL_MARGIN = 3;
            protected Point start;

                protected boolean armed;

                private void setLabel() {
                    String text = scale.format(value);
                    Dimension textSize = FigureUtilities.getStringExtents(text, label.getFont());
                    label.setText(text);
                    if(horizontal) {
                        label.setBounds(new Rectangle(
                            Thumb.this.getBounds().x + Thumb.this.getBounds().width/2
                            - (textSize.width + 2*LABEL_MARGIN)/2,
                            Thumb.this.getBounds().y  - textSize.height - 2*LABEL_MARGIN,
                            textSize.width + 2 * LABEL_MARGIN, textSize.height+LABEL_MARGIN));
                    } else {
                        label.setBounds(new Rectangle(
                            Thumb.this.getBounds().x - textSize.width - 3*LABEL_MARGIN,
                            Thumb.this.getBounds().y + Thumb.this.getBounds().height/2
                            - (textSize.height + LABEL_MARGIN)/2,
                            textSize.width + 2 * LABEL_MARGIN, textSize.height+LABEL_MARGIN));
                    }
                    label.setVisible(true);
                }

                public void mousePressed(final MouseEvent me) {
                    armed = true;
                    start = me.getLocation();
                    setLabel();
                    me.consume();
                }

                public void mouseDragged(final MouseEvent me) {
                    if (!armed) {
                        return;
                    }
                    Dimension difference = me.getLocation().getDifference(start);
                    double valueChange = calcValueChange(difference, value);
                    if((increment <= 0) || (Math.abs(valueChange) > increment/2.0)) {
                        if(increment > 0) {
                            setValue(value + increment * Math.round(valueChange/increment));
                        } else {
                            setValue(value + valueChange);
                        }

                        double valuePosition =
                                ((LinearScale)scale).getValuePosition(value, false);
                        start = new Point(
                                    horizontal? valuePosition: 0,
                                    horizontal ? 0 : valuePosition);
                        fireManualValueChange(value);
                        ScaledSliderFigure.this.layout();
                        ScaledSliderFigure.this.repaint();
                        setLabel();
                    }
                    me.consume();
                }



                public void mouseReleased(final MouseEvent me) {
                    if (!armed) {
                        return;
                    }
                    armed = false;
                    me.consume();
                }

                public void mouseEntered(final MouseEvent me) {
                    temp = thumbColor;
                    thumbColor = GREEN_COLOR;
                    repaint();
                }

                public void mouseExited(final MouseEvent me) {
                    thumbColor = temp;
                    label.setVisible(false);
                    repaint();
                }

                public void mouseDoubleClicked(final MouseEvent me) {

                }
        }

        public Thumb() {
            super();
            setOutline(true);
            setFill(true);
            setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
            setForegroundColor(GRAY_COLOR);
            setLineWidth(1);
            ThumbDragger thumbDragger = new ThumbDragger();
            addMouseMotionListener(thumbDragger);
            addMouseListener(thumbDragger);
        }

        @Override
        protected void fillShape(final Graphics g) {
            g.setAntialias(SWT.ON);
            g.setClip(new Rectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height));
            g.setBackgroundColor(WHITE_COLOR);
            super.fillShape(g);
            Point leftPoint = getPoints().getPoint(0);
            Point rightPoint;
            if(horizontal) {
                rightPoint = getPoints().getPoint(4);
            } else {
                rightPoint = getPoints().getPoint(1);//.translate(0, -BREADTH/2);
            }
            Pattern thumbPattern = null;
            if(effect3D) {
                thumbPattern = new Pattern(Display.getCurrent(),
                    leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y, WHITE_COLOR, 0,
                    thumbColor, 255);
                g.setBackgroundPattern(thumbPattern);
            } else {
                g.setBackgroundColor(thumbColor);
            }

            g.fillPolygon(getPoints());

            if(effect3D) {
                thumbPattern.dispose();
            }

        }
    }

    class XSliderLayout extends AbstractLayout {

        private static final int GAP_BTW_THUMB_SCALE = 5;
        private static final int ADDITIONAL_MARGIN = 3;

        /** Used as a constraint for the scale. */
        public static final String SCALE = "scale";   //$NON-NLS-1$
        /** Used as a constraint for the pipe indicator. */
        public static final String TRACK = "track"; //$NON-NLS-1$
        /** Used as a constraint for the alarm ticks */
        public static final String MARKERS = "markers";      //$NON-NLS-1$
        /** Used as a constraint for the thumb */
        public static final String THUMB = "thumb";      //$NON-NLS-1$

        private LinearScale scale;
        private LinearScaledMarker marker;
        private Track track;
        private Thumb thumb;


        @Override
        public void setConstraint(final IFigure child, final Object constraint) {
            if(constraint.equals(SCALE)) {
                scale = (LinearScale)child;
            } else if (constraint.equals(MARKERS)) {
                marker = (LinearScaledMarker) child;
            } else if (constraint.equals(TRACK)) {
                track = (Track) child;
            } else if (constraint.equals(THUMB)) {
                thumb = (Thumb) child;
            }
        }

        @Override
        protected Dimension calculatePreferredSize(final IFigure container, final int w,
                final int h) {
            Insets insets = container.getInsets();
            Dimension d = new Dimension(64, 4*64);
            d.expand(insets.getWidth(), insets.getHeight());
            return d;
        }

        public void layout(final IFigure container) {
            if(horizontal) {
                horizontalLayout(container);
            } else {
                verticalLayout(container);
            }
        }


        private void horizontalLayout(final IFigure container) {
            Rectangle area = container.getClientArea().getCopy();
            area.x += ADDITIONAL_MARGIN;
            area.width -= 2*ADDITIONAL_MARGIN;
            Dimension scaleSize = new Dimension(0, 0);
            Dimension markerSize = new Dimension(0, 0);

            if(scale != null) {
                scaleSize = scale.getPreferredSize(area.width, -1);
                scale.setBounds(new Rectangle(area.x,
                        area.y + area.height/2 + Thumb.LENGTH/2 + GAP_BTW_THUMB_SCALE,
                        scaleSize.width, scaleSize.height));
            }

            if((marker != null) && marker.isVisible()) {
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
                newPointList.translate(scale.getValuePosition(value, false) - Thumb.BREADTH/2,
                        area.y + area.height/2 - Thumb.LENGTH/2 + 1
                        );
                thumb.setPoints(newPointList);
            }
        }

        private void verticalLayout(final IFigure container) {
            Rectangle area = container.getClientArea();

            Dimension scaleSize = new Dimension(0, 0);
            Dimension markerSize = new Dimension(0, 0);

            if(scale != null) {
                scaleSize = scale.getPreferredSize(-1, area.height);
                scale.setBounds(new Rectangle(area.x + area.width/2 + Thumb.LENGTH/2 + GAP_BTW_THUMB_SCALE,
                        area.y,
                        scaleSize.width, scaleSize.height));
            }

            if((marker != null) && marker.isVisible()) {
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
                        scale.getValuePosition(value, false) - Thumb.BREADTH/2);
                thumb.setPoints(newPointList);
            }
        }

    }
}

