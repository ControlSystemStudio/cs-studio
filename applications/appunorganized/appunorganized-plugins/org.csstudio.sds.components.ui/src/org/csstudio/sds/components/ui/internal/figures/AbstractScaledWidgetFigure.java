package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.model.AbstractScaledWidgetModel;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.swt.xygraph.linearscale.AbstractScale;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

/**
 * Base figure for a widget based on {@link AbstractScaledWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractScaledWidgetFigure extends Figure implements
    IAdaptable {

    protected AbstractScale scale;

    protected boolean transparent;

    protected double value;

    protected double minimum;

    protected double maximum;

    protected double majorTickMarkStepHint;

    protected boolean showMinorTicks;

    protected boolean showScale;

    protected boolean logScale;


    /** A border adapter, which covers all border handlings. */
    private IBorderEquippedWidget _borderAdapter;

    private ICrossedFigure _crossedOutAdapter;

    private IRhombusEquippedWidget _rhombusAdapter;

    @Override
    public boolean isOpaque() {
        return false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        if (!transparent) {
            graphics.setBackgroundColor(this.getBackgroundColor());
            bounds.crop(this.getInsets());
            graphics.fillRectangle(bounds);
        }
        super.paintFigure(graphics);
        paintAdapter(graphics);
    }

    protected void paintAdapter(final Graphics graphics) {
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);

    }

    /**
     * @param value the value to set
     */
    public void setValue(final double value) {
        this.value =
            Math.max(scale.getRange().getLower(), Math.min(scale.getRange().getUpper(), value));
    }

    /**
     * set the range of the scale
     * @param min
     * @param max
     */
    public void setRange(final double min, final double max) {
        this.minimum = min;
        this.maximum = max;
        scale.setRange(new Range(min, max));
    }

    /**
     * @param majorTickMarkStepHint the majorTickMarkStepHint to set
     */
    public void setMajorTickMarkStepHint(final double majorTickMarkStepHint) {
        this.majorTickMarkStepHint = majorTickMarkStepHint;
        scale.setMajorTickMarkStepHint((int) majorTickMarkStepHint);
    }

    /**
     * @param showMinorTicks the showMinorTicks to set
     */
    public void setShowMinorTicks(final boolean showMinorTicks) {
        this.showMinorTicks = showMinorTicks;
        scale.setMinorTicksVisible(showMinorTicks);
    }


    /**
     * @param showScale the showScale to set
     */
    public void setShowScale(final boolean showScale) {
        this.showScale = showScale;
        scale.setVisible(showScale);
    }

    /**
     * @param logScale the logScale to set
     */
    public void setLogScale(final boolean logScale) {
        this.logScale = logScale;
        scale.setLogScale(logScale);
        scale.setRange(new Range(minimum, maximum));
    }

    /**
     * Sets, if this widget should have a transparent background.
     * @param transparent
     *                 The new value for the transparent property
     */
    public void setTransparent(final boolean transparent) {
        this.transparent = transparent;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if(_borderAdapter==null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }
        return null;
    }

}
