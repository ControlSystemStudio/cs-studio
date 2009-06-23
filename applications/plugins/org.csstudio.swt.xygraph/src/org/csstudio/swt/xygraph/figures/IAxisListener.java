package org.csstudio.swt.xygraph.figures;

/**
 * A listener on the axis when axis was revalidated.
 * @author Xihui Chen
 *
 */
public interface IAxisListener {
	
	/**
	 * This method will be notified by axis whenever the axis is revalidated.
	 */
	public void axisRevalidated(Axis axis);	
		
}
