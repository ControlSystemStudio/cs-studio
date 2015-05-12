package org.csstudio.sds.components.ui.internal.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.ui.internal.figureparts.Bulb;
import org.csstudio.sds.components.ui.internal.figureparts.PolarPoint;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScale;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScaledRamp;
import org.csstudio.sds.util.RotationUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;


/**
 * The figure of knob
 * @author Xihui Chen
 *
 */
public class KnobFigure extends AbstractRoundRampedFigure {

    private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_WHITE);
    private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);
    private final static Font DEFAULT_LABEL_FONT = CustomMediaFactory.getInstance().getFont(
            new FontData("Arial", 12, SWT.BOLD));
    private final static int BORDER_WIDTH = 2;

    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    private static final int DISABLED_ALPHA = 100;

    private boolean effect3D = true;

    private double increment = 1;

    private boolean manualSetValue = false;

    private final Thumb thumb;

    private final Bulb bulb;
    private Color thumbColor;

    private final Label valueLabel;

    /**
     * Listeners that react on slider events.
     */
    private final List<IKnobListener> knobListeners =
        new ArrayList<IKnobListener>();

    public KnobFigure() {
        super();
        transparent = true;
        scale.setScaleLineVisible(false);
        ramp.setRampWidth(12);

        valueLabel = new Label();
        valueLabel.setText("20.00");
        valueLabel.setFont(DEFAULT_LABEL_FONT);

        bulb = new Bulb();

        thumb = new Thumb();
        thumb.setOutline(false);
        setLayoutManager(new KnobLayout());
        add(ramp, KnobLayout.RAMP);
        add(bulb, KnobLayout.BULB);
        add(scale, KnobLayout.SCALE);
        add(valueLabel, KnobLayout.VALUE_LABEL);

        add(thumb, KnobLayout.THUMB);
        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure source) {
                ramp.setDirty(true);
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
     * Add a knob listener.
     *
     * @param listener
     *            The knob listener to add.
     */
    public void addKnobListener(final IKnobListener listener) {
        knobListeners.add(listener);
    }

    @Override
    public void setBounds(final Rectangle rect) {

        super.setBounds(rect);
    }

    @Override
    public void setValue(final double value) {
        super.setValue(value);
        valueLabel.setText(scale.format(manualSetValue? this.value : value));
        manualSetValue = false;
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
        Rectangle figureBounds = getBounds().getCopy();
        paintAdapter(graphics);
    }

    /**
     * @param color the bulb color to set
     */
    public void setBulbColor(final Color color) {
        bulb.setBulbColor(color);
    }

    /**
     * @param effect3D the effect3D to set
     */
    public void setEffect3D(final boolean effect3D) {
        this.effect3D = effect3D;
        bulb.setEffect3D(effect3D);
    }

    public void setThumbColor(final Color color) {
        this.thumbColor = color;
    }

    public void setValueLabelVisibility(final boolean visible) {
        valueLabel.setVisible(visible);
    }

    /**Convert the difference of two points to the corresponding value to be changed.
     * @param difference the different theta value in degrees between two polar points.
     * @param oldValue the old value before this change
     * @return the value to be changed
     */
    private double calcValueChange(final double difference, final double oldValue) {
        double change;
        double dragRange = ((RoundScale)scale).getLengthInDegrees();
        if(scale.isLogScaleEnabled()) {
                double c = dragRange/(
                        Math.log10(scale.getRange().getUpper()) -
                        Math.log10(scale.getRange().getLower()));

                    change = oldValue * (Math.pow(10, -difference/c) - 1);
        } else {

                change = -(scale.getRange().getUpper() - scale.getRange().getLower())
                        * difference / dragRange;
        }
        return change;
    }

    /**
     * Inform all knob listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    private void fireManualValueChange(final double newManualValue) {

            for (IKnobListener l : knobListeners) {
                l.knobValueChanged(newManualValue);
            }
    }

    /**
     * Definition of listeners that react on knob events.
     *
     * @author Xihui Chen
     *
     */
    public interface IKnobListener {
        /**
         * React on a knob event.
         *
         * @param newValue
         *            The new slider value.
         */
        void knobValueChanged(double newValue);
    }

    class Thumb extends Ellipse {

        class ThumbDragger
        extends MouseMotionListener.Stub
        implements MouseListener {
            private PolarPoint startPP;
            protected double oldValuePosition;
            protected boolean armed;
                Point pole;
                public void mousePressed(final MouseEvent me) {
                    armed = true;
                    pole = scale.getBounds().getCenter();
                    startPP = PolarPoint.point2PolarPoint(pole, bounds.getCenter());
                    //rotate axis to endAngle
                    startPP.rotateAxis(((RoundScale)scale).getEndAngle(), false);


                    oldValuePosition = ((RoundScale)scale).getValuePosition(
                            value, true);
                    me.consume();
                }

                public void mouseDragged(final MouseEvent me) {
                    if (!armed) {
                        return;
                    }

                    PolarPoint currentPP =
                        PolarPoint.point2PolarPoint(pole, me.getLocation());
                    //rotate axis to endAngle
                    currentPP.rotateAxis(((RoundScale)scale).getEndAngle(), false);

                    //coerce currentPP to min or max
                    if(currentPP.theta * 180.0/Math.PI > (((RoundScale)scale).getLengthInDegrees())) {
                        if(Math.abs(((RoundScale)scale).getValuePosition(value, true)-
                            (((RoundScale)scale).getLengthInDegrees())) < ((RoundScale)scale).getLengthInDegrees()/2.0) {
                            currentPP.theta = ((RoundScale)scale).getLengthInDegrees() * Math.PI/180.0;
                        } else {
                            currentPP.theta = 0;
                        }
                    }

                    double difference = currentPP.theta * 180.0/Math.PI - oldValuePosition;
                    double valueChange = calcValueChange(difference, value);
                    if((increment <= 0) || (Math.abs(valueChange) > increment/2.0)) {
                        manualSetValue = true;
                        if(increment > 0) {
                            setValue(value + increment * Math.round(valueChange/increment));
                        } else {
                            setValue(value + valueChange);
                        }

                        oldValuePosition = ((RoundScale)scale).getValuePosition(
                            value, true);
                        fireManualValueChange(value);
                        KnobFigure.this.revalidate();
                        KnobFigure.this.repaint();
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

                public void mouseDoubleClicked(final MouseEvent me) {

                }
        }

        public Thumb() {
            setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

            ThumbDragger thumbDragger = new ThumbDragger();
            addMouseMotionListener(thumbDragger);
            addMouseListener(thumbDragger);
        }

        @Override
        protected void fillShape(final Graphics graphics) {
            graphics.setAntialias(SWT.ON);
            Pattern pattern = null;
            graphics.setBackgroundColor(thumbColor);
            if(effect3D){
                graphics.setBackgroundColor(thumbColor);
                super.fillShape(graphics);
                pattern = new Pattern(Display.getCurrent(), bounds.x, bounds.y,
                        bounds.x + bounds.width, bounds.y + bounds.height,
                        WHITE_COLOR, 0, WHITE_COLOR, 255);
                graphics.setBackgroundPattern(pattern);
            }
            super.fillShape(graphics);
            if(effect3D) {
                pattern.dispose();
            }
            graphics.setForegroundColor(thumbColor);
            Rectangle figureBounds = getBounds().getCopy();
            paintAdapter(graphics);
        }
    }


    class KnobLayout extends AbstractLayout {

        private static final int GAP_BTW_BULB_SCALE = 4;

        /** Used as a constraint for the scale. */
        public static final String SCALE = "scale";   //$NON-NLS-1$
        /** Used as a constraint for the bulb. */
        public static final String BULB = "bulb"; //$NON-NLS-1$
        /** Used as a constraint for the Ramp */
        public static final String RAMP = "ramp";      //$NON-NLS-1$
        /** Used as a constraint for the thumb */
        public static final String THUMB = "thumb";      //$NON-NLS-1$
        /** Used as a constraint for the value label*/
        public static final String VALUE_LABEL = "valueLabel";      //$NON-NLS-1$

        private RoundScale scale;
        private RoundScaledRamp ramp;
        private Bulb bulb;
        private Thumb thumb;
        private Label valueLabel;


        @Override
        public void setConstraint(final IFigure child, final Object constraint) {
            if(constraint.equals(SCALE)) {
                scale = (RoundScale)child;
            } else if (constraint.equals(RAMP)) {
                ramp = (RoundScaledRamp) child;
            } else if (constraint.equals(BULB)) {
                bulb = (Bulb) child;
            } else if (constraint.equals(THUMB)) {
                thumb = (Thumb) child;
            } else if (constraint.equals(VALUE_LABEL)) {
                valueLabel = (Label)child;
            }
        }


        @Override
        protected Dimension calculatePreferredSize(final IFigure container, final int w,
                final int h) {
            Insets insets = container.getInsets();
            Dimension d = new Dimension(256, 256);
            d.expand(insets.getWidth(), insets.getHeight());
            return d;
        }


        public void layout(final IFigure container) {
            Rectangle area = container.getClientArea();
            area.width = Math.min(area.width, area.height);
            area.height = area.width;
            area.shrink(BORDER_WIDTH, BORDER_WIDTH);

            Point center = area.getCenter();
            Rectangle bulbBounds = null;

            if(scale != null) {
                scale.setBounds(area);
                bulbBounds = area.getCopy();
                bulbBounds.shrink(area.width/2 - scale.getInnerRadius() + GAP_BTW_BULB_SCALE,
                        area.height/2 - scale.getInnerRadius() + GAP_BTW_BULB_SCALE);
            }

            if((scale != null) && (ramp != null) && ramp.isVisible()) {
                Rectangle rampBounds = area.getCopy();
                ramp.setBounds(rampBounds.shrink(area.width/2 - scale.getInnerRadius() - ramp.getRampWidth()+2,
                        area.height/2 - scale.getInnerRadius() - ramp.getRampWidth()+2));
            }

            if((valueLabel != null) && valueLabel.isVisible()) {
                Dimension labelSize = valueLabel.getPreferredSize();
                valueLabel.setBounds(new Rectangle(bulbBounds.x + bulbBounds.width/2 - labelSize.width/2,
                        bulbBounds.y + bulbBounds.height * 3/4 - labelSize.height/2,
                        labelSize.width, labelSize.height));
            }

            if((bulb != null) && (scale != null) && bulb.isVisible()) {
                bulb.setBounds(bulbBounds);
            }

            if((scale != null) && (thumb != null) && thumb.isVisible()){
                Point thumbCenter = new Point(bulbBounds.x + bulbBounds.width*7.0/8.0,
                        bulbBounds.y + bulbBounds.height/2);
                double valuePosition = 360 - scale.getValuePosition(value, false);
                thumbCenter = RotationUtil.rotate(thumbCenter,    valuePosition, center);
                int thumbDiameter = bulbBounds.width/6;

                thumb.setBounds(new Rectangle(thumbCenter.x - thumbDiameter/2,
                        thumbCenter.y - thumbDiameter/2,
                        thumbDiameter, thumbDiameter));
            }
        }
    }

}
