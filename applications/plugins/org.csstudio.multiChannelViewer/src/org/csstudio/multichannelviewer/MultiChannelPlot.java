package org.csstudio.multichannelviewer;

import static org.epics.pvmanager.util.TimeDuration.ms;
import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.multichannelviewer.model.CSSChannelGroupPV;
import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VMultiDouble;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

public class MultiChannelPlot extends EditorPart {

	//
	public static final String EDITOR_ID = "org.csstudio.multiChannelViewer.plot";

	// Model
	private CSSChannelGroup channels;
	private PV<VMultiDouble> pv;

	// GUI
	private GridLayout layout;
	private ChartComposite frame;
	private JFreeChart chart;

	private XYSeriesCollection dataset;

	//
	// public MultiChannelPlot() {
	//
	// }

	@Override
	public void createPartControl(Composite parent) {
		createChart(parent);
	}

	private void createChart(Composite parent) {
		channels = new CSSChannelGroup("test group of channels");

		// TODO Auto-generated method stub
		layout = new GridLayout(1, true);
		parent.setLayout(layout);

		dataset = new XYSeriesCollection();
		try {
			chart = ChartFactory.createXYLineChart(
					"Multi Channel Plot", "PV Channels", "Values", dataset,
					PlotOrientation.VERTICAL, true, true, true);
			XYPlot plot = (XYPlot) chart.getPlot();
//			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//			rangeAxis.setRange(0, 250);
			plot.setDomainGridlinesVisible(false);
			plot.setRangeGridlinesVisible(false);
			plot.setBackgroundPaint(Color.white);
			frame = new ChartComposite(parent, SWT.NONE, chart, true);
			// layout
			GridData frameGD = new GridData();
			frameGD.horizontalSpan = 1;
			frameGD.grabExcessHorizontalSpace = true;
			frameGD.grabExcessVerticalSpace = true;
			frameGD.horizontalAlignment = SWT.FILL;
			frameGD.verticalAlignment = SWT.FILL;
			frame.setLayoutData(frameGD);
			frame.pack();

		} catch (Exception e) {
			e.printStackTrace();
		}
		channels.addEventListListener(new ListEventListener<ICSSChannel>() {

			@Override
			public void listChanged(ListEvent<ICSSChannel> listChanges) {
				if (pv != null) {
					pv.close();					
					dataset.removeSeries(dataset.getSeries("PV Group1"));
				}
				ValueAxis rangeAxis = (ValueAxis) ((XYPlot) chart.getPlot()).getDomainAxis();
				rangeAxis.setLabel("Channels sorted by "+channels.getComparator().toString());
				List<String> pvNames = new ArrayList<String>();
				for (ICSSChannel channel : channels.getList()) {	
//					pvNames.add(channel.getChannel().getName());
					pvNames.add("sim://gaussian(50, 20, 0.1)");
				}				
				pv = PVManager.read(
						synchronizedArrayOf(ms(75), vDoubles(Collections
								.unmodifiableList(pvNames)))).atHz(10);
				final XYSeries series = new XYSeries("PV Group1", false, true);
				dataset.addSeries(series);
				pv.addPVValueChangeListener(new PVValueChangeListener() {
					@Override
					public void pvValueChanged() {
						// replace old series with new
						updateXYSeriesForPV(pv, series);
					}
				});
			}

		});
	}

	/**
	 * updates the series to represent the new values for the pv
	 * 
	 * @param pv
	 * @param series
	 */
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
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		// Update the editor's name from "OrbitViewer" to file name
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public static MultiChannelPlot createInstance() {
		final MultiChannelPlot editor;
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			editor = (MultiChannelPlot) page.openEditor(new EmptyEditorInput(),
					EDITOR_ID);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return editor;
	}

	public CSSChannelGroup getCSSChannelGroup() {
		return this.channels;
	}

	PV<VMultiDouble> getChannelGroupPV() {
		return this.pv;
	}

	 /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        if (pv != null)
        {
            pv.close();
        }
        super.dispose();
    }

}
