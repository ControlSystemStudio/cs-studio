/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;
import static org.epics.pvmanager.util.TimeDuration.ms;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VMultiDouble;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author shroffk
 * 
 */
public class XYChartWidget extends Composite {

	private ChartComposite chartDisplay;
	private JFreeChart chart;
	private XYSeriesCollection dataset;

	public XYChartWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		chartDisplay = new ChartComposite(this, SWT.BORDER, null, true);
		FormData fd_chartDisplay = new FormData();
		fd_chartDisplay.bottom = new FormAttachment(100);
		fd_chartDisplay.right = new FormAttachment(100);
		fd_chartDisplay.top = new FormAttachment(0);
		fd_chartDisplay.left = new FormAttachment(0, 0);
		chartDisplay.setLayoutData(fd_chartDisplay);
	}

	// The pv name for connection
	private List<? extends String> pvNames;
	// The pv created by pvmanager
	private PV<VMultiDouble> pv;

	public List<? extends String> getPvName() {
		return pvNames;
	}

	public void setPvName(List<? extends String> pvNames) {
		this.pvNames = pvNames;
		reconnect();
	}

	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}

		// Clean up old chart if present
		chartDisplay.setChart(null);
		// Create an empty chart
		dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("PV Group1", false, true);
		dataset.addSeries(series);
		chart = ChartFactory.createXYLineChart("Multi Channel Plot",
				"PV Channels", "Values", dataset, PlotOrientation.VERTICAL,
				true, true, true);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setBackgroundPaint(Color.white);

		chartDisplay.setChart(chart);
		chartDisplay.pack();

		if (pvNames != null) {
			final Random generator = new Random();
			pv = PVManager.read(
					synchronizedArrayOf(ms(75),
							vDoubles(Collections.unmodifiableList(pvNames))))
					.atHz(10);
			pv.addPVValueChangeListener(new PVValueChangeListener() {

				@Override
				public void pvValueChanged() {
					if (!chartDisplay.isDisposed()) {
						XYSeries series = new XYSeries("PV Group1", false, true);
						double index = 0;
						for (VDouble value : pv.getValue().getValues()) {
							if (value != null)
								series.add(index, (double) value.getValue(),
										false);
							index++;
						}
						PlatformUI
								.getWorkbench()
								.getDisplay()
								.asyncExec(
										new UpdateXYSeriesCollection(chartDisplay, dataset,
												series));
					}
				}
			});
		}
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		if (pv != null) {
			pv.close();
		}
		super.dispose();
	}

}
