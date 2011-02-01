/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VMultiDouble;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author shroffk
 * 
 */
public class VMultiChannelChartDisplay extends Composite {

	private ChartComposite chartDisplay;
	private JFreeChart chart;
	private XYSeriesCollection dataset;

	private List<String> pvNames;
	private List<Double> positions;
	Random generator;

	/**
	 * Creates a new display for data of type VMultiDouble
	 * 
	 * @param parent
	 * @param style
	 */
	public VMultiChannelChartDisplay(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		chartDisplay = new ChartComposite(this, SWT.BORDER, null, true);
		FormData fd_chartDisplay = new FormData();
		fd_chartDisplay.bottom = new FormAttachment(100);
		fd_chartDisplay.right = new FormAttachment(100);
		fd_chartDisplay.top = new FormAttachment(0);
		fd_chartDisplay.left = new FormAttachment(0);
		chartDisplay.setLayoutData(fd_chartDisplay);

	}

	public void setPVData(List<VMultiDouble> pvdata) {
		if (pvdata == null || pvdata.isEmpty()) {
			createNewChart();
		} else {
			chartDisplay.setRedraw(false);
			dataset.removeAllSeries();
			for (VMultiDouble vMultiDouble : pvdata) {
				XYSeries series = new XYSeries(vMultiDouble.toString(), false,
						false);
				double index = 0;
				long startTime = System.nanoTime();
				for (VDouble value : vMultiDouble.getValues()) {
					if (value != null)
						series.add(index, (double) value.getValue(), false);
					index++;
				}
				System.out.println("dataset created in "
						+ (System.nanoTime() - startTime) + " ns.");
				dataset.addSeries(series);
			}
			chartDisplay.setRedraw(true);
			chartDisplay.redraw();
		}
	}

	private void createNewChart() {
		generator = new Random();
		dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("PV Group1", false, true);
		dataset.addSeries(series);
		chart = ChartFactory.createXYLineChart("Title", "X Axis", "Y Axis",
				dataset, PlotOrientation.VERTICAL, true, true, true);
		chartDisplay.setChart(chart);
	}

	/**
	 * list which is a union of all the pvNames
	 * 
	 * @param pvNames
	 */
	public void setNames(List<String> pvNames) {
		if (pvNames != null) {
			this.pvNames = pvNames;
		} else {
			throw new IllegalArgumentException("pvNames cannot be null.");
		}
	}

	/**
	 * ordered List of all the z positions
	 * 
	 * @param positions
	 *            null permitted
	 */
	public void setPositions(List<Double> positions) {
		if (positions != null && positions.size() == this.pvNames.size()) {
			this.positions = positions;
		}

	}
}
