package org.csstudio.display.multichannelviewer.views;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.ms;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.csstudio.display.multichannelviewer.Activator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.epics.pvmanager.ExpressionLanguage;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.ValueUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

public class MultiChannelGraph extends Composite {

	public static final String CHANNEL_NAME_SORT = "channel-name";
	private static final Logger log = Logger.getLogger(MultiChannelGraph.class
			.getName());

	private volatile String queryString;
	private volatile List<Channel> channels;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);
	private ChannelQuery channelQuery;

	PVReader<Map<String, Object>> pvReader;
	private volatile String sortProperty = CHANNEL_NAME_SORT;
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
						executor.shutdownNow(); // Cancel currently executing
												// tasks
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

		setLayout(new GridLayout(1, false));
		chartDisplay = new ChartComposite(this, SWT.NONE, null, false, false,
				false, false, true);
		chartDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		chartDisplay.setChart(createChart(null));

		// reconnect();
		addPropertyChangeListener(new PropertyChangeListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("channels")) {
					reconnect();
				} else if (evt.getPropertyName().equals("ordering")) {
					log.info("switching the ordering....");
					if (sortProperty.equals(CHANNEL_NAME_SORT)) {
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
	}

	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createScatterPlot(
				null, "", "Value", null,
				PlotOrientation.VERTICAL, true, true, false);
		chart.removeLegend();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);
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
					plot.getRangeAxis().setRange(minYValue-(0.05*minYValue), maxYValue+(0.05*maxYValue));
					plot.setDataset(xyDataset);
					if (xyDataset != null)
						plot.getDomainAxis().setLabel(
								"Channels ordered by : " + sortProperty);
					else
						plot.getDomainAxis().setLabel("");
					//TODO performance issues
					chartDisplay.redraw();
				}
			});
		}
	}

	public void reconnect() {
		if (pvReader != null) {
			pvReader.close();
			pvReader = null;
		}
		if (channels != null && channels.size() != 0) {
			Collections.sort(channels, comparator);
			pvReader = PVManager.read(
					mapOf(latestValueOf(ExpressionLanguage.channels(ChannelUtil
							.getChannelNames(channels))))).every(ms(50));
			pvReader.addPVReaderListener(new PVReaderListener() {
				public void pvChanged() {
					Map<String, Object> map = pvReader.getValue();
					updateGraph(Collections.unmodifiableList(channels), map);
				}

			});
		} else {
			updateGraph(null, null);
		}
	}

	private void updateGraph(List<Channel> channels, Map<String, Object> map) {
//		long time = System.nanoTime();
		if (channels != null && map != null) {
			DefaultXYDataset dataset = new DefaultXYDataset();
			ArrayList<XYDataItem> dataItems = new ArrayList<XYDataItem>();
			int count = 0;
			for (Channel channel : channels) {
				double xValue;
				if (sortProperty.equals(CHANNEL_NAME_SORT))
					xValue = count;
				else
					xValue = Double.valueOf(channel.getProperty(
							sortProperty).getValue());
				Object value = map.get(channel.getName());
				if(ValueUtil.typeOf(value).equals(VDoubleArray.class)){
					double[] values = ((VDoubleArray) value).getArray();
					for (double d : values) {
						double yValue = d;
						maxYValue = yValue > maxYValue ? yValue : maxYValue;
						minYValue = yValue < minYValue ? yValue : minYValue;
						dataItems.add(new XYDataItem(xValue, yValue));
					}
				}else{
					double yValue = ValueUtil.numericValueOf(map.get(channel
							.getName()));
					maxYValue = yValue > maxYValue ? yValue : maxYValue;
					minYValue = yValue < minYValue ? yValue : minYValue;
					dataItems.add(new XYDataItem(xValue, yValue));
				}
				count++;
			}
			count = 0;
			double[][] data = new double[2][dataItems.size()];
			for (XYDataItem xyDataItem : dataItems) {
				data[0][count] = xyDataItem.getXValue();
				data[1][count] = xyDataItem.getYValue();
				count++;
			}
			dataset.addSeries("pvs", data);
//			 System.out.println("time to compute dataset = "
//			 + (System.nanoTime() - time) / 1000 + " micro seconds");
			setXYDataset(dataset);
		} else {
			setXYDataset(null);
		}

	}

	public void setQueryString(String queryString) {
		String oldQuery = this.queryString;
		this.queryString = queryString;
		changeSupport.firePropertyChange("queryString", oldQuery, queryString);
		queryChannels();
	}

	public void queryChannels() {
		this.channelQuery = ChannelQuery.query(queryString).build();
		this.channelQuery.execute(new ChannelQueryListener() {

			@Override
			public void queryExecuted(Result result) {
				final Exception e = result.exception;
				if (e == null) {
					setChannels(new ArrayList<Channel>(result.channels));
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
