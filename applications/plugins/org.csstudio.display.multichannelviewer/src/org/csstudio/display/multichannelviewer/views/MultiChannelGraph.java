package org.csstudio.display.multichannelviewer.views;

import static org.csstudio.utility.channelfinder.ChannelQuery.Builder.query;
import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.util.TimeDuration.ms;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.csstudio.display.multichannelviewer.Activator;
import org.csstudio.utility.channelfinder.ChannelQuery;
import org.csstudio.utility.channelfinder.ChannelQueryListener;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.epics.pvmanager.ExpressionLanguage;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.ValueUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class MultiChannelGraph extends Composite {

	private volatile String queryString;
	private volatile List<Channel> channels;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private ChannelQuery channelQuery;

	PVReader<Map<String, Object>> pvReader;
	private volatile String sortProperty;
	private Comparator<Channel> comparator = new ChannelNameComparator();

	private ChartComposite chartDisplay;
	private volatile double maxYValue;
	private volatile double minYValue;

	private final ExecutorService executor = Executors
			.newSingleThreadExecutor();

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public MultiChannelGraph(Composite parent, int style) {
		super(parent, style);
		// Close PV on dispose
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pvReader != null) {
					pvReader.close();
					pvReader = null;
				}
				executor.shutdown(); // Disable new tasks from being submitted
				try {
					// Wait a while for existing tasks to terminate
					if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
						executor.shutdownNow(); // Cancel currently executing tasks
						// Wait a while for tasks to respond to being cancelled
						if (!executor.awaitTermination(60, TimeUnit.SECONDS))
							System.err.println("Pool did not terminate"); //$NON-NLS-1$
					}
				} catch (InterruptedException ie) {
					// (Re-)Cancel if current thread also interrupted
					executor.shutdownNow();
					// Preserve interrupt status
					Thread.currentThread().interrupt();
				}
			}
		});

		reconnect();
		addPropertyChangeListener(new PropertyChangeListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("channels")) {
					reconnect();
				} else if (evt.getPropertyName().equals("ordering")) {
					System.out.println("switching the ordering....");
					if (sortProperty.equals("channel-name")) {
						comparator = new ChannelNameComparator();
					} else {
						comparator = new ChannelPropertyComparator(sortProperty);
					}
					Collections.sort(channels, comparator);
					((XYPlot) chartDisplay.getChart().getPlot())
							.getDomainAxis().setLabel(
									"Channels sorted by " + sortProperty);
				}
			}
		});
		setLayout(new GridLayout(1, false));
		chartDisplay = new ChartComposite(this, SWT.NONE, null, false, false,
				false, false, true);
		chartDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		chartDisplay.setChart(createChart(null));
	}

	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createScatterPlot(
				"MultiChannel Viewer", "Channels sorted by " + sortProperty,
				"Value", null, PlotOrientation.VERTICAL, true, true, false);
		chart.removeLegend();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setAutoRangeIncludesZero(false);
		domainAxis.setTickLabelsVisible(false);
		return chart;
	}

	private void setXYDataset(final XYDataset xyDataset) {

		if (chartDisplay.getChart() != null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					XYPlot plot = (XYPlot) chartDisplay.getChart().getPlot();
					plot.getRangeAxis().setRange(minYValue, maxYValue);
					plot.setDataset(xyDataset);
					chartDisplay.redraw();
				}
			});
		}
	}

	public void reconnect() {
		if (pvReader != null) {
			pvReader.close();
		}
		if (channels != null) {
			Collections.sort(channels, comparator);
			pvReader = PVManager.read(
					mapOf(latestValueOf(ExpressionLanguage.channels(ChannelUtil
							.getChannelNames(channels))))).every(ms(25));
			pvReader.addPVReaderListener(new PVReaderListener() {
				public void pvChanged() {
					Map<String, Object> map = pvReader.getValue();
					updateGraph(Collections.unmodifiableList(channels), map);
				}

			});
		}
	}

	private void updateGraph(List<Channel> channels, Map<String, Object> map) {
		long time = System.nanoTime();
		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] data = new double[2][map.keySet().size()];
		int count = 0;
		for (Channel channel : channels) {
			if (sortProperty.equals("channel-name"))
				data[0][count] = count;
			else
				data[0][count] = Double.valueOf(channel.getProperty(
						sortProperty).getValue());
			double yValue = ValueUtil
					.numericValueOf(map.get(channel.getName()));
			maxYValue = yValue > maxYValue ? yValue : maxYValue;
			minYValue = yValue < minYValue ? yValue : minYValue;
			data[1][count] = yValue;
			count++;
		}
		dataset.addSeries("pvs", data);
		// System.out.println("time to compute dataset = "
		// + (System.nanoTime() - time) / 1000 + " micro seconds");
		setXYDataset(dataset);
	}

	public void setQueryString(String queryString) {
		String oldQuery = this.queryString;
		this.queryString = queryString;
		changeSupport.firePropertyChange("queryString", oldQuery, queryString);
		queryChannels();
	}

	public void queryChannels() {
		this.channelQuery = query(queryString).create();
		this.channelQuery.addChannelQueryListener(new ChannelQueryListener() {

			@Override
			public void getQueryResult() {
				final Exception e = channelQuery.getLastException();
				if (e == null) {
					setChannels(new ArrayList<Channel>(channelQuery.getResult()));
					System.out.println(channels.size());
				} else {
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {

								@Override
								public void run() {
									Status status = new Status(Status.ERROR,
											Activator.PLUGIN_ID,
											e.getMessage(), e.getCause());
									ErrorDialog.openError(getShell(),
											"Error retrieving channels",
											e.getMessage(), status);
								}
							});
				}
			}

		});
		this.channelQuery.execute();
	}

	public Collection<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		List<Channel> oldChannels = this.channels;
		this.channels = channels;
		changeSupport
				.firePropertyChange("channels", oldChannels, this.channels);
	}

	public String getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(String sortProperty) {
		String oldSortProperty = this.sortProperty;
		this.sortProperty = sortProperty;
		changeSupport.firePropertyChange("ordering", oldSortProperty,
				this.sortProperty);
	}
}
