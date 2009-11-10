package org.csstudio.swt.xygraph.figures;

import org.csstudio.swt.xygraph.linearscale.Range;

/**
 * A listener on the axis when axis was revalidated.
 * @author Xihui Chen
 *
 */
public interface IAxisListener {
	
    /**
     * This event indicates a change in the axis' value range
     */
    public void axisRangeChanged(Axis axis, Range old_range, Range new_range);
    
	/**
	 * This method will be notified by axis whenever the axis is revalidated.
	 */
	public void axisRevalidated(Axis axis);	
		
}
