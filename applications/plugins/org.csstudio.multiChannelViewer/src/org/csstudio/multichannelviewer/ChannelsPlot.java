package org.csstudio.multichannelviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.csstudio.multichannelviewer.model.CSSChannelGroupPV;
import org.csstudio.multichannelviewer.model.IChannelGroup;
import org.csstudio.utility.pvmanager.jfreechart.widgets.XYChartWidget;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class ChannelsPlot extends ViewPart {

	private static int instance;

	public static final String ID = "org.csstudio.orbitViewer.channelsPlot";
	private Button button1;
	private Button button2;
	private XYChartWidget chart;

	// Model
	private IChannelGroup channels;
	
	public ChannelsPlot() {
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());

		button1 = new Button(parent, SWT.PUSH);
		FormData fd_button1 = new FormData();
		fd_button1.top = new FormAttachment(0, 5);
		fd_button1.left = new FormAttachment(0, 5);
		button1.setLayoutData(fd_button1);
		button1.setText("AddGroup1-ramp");
		button1.setToolTipText("add channels group");
		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				List<String> pvNames = new ArrayList<String>();
				for (int i = 0; i < 200; i++) {
					pvNames.add("sim://ramp(" + i + "," + (i+50) + ", 1, 0.1)");
				}
				chart.setChannelNames(pvNames);
				chart.setChannelPositions(generatePositions(pvNames.size(), true));
			}
		});

		button2 = new Button(parent, SWT.PUSH);
		FormData fd_button2 = new FormData();
		fd_button2.top = new FormAttachment(0, 5);
		fd_button2.left = new FormAttachment(0, 116);
		button2.setLayoutData(fd_button2);
		button2.setText("AddGroup2-noise");
		button2.setToolTipText("add channels group");
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> pvNames = new ArrayList<String>();
				for (int i = 200; i > 0; i--) {
					pvNames.add("sim://noise(0,20,0.1)");
				}
				chart.setChannelNames(pvNames);
				chart.setChannelPositions(generatePositions(pvNames.size(), false));
			}
		});
		
		chart = new XYChartWidget(parent, SWT.NONE);
		FormData fd_chart = new FormData();
		fd_chart.bottom = new FormAttachment(100);
		fd_chart.top = new FormAttachment(button1, 6);
		fd_chart.left = new FormAttachment(button1, 0, SWT.LEFT);
		fd_chart.right = new FormAttachment(100);
		chart.setLayoutData(fd_chart);
	}

	protected List<Double> generatePositions(int size, boolean exponential) {
		List<Double> positions = new ArrayList<Double>();
		if(exponential){			
			double step = 1;
			for (double i = 0; i < size; i++) {
				positions.add(i+step);
				step=step*2;				
			}
		}else{
			Random generator = new Random();
			for (int i = 0; i < size; i++) {
				positions.add(generator.nextDouble());
			}
		}
		return positions;
	}

	@Override
	public void setFocus() {

	}

	public void addChannelsGroup(IChannelGroup channels) {

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		chart.dispose();
		super.dispose();
	}

}
