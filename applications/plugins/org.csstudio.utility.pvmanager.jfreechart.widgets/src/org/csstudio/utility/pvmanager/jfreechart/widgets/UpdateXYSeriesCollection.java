/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author shroffk
 * 
 */
public class UpdateXYSeriesCollection implements Runnable {

	private Composite parent;
	private XYSeriesCollection xySeriesCollection;
	private XYSeries xySeries;

	public UpdateXYSeriesCollection(Composite parent,
			XYSeriesCollection xySeriesCollection, XYSeries xySeries) {
		super();
		this.parent = parent;
		this.xySeriesCollection = xySeriesCollection;
		this.xySeries = xySeries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (!parent.isDisposed()) {
			xySeriesCollection.removeAllSeries();
			xySeriesCollection.addSeries(xySeries);
		}
	}

}
