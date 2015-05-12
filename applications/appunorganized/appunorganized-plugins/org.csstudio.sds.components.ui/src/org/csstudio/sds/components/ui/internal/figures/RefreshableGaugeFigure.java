package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.ui.internal.figureparts.RoundScale;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScaleTickMarks;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScaledRamp;
import org.csstudio.sds.util.RotationUtil;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * The figure of gauge
 * @author Xihui Chen
 *
 */
public class RefreshableGaugeFigure extends AbstractRoundRampedFigure {

    private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_WHITE);
    private final static Color BORDER_COLOR = CustomMediaFactory.getInstance().getColor(
            new RGB(100, 100, 100));
    private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);
    private final static Font DEFAULT_LABEL_FONT = CustomMediaFactory.getInstance().getFont(
            new FontData("Arial", 12, SWT.BOLD));
    private final static int BORDER_WIDTH = 2;

    private boolean effect3D = true;

    private final NeedleCenter needleCenter;

    private final Needle needle;

    private final Label valueLabel;

    public RefreshableGaugeFigure() {
        super();
        transparent = true;
        scale.setScaleLineVisible(false);
        scale.setTickLableSide(LabelSide.Secondary);
        ramp.setRampWidth(10);

        valueLabel = new Label();
        valueLabel.setText("20.00");
        valueLabel.setFont(DEFAULT_LABEL_FONT);

        needle = new Needle();
        needle.setFill(true);
        needle.setOutline(false);

        needleCenter = new NeedleCenter();
        needleCenter.setOutline(false);

        setLayoutManager(new GaugeLayout());
        add(ramp, GaugeLayout.RAMP);
        add(scale, GaugeLayout.SCALE);
        add(valueLabel, GaugeLayout.VALUE_LABEL);
        add(needle, GaugeLayout.NEEDLE);
        add(needleCenter, GaugeLayout.NEEDLE_CENTER);
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

    @Override
    public void setBounds(final Rectangle rect) {

        super.setBounds(rect);
    }

    @Override
    public void setValue(final double value) {
        super.setValue(value);
        valueLabel.setText(scale.format(value));
    }

    @Override
    protected void paintClientArea(final Graphics graphics) {
        graphics.setAntialias(SWT.ON);
        Rectangle area = getClientArea();
        area.width = Math.min(area.width, area.height);
        area.height = area.width;
        Pattern pattern = null;
        graphics.pushState();
        graphics.setBackgroundColor(GRAY_COLOR);
        if(effect3D) {
            pattern = new Pattern(Display.getCurrent(), area.x, area.y,
                    area.x+area.width, area.y + area.height, BORDER_COLOR, WHITE_COLOR);
            graphics.setBackgroundPattern(pattern);
        }
        graphics.fillOval(area);
        graphics.popState();

        if(effect3D){
            pattern.dispose();
            area.shrink(BORDER_WIDTH, BORDER_WIDTH);
        } else {
            area.shrink(1, 1);
        }

        graphics.fillOval(area);


        super.paintClientArea(graphics);

            //glossy effect
        if(effect3D) {
            graphics.pushState();
            graphics.setAntialias(SWT.ON);
            final double R = area.width/2;
            final double UD_FILL_PART = 9.5d/10d;
            final double UP_DOWN_RATIO = 1d/2d;
            final double LR_FILL_PART = 8.5d/10d;
            final double UP_ANGLE = 0d * Math.PI/180d;
            final double DOWN_ANGLE = 35d * Math.PI/180d;

            Pattern glossyPattern = new Pattern(Display.getCurrent(),
                    area.x + area.width/2, (float)(area.y + area.height/2 - R * UD_FILL_PART),
                    area.x + area.width/2, (float) (area.y + area.height/2 + R * UP_DOWN_RATIO),
                    WHITE_COLOR, 90, WHITE_COLOR, 0);
            graphics.setBackgroundPattern(glossyPattern);
            Rectangle rectangle = new Rectangle(
                    (int)(area.x + area.width/2 - R * LR_FILL_PART *Math.cos(UP_ANGLE)),
                    (int)(area.y + area.height/2 - R * UD_FILL_PART),
                    (int)(2*R* LR_FILL_PART*Math.cos(UP_ANGLE)), (int)(R*UD_FILL_PART + R*UP_DOWN_RATIO));
            graphics.fillOval(rectangle);
            glossyPattern.dispose();

            glossyPattern = new Pattern(Display.getCurrent(),
                    area.x + area.width/2, (float)(area.y + area.height/2 + R * UP_DOWN_RATIO -1),
                    area.x + area.width/2, (float) (area.y + area.height/2 + R * UD_FILL_PART + 1),
                    WHITE_COLOR, 0, WHITE_COLOR, 40);
            graphics.setBackgroundPattern(glossyPattern);
            rectangle = new Rectangle(
                    (int)(area.x + area.width/2 - R*LR_FILL_PART*Math.sin(DOWN_ANGLE)),
                    (int)Math.ceil(area.y + area.height/2 + R * UP_DOWN_RATIO),
                    (int)(2*R*LR_FILL_PART*Math.sin(DOWN_ANGLE)),
                    (int)Math.ceil(R*UD_FILL_PART - R*UP_DOWN_RATIO));
            graphics.fillOval(rectangle);
            glossyPattern.dispose();
            graphics.popState();
        }


    }

    public void setNeedleColor(final Color color) {
        needle.setBackgroundColor(color);
    }

    /**
     * @param effect3D the effect3D to set
     */
    public void setEffect3D(final boolean effect3D) {
        this.effect3D = effect3D;
    }

    class Needle extends Polygon {
        @Override
        protected void fillShape(final Graphics g) {
            g.setAntialias(SWT.ON);
            super.fillShape(g);
        }
    }

    class NeedleCenter extends Ellipse {

        public static final int DIAMETER = 16;

        @Override
        protected void fillShape(final Graphics graphics) {
            graphics.setAntialias(SWT.ON);
            Pattern pattern = null;
            graphics.setBackgroundColor(GRAY_COLOR);
            if(effect3D){
                pattern = new Pattern(Display.getCurrent(), bounds.x, bounds.y,
                        bounds.x + bounds.width, bounds.y + bounds.height, WHITE_COLOR, BORDER_COLOR);
                graphics.setBackgroundPattern(pattern);
            }
            super.fillShape(graphics);
            if(effect3D) {
                pattern.dispose();
            }
        }
    }


    class GaugeLayout extends AbstractLayout {

        private static final int GAP_BTW_NEEDLE_SCALE = -1;

        /** Used as a constraint for the scale. */
        public static final String SCALE = "scale";   //$NON-NLS-1$
        /** Used as a constraint for the Needle. */
        public static final String NEEDLE = "needle"; //$NON-NLS-1$
        /** Used as a constraint for the Ramp */
        public static final String RAMP = "ramp";      //$NON-NLS-1$
        /** Used as a constraint for the needleCenter */
        public static final String NEEDLE_CENTER = "needleCenter";      //$NON-NLS-1$
        /** Used as a constraint for the value label*/
        public static final String VALUE_LABEL = "valueLabel";      //$NON-NLS-1$

        private RoundScale scale;
        private RoundScaledRamp ramp;
        private Polygon needle;
        private NeedleCenter needleCenter;
        private Label valueLabel;
        private final PointList needlePoints = new PointList(new int[] {0,0,0,0,0,0});


        @Override
        public void setConstraint(final IFigure child, final Object constraint) {
            if(constraint.equals(SCALE)) {
                scale = (RoundScale)child;
            } else if (constraint.equals(RAMP)) {
                ramp = (RoundScaledRamp) child;
            } else if (constraint.equals(NEEDLE)) {
                needle = (Polygon) child;
            } else if (constraint.equals(NEEDLE_CENTER)) {
                needleCenter = (NeedleCenter) child;
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

            if(scale != null) {
                scale.setBounds(area);
            }

            if((ramp != null) && ramp.isVisible()) {
                Rectangle rampBounds = area.getCopy();
                ramp.setBounds(rampBounds.shrink(area.width/4, area.height/4));
            }

            if(valueLabel != null) {
                Dimension labelSize = valueLabel.getPreferredSize();
                valueLabel.setBounds(new Rectangle(area.x + area.width/2 - labelSize.width/2,
                        area.y + area.height * 7/8 - labelSize.height/2,
                        labelSize.width, labelSize.height));
            }

            if((needle != null) && (scale != null)) {
                needlePoints.setPoint (
                        new Point(center.x, center.y - NeedleCenter.DIAMETER/2 + 3), 0);
                scale.getScaleTickMarks();
                needlePoints.setPoint(
                        new Point(center.x + area.width/2 - RoundScaleTickMarks.MAJOR_TICK_LENGTH
                        - GAP_BTW_NEEDLE_SCALE, center.y), 1);
                needlePoints.setPoint(
                        new Point(center.x, center.y + NeedleCenter.DIAMETER/2 - 3), 2);

                double valuePosition = 360 - scale.getValuePosition(value, false);
                needlePoints.setPoint(
                        RotationUtil.rotate(needlePoints.getPoint(0),    valuePosition, center), 0);
                needlePoints.setPoint(
                        RotationUtil.rotate(needlePoints.getPoint(1), valuePosition, center), 1);
                needlePoints.setPoint(
                        RotationUtil.rotate(needlePoints.getPoint(2), valuePosition, center),2);
                needle.setPoints(needlePoints);

            }

            if(needleCenter != null){
                needleCenter.setBounds(new Rectangle(center.x - NeedleCenter.DIAMETER/2,
                        center.y - NeedleCenter.DIAMETER/2,
                        NeedleCenter.DIAMETER, NeedleCenter.DIAMETER));
            }

        }
    }

}
