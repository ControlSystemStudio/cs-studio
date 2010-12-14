package org.csstudio.multichannelviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.multichannelviewer.model.CSSChannelGroupPV;
import org.csstudio.multichannelviewer.model.IChannelGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VMultiDouble;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import static org.epics.pvmanager.util.TimeDuration.ms;
import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDouble;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;

public class ChannelsPlot extends ViewPart {

	private static int instance;

	public static final String ID = "org.csstudio.orbitViewer.channelsPlot";

	// GUI
	private GridLayout layout;
	private Button button1;
	private Button button2;
	private Button button3;

	private ChartComposite frame;

	// Model
	private IChannelGroup channels;
	private XYSeriesCollection dataset;
	private PV<VMultiDouble> pv1;
	private PV<VMultiDouble> pv2;
	private PV<VMultiDouble> pv3;

	public ChannelsPlot() {
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	@Override
	public void createPartControl(Composite parent) {
		layout = new GridLayout(3, true);
		parent.setLayout(layout);

		button1 = new Button(parent, SWT.PUSH);
		button1.setText("AddGroup1");
		button1.setToolTipText("add channels group");
		button1.setLayoutData(new GridData());
		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (pv1 != null) {
					pv1.close();
					dataset.removeSeries(dataset.getSeries("PV group1"));
				}
				List<String> pvNames = new ArrayList<String>();
				for (int i = 0; i < 200; i++) {
					pvNames.add("ramp(" + i + "," + (i+50) + ", 1, 0.1)");
				}
				pv1 = PVManager.read(
						synchronizedArrayOf(ms(75), vDoubles(Collections
								.unmodifiableList(pvNames)))).atHz(10);
				final XYSeries series1 = new XYSeries("PV Group1", false, true);
				dataset.addSeries(series1);
				pv1.addPVValueChangeListener(new PVValueChangeListener() {
					@Override
					public void pvValueChanged() {
						// get new series.
						updateXYSeriesForPV(pv1, series1);
					}
				});
			}
		});

		button2 = new Button(parent, SWT.PUSH);
		button2.setText("AddGroup2");
		button2.setToolTipText("add channels group");
		button2.setLayoutData(new GridData());
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (pv2 != null) {
					pv2.close();
					dataset.removeSeries(dataset.getSeries("PV group1"));
				}
				List<String> pvNames = new ArrayList<String>();
				for (int i = 200; i > 0; i--) {
					pvNames.add("ramp(" + i + "," + (i+50) + ", 1, 0.1)");
				}
				pv2 = PVManager.read(
						synchronizedArrayOf(ms(75), vDoubles(Collections
								.unmodifiableList(pvNames)))).atHz(10);
				final XYSeries series2 = new XYSeries("PV Group2", false, true);
				dataset.addSeries(series2);
				pv2.addPVValueChangeListener(new PVValueChangeListener() {
					@Override
					public void pvValueChanged() {
						// get new series.
						updateXYSeriesForPV(pv2, series2);
					}
				});
			}
		});

		button3 = new Button(parent, SWT.PUSH);
		button3.setText("AddGroup3");
		button3.setToolTipText("add channels group");
		button3.setLayoutData(new GridData());
		button3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				XYSeries series3 = new XYSeries("Series 3", false, true);
				series3.add(0, 1);
				series3.add(1, 1);
				series3.add(2, 1);
				dataset.addSeries(series3);
			}
		});

		dataset = new XYSeriesCollection();

		try {
			JFreeChart chart = ChartFactory.createXYLineChart("title",
					"x Axis", "y Axis", dataset, PlotOrientation.VERTICAL,
					true, true, true);
			XYPlot plot = (XYPlot) chart.getPlot();
//			XYItemRenderer xyItemRenderer = plot.getRenderer();
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setRange(0, 250);
			plot.setDomainGridlinesVisible(true);
			plot.setRangeGridlinesVisible(true);
			frame = new ChartComposite(parent, SWT.NONE, chart, true);
			// layout
			GridData frameGD = new GridData();
			frameGD.horizontalSpan = 3;
			frameGD.grabExcessHorizontalSpace = true;
			frameGD.grabExcessVerticalSpace = true;
			frameGD.horizontalAlignment = SWT.FILL;
			frameGD.verticalAlignment = SWT.FILL;
			frame.setLayoutData(frameGD);
			frame.pack();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {

	}

	public void addChannelsGroup(IChannelGroup channels) {

	}

	private void updateXYSeriesForPV(PV<VMultiDouble> pv, XYSeries series) {
		if (pv.getValue() != null) {
			double index = 0;
			series.clear();
			for (VDouble value : pv.getValue().getValues()) {
				if (value != null)
					series.add(index, (double)value.getValue(), false);
				index++;
			}
			series.fireSeriesChanged();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		if (pv1 != null)
			pv1.close();
		if (pv2 != null)
			pv2.close();
		if (pv3 != null)
			pv3.close();
		super.dispose();
	}

}
