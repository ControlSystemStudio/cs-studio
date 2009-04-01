package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.model.AbstractScaledWidgetModel;
import org.csstudio.sds.components.ui.internal.figureparts.AbstractScale;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScale;
import org.csstudio.sds.components.ui.internal.figureparts.Range;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

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
	
	protected boolean showMinorTicks;
	
	protected boolean showScale;
	
	protected boolean logScale;	
	

	/** A border adapter, which covers all border handlings. */
	private IBorderEquippedWidget _borderAdapter;
	
	
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
	 * @param value the value to set
	 */
	public void setValue(final double value) {
		this.value = 
			Math.max(scale.getRange().lower, Math.min(scale.getRange().upper, value));
	}
	
	
	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(final double minimum) {
		this.minimum = minimum;
		scale.setRange(new Range(minimum, scale.getRange().upper));
		//value = Math.max(scale.getRange().lower, value); 
		scale.revalidate();
		
	}

	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(final double maximum) {
		this.maximum = maximum;
		scale.setRange(new Range(scale.getRange().lower, maximum));
		//value = Math.min(scale.getRange().upper, value);
		scale.revalidate();
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
		scale.revalidate();
	}	

	/**
	 * Sets, if this widget should have a transparent background.
	 * @param transparent
	 * 				The new value for the transparent property
	 */
	public void setTransparent(final boolean transparent) {
		this.transparent = transparent;
	}	
	

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

}
