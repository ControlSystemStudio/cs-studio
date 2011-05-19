package org.csstudio.multichannelviewer;

import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.utility.pvmanager.jfreechart.widgets.XYChartWidget;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

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

		channels.addEventListListener(new ListEventListener<Channel>() {

			@Override
			public void listChanged(ListEvent<Channel> listChanges) {
				chart.setXAxisLabel("Channels sorted by "
						+ channels.getComparator().toString());
				List<String> pvNames = new ArrayList<String>();
				for (Channel channel : channels.getList()) {
					// pvNames.add(channel.getName());
					pvNames.add("sim://gaussian(50, 20, 0.1)");
				}
				chart.setChannelNames(pvNames);
				chart.setChannelPositions(generatePositions(pvNames.size(),
						true));
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

	// PV<VMultiDouble> getChannelGroupPV() {
	// return this.pv;
	// }

	/** {@inheritDoc} */
	@Override
	public void dispose() {
		chart.dispose();
		super.dispose();
	}

	/**
	 * generate the positions for the test data
	 * 
	 * @param size
	 * @param exponential
	 * @return
	 */
	protected List<Double> generatePositions(int size, boolean gaussian) {
		Random generator = new Random();
		Set<Double> positions = new TreeSet<Double>();
		for (int i = 0; i < size; i++) {
			if (gaussian) {
				// get the range, casting to long to avoid overflow problems
				Double range =(double) ((size * 10) + 1);
				// compute a fraction of the range, 0 <= frac < range
				Double fraction = (Double) (range * generator.nextGaussian());
				positions.add((Double) (fraction + 0));
			} else {
				positions.add(generator.nextDouble());
			}
		}
		return new ArrayList<Double>(positions);
	}
}
