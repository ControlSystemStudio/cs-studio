package org.csstudio.swt.widgets.figures;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.ScaleWidgetIntrospector;
import org.csstudio.swt.xygraph.linearscale.AbstractScale;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Base figure for a widget based on {@link AbstractScaledWidgetModel}.
 * 
 * @author Xihui Chen
 *
 */
public abstract class AbstractScaledWidgetFigure extends Figure implements Introspectable{

	protected AbstractScale scale;
	
	protected boolean transparent = true;
	
	protected double value = 50;
	
	protected double minimum = 0;	
	
	protected double maximum = 100;
	
	protected int majorTickMarkStepHint;
	
	protected boolean showMinorTicks;
	
	protected boolean showScale;
	
	protected boolean logScale;	
	

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new ScaleWidgetIntrospector().getBeanInfo(this.getClass());
	}
	public double getCoercedValue(){
		return Math.max(scale.getRange().getLower(), Math.min(scale.getRange().getUpper(), value));
	}
	
	/**
	 * @return the majorTickMarkStepHint
	 */
	public int getMajorTickMarkStepHint() {
		return majorTickMarkStepHint;
	}
	
	/**
	 * @return the maximum
	 */
	public double getMaximum() {
		return maximum;
	}
	
	/**
	 * @return the minimum
	 */
	public double getMinimum() {
		return minimum;
	}
	
	/**
	 * @return the scale
	 */
	public AbstractScale getScale() {
		return scale;
	}
	
	public double getValue() {
		return value;
	}

	/**
	 * @return the logScale
	 */
	public boolean isLogScale() {
		return logScale;
	}

	
	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @return the showMinorTicks
	 */
	public boolean isShowMinorTicks() {
		return showMinorTicks;
	}	

	/**
	 * @return the showScale
	 */
	public boolean isShowScale() {
		return showScale;
	}
	/**
	 * @return the transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}
	/**
	 * {@inheritDoc}
	 */
	public void paintFigure(final Graphics graphics) {		
		if (!transparent) {
			graphics.setBackgroundColor(this.getBackgroundColor());
			Rectangle bounds = this.getBounds().getCopy();
			bounds.crop(this.getInsets());
			graphics.fillRectangle(bounds);
		}
		super.paintFigure(graphics);
	}
	/**
	 * @param logScale the logScale to set
	 */
	public void setLogScale(final boolean logScale) {
		if(this.logScale == logScale)
			return;
		this.logScale = logScale;
		scale.setLogScale(logScale);
		scale.setRange(new Range(minimum, maximum));
		repaint();
	}
	/**
	 * @param majorTickMarkStepHint the majorTickMarkStepHint to set
	 */
	public void setMajorTickMarkStepHint(int majorTickMarkStepHint) {
		if(this.majorTickMarkStepHint == majorTickMarkStepHint || majorTickMarkStepHint <=0)
			return;
		this.majorTickMarkStepHint = majorTickMarkStepHint;
		scale.setMajorTickMarkStepHint(majorTickMarkStepHint);
		repaint();
	}
	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(double maximum) {
		if(this.maximum == maximum || maximum < minimum)
			return;
		this.maximum = maximum;
		scale.setRange(new Range(minimum, maximum));
		repaint();
	}
	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(double minimum) {
		if(this.minimum == minimum || minimum > maximum)
			return;
		this.minimum = minimum;
		scale.setRange(new Range(minimum, maximum));
		repaint();
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
	 * @param scale the scale to set
	 */
	public void setScale(AbstractScale scale) {
		this.scale = scale;
	}
	/**
	 * @param showMinorTicks the showMinorTicks to set
	 */
	public void setShowMinorTicks(final boolean showMinorTicks) {
		if(this.showMinorTicks == showMinorTicks)
			return;
		this.showMinorTicks = showMinorTicks;
		scale.setMinorTicksVisible(showMinorTicks);
		repaint();
	}
	/**
	 * @param showScale the showScale to set
	 */
	public void setShowScale(final boolean showScale) {
		if(this.showScale == showScale)
			return;
		this.showScale = showScale;
		scale.setVisible(showScale);
		repaint();
	}
	/**
	 * Sets, if this widget should have a transparent background.
	 * @param transparent
	 * 				The new value for the transparent property
	 */
	public void setTransparent(final boolean transparent) {
		if(this.transparent == transparent)
			return;
		this.transparent = transparent;
		repaint();
	}	
	
	/**
	 * @param value the value to set
	 */
	public void setValue(final double value) {
		this.value = value;
		//	Math.max(scale.getRange().getLower(), Math.min(scale.getRange().getUpper(), value));
		repaint();
	}
	

}
