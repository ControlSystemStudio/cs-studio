package org.csstudio.multichannelviewer;

import static org.epics.pvmanager.util.TimeDuration.ms;
import static org.epics.pvmanager.data.ExpressionLanguage.synchronizedArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.multichannelviewer.model.CSSChannelGroupPV;
import org.csstudio.utility.channel.ICSSChannel;
import org.csstudio.utility.pvmanager.jfreechart.widgets.XYChartWidget;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class MultiChannelPlot extends EditorPart {
	public MultiChannelPlot() {
	}

	//
	public static final String EDITOR_ID = "org.csstudio.multiChannelViewer.plot";

	// Model
	private CSSChannelGroup channels;
	private XYChartWidget chart;

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
		parent.setLayout(new FormLayout());
		chart = new XYChartWidget(parent, SWT.NONE);
		FormData fd_chart = new FormData();
		fd_chart.bottom = new FormAttachment(100);
		fd_chart.right = new FormAttachment(100);
		fd_chart.top = new FormAttachment(0);
		fd_chart.left = new FormAttachment(0);
		chart.setLayoutData(fd_chart);
		
		chart.setTitle("MultiChannel Plot");
		chart.setYAxisLabel("PV Value");

		channels.addEventListListener(new ListEventListener<ICSSChannel>() {

			@Override
			public void listChanged(ListEvent<ICSSChannel> listChanges) {
				chart.setXAxisLabel("Channels sorted by "
						+ channels.getComparator().toString());
				List<String> pvNames = new ArrayList<String>();
				for (ICSSChannel channel : channels.getList()) {
					// pvNames.add(channel.getChannel().getName());
					pvNames.add("sim://gaussian(50, 20, 0.1)");
				}
				chart.setChannelNames(pvNames);
				chart.setChannelPositions(generatePositions(pvNames.size(),true));				
			}

		});
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

//	PV<VMultiDouble> getChannelGroupPV() {
//		return this.pv;
//	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		chart.dispose();
		super.dispose();
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
}
