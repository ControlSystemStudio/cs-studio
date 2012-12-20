package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vNumber;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleOf;
import static org.epics.util.time.TimeDuration.ofHertz;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.RangeListener;
import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraphRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.LineGraphPlot;
import org.epics.pvmanager.graphene.Plot2DResult;
import org.epics.pvmanager.graphene.PlotDataRange;
import org.epics.pvmanager.util.TimeDuration;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class Line2DPlotWidget extends AbstractChannelQueryResultWidget
		implements ISelectionProvider, ConfigurableWidget {

	private VImageDisplay imageDisplay;
	private LineGraphPlot plot;
	private ErrorBar errorBar;
	private boolean showRange;
	private StartEndRangeWidget yRangeControl;
	private StartEndRangeWidget xRangeControl;

	public Line2DPlotWidget(Composite parent, int style) {
		super(parent, style);

		// Close PV on dispose
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pv != null) {
					pv.close();
					pv = null;
				}
			}
		});

		setLayout(new FormLayout());

		errorBar = new ErrorBar(this, SWT.NONE);
		FormData fd_errorBar = new FormData();
		fd_errorBar.left = new FormAttachment(0, 2);
		fd_errorBar.right = new FormAttachment(100, -2);
		fd_errorBar.top = new FormAttachment(0, 2);
		errorBar.setLayoutData(fd_errorBar);

		errorBar.setMarginBottom(5);

		yRangeControl = new StartEndRangeWidget(this, SWT.NONE);
		FormData fd_yRangeControl = new FormData();
		fd_yRangeControl.top = new FormAttachment(errorBar, 2);
		fd_yRangeControl.left = new FormAttachment(0, 2);
		fd_yRangeControl.bottom = new FormAttachment(100, -15);
		fd_yRangeControl.right = new FormAttachment(0, 13);
		yRangeControl.setLayoutData(fd_yRangeControl);
		yRangeControl.setOrientation(ORIENTATION.VERTICAL);
		yRangeControl.addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				if (plot != null) {
					double invert = yRangeControl.getMin()
							+ yRangeControl.getMax();
					plot.update(new LineGraphRendererUpdate()
							.rangeFromDataset(false)
							.startY((invert - yRangeControl.getSelectedMax()))
							.endY((invert - yRangeControl.getSelectedMin())));
				}
			}
		});

		imageDisplay = new VImageDisplay(this);
		FormData fd_imageDisplay = new FormData();
		fd_imageDisplay.top = new FormAttachment(errorBar, 2);
		fd_imageDisplay.right = new FormAttachment(100, -2);
		fd_imageDisplay.left = new FormAttachment(yRangeControl, 2);
		imageDisplay.setLayoutData(fd_imageDisplay);
		imageDisplay.setStretched(SWT.HORIZONTAL);

		imageDisplay.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				if (plot != null) {
					plot.update(new LineGraphRendererUpdate()
							.imageHeight(imageDisplay.getSize().y)
							.imageWidth(imageDisplay.getSize().x)
							.interpolation(InterpolationScheme.LINEAR));
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do
			}
		});

		xRangeControl = new StartEndRangeWidget(this, SWT.NONE);
		fd_imageDisplay.bottom = new FormAttachment(xRangeControl, -2);
		FormData fd_xRangeControl = new FormData();
		fd_xRangeControl.left = new FormAttachment(0, 15);
		fd_xRangeControl.top = new FormAttachment(100, -13);
		fd_xRangeControl.right = new FormAttachment(100, -2);
		fd_xRangeControl.bottom = new FormAttachment(100, -2);
		xRangeControl.setLayoutData(fd_xRangeControl);
		xRangeControl.addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				if (plot != null) {
					plot.update(new LineGraphRendererUpdate()
							.rangeFromDataset(false)
							.startX(xRangeControl.getSelectedMin())
							.endX(xRangeControl.getSelectedMax()));
				}
			}
		});

		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName());

			}
		});

	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		imageDisplay.setMenu(menu);
	}

	@Override
	protected void queryCleared() {
		setLastError(null);
		setYChannelNames(null);
		imageDisplay.setVImage(null);
	}

	private Result result;

	private List<String> getResultChannels(Result result) {
		if (result == null)
			return null;

		setLastError(result.exception);
		this.result = result;
		List<String> channelNames = null;
		Exception ex = result.exception;
		if (ex == null) {
			Collection<Channel> channels = result.channels;
			if (channels != null && !channels.isEmpty()) {
				// Sort if you can
				try {
					List<Channel> sortedChannels = new ArrayList<Channel>(
							channels);

					Collections.sort(sortedChannels, new Comparator<Channel>() {
						@Override
						public int compare(Channel o1, Channel o2) {
							return findProperty(o1).compareTo(findProperty(o2));
						}

						public Double findProperty(Channel channel) {
							for (Property property : channel.getProperties()) {
								if (property.getName()
										.equals(getSortProperty())) {
									return Double.parseDouble(property
											.getValue());
								}
							}
							return null;
						}
					});
					channels = sortedChannels;
				} catch (Exception e) {
					// Leave unsorted
				}

				channelNames = new ArrayList<String>();
				for (Channel channel : channels) {
					channelNames.add(channel.getName());
				}
			}
		}

		final List<String> finalChannels = channelNames;
		return finalChannels;
	}

	@Override
	protected void queryExecuted(Result result) {
		List<String> finalChannels = getResultChannels(result);
		if (finalChannels != null && !finalChannels.isEmpty()) {
			setYChannelNames(finalChannels);
			setProperties(ChannelUtil.getPropertyNames(result.channels));
		} else if (finalChannels == null) {
			// assumes the entered string to be an waveform pv
			setyWaveformChannelName(getChannelQuery().getQuery());
		}
	}

	private PVReader<Plot2DResult> pv;
	// Y values
	private Collection<String> yChannelNames;
	private String yWaveformChannelName;

	private enum YAxis {
		CHANNELQUERY, WAVEFORM
	}

	private YAxis yOrdering = YAxis.CHANNELQUERY;

	// X values
	private ChannelQuery xChannelQuery;
	private Collection<String> xChannelNames;
	private String xWaveformChannelName;

	private String sortProperty;
	private Collection<String> properties;

	private String offset;
	private String increment;

	public enum XAxis {
		INDEX, CHANNELQUERY, PROPERTY, OFFSET_INCREMENT
	}

	private XAxis xOrdering = XAxis.INDEX;

	public void setxOrdering(XAxis xAxis) {
		this.xOrdering = xAxis;
		reconnect();
	}

	public XAxis getxOrdering() {
		return xOrdering;
	}

	public Collection<String> getxChannelNames() {
		return xChannelNames;
	}

	public void setxChannelNames(Collection<String> xChannelNames) {
		if (this.xChannelNames != null
				&& this.xChannelNames.equals(xChannelNames)) {
			return;
		}
		this.xChannelNames = xChannelNames;
		this.xWaveformChannelName = null;
		reconnect();
	}

	public String getxWaveformChannelName() {
		return xWaveformChannelName;
	}

	public void setxWaveformChannelName(String xWaveformChannelName) {
		if (this.xWaveformChannelName != null
				&& this.xWaveformChannelName.equals(xWaveformChannelName)) {
			return;
		}
		this.xWaveformChannelName = xWaveformChannelName;
		this.xChannelNames = null;
		reconnect();
	}

	public Collection<String> getYChannelNames(List<String> finalChannels) {
		return this.yChannelNames;
	}

	public void setYChannelNames(List<String> yChannelNames) {
		if (this.yChannelNames != null
				&& this.yChannelNames.equals(yChannelNames)) {
			return;
		}
		this.yChannelNames = yChannelNames;
		this.yWaveformChannelName = null;
		this.yOrdering = YAxis.CHANNELQUERY;
		reconnect();
	}

	public String getyWaveformChannelName() {
		return yWaveformChannelName;
	}

	public void setyWaveformChannelName(String yWaveformChannelName) {
		if (this.yWaveformChannelName != null
				&& this.yWaveformChannelName.equals(yWaveformChannelName)) {
			return;
		}
		this.yWaveformChannelName = yWaveformChannelName;
		this.yChannelNames = null;
		this.yOrdering = YAxis.WAVEFORM;
		reconnect();
	}

	public ChannelQuery getXChannelQuery() {
		return xChannelQuery;
	}

	public void setXChannelQuery(ChannelQuery xChannelQuery) {
		// If new query is the same, don't change -- you would re-trigger the
		// query for nothing
		if (getXChannelQuery() != null
				&& getXChannelQuery().equals(xChannelQuery)) {
			return;
		}

		ChannelQuery oldValue = getXChannelQuery();
		if (oldValue != null) {
			oldValue.removeChannelQueryListener(xQueryListener);
		}
		xChannelQueryCleared();
		if (xChannelQuery != null) {
			xChannelQuery.execute(xQueryListener);
		}

		if (getXChannelQuery() == null && xChannelQuery == null)
			return;

		ChannelQuery oldXValue = this.xChannelQuery;
		this.xChannelQuery = xChannelQuery;
	}

	private final ChannelQueryListener xQueryListener = new ChannelQueryListener() {

		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {

				@Override
				public void run() {
					xChannelQueryExecuted(result);
				}

			});

		}
	};

	private void xChannelQueryExecuted(Result result) {
		List<String> finalChannels = getResultChannels(result);
		if (finalChannels != null && !finalChannels.isEmpty()) {
			setxChannelNames(finalChannels);
		} else if (finalChannels == null) {
			// assumes the entered string to be an waveform pv
			setxWaveformChannelName(getXChannelQuery().getQuery());
		}
	}

	private void xChannelQueryCleared() {
		setLastError(null);
		setxChannelNames(null);
		imageDisplay.setVImage(null);
		// reconnect();
	}

	public String getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(String sortProperty) {
		if (sortProperty != null) {
			this.sortProperty = sortProperty;
			reconnect();
		}
	}

	public Collection<String> getProperties() {
		if (this.properties == null)
			return Collections.emptyList();
		return properties;
	}

	private void setProperties(Collection<String> properties) {
		this.properties = properties;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		if (offset != null && !offset.isEmpty()) {
			this.offset = offset;
			reconnect();
		}
	}

	public String getIncrement() {
		return increment;
	}

	public void setIncrement(String increment) {
		if (increment != null && !increment.isEmpty()) {
			this.increment = increment;
			reconnect();
		}
	}

	private void setLastError(Exception lastException) {
		errorBar.setException(lastException);
	}

	private void reconnect() {
		if (pv != null) {
			pv.close();
			imageDisplay.setVImage(null);
			plot = null;
			resetRange(xRangeControl);
			resetRange(yRangeControl);
		}

		if ((yChannelNames == null || yChannelNames.isEmpty())
				&& yWaveformChannelName == null) {
			return;
		}

		DesiredRateExpression<VDoubleArray> yValueExpression = null;
		// Determine the expression for the y values.
		if (yOrdering.equals(YAxis.CHANNELQUERY)) {
			if (yChannelNames != null)
				yValueExpression = vDoubleArrayOf(latestValueOf(channels(
						yChannelNames, VNumber.class, VNumber.class)));
		} else if (yOrdering.equals(YAxis.WAVEFORM)) {
			if (yWaveformChannelName != null && !yWaveformChannelName.isEmpty())
				yValueExpression = latestValueOf(vDoubleArrayOf(channel(yWaveformChannelName)));
		}

		if (yValueExpression != null) {
			DesiredRateExpression<VDoubleArray> xValueExpression = null;
			// Determine the expression for the x values.
			switch (xOrdering) {
			case INDEX:
				plot = ExpressionLanguage.lineGraphOf(yValueExpression);
				break;
			case CHANNELQUERY:
				if (xChannelNames != null && !xChannelNames.isEmpty()) {
					xValueExpression = vDoubleArrayOf(latestValueOf(channels(
							xChannelNames, VNumber.class, VNumber.class)));
					plot = ExpressionLanguage.lineGraphOf(xValueExpression,
							yValueExpression);
				} else if (xWaveformChannelName != null
						&& !xWaveformChannelName.isEmpty()) {
					xValueExpression = latestValueOf(vDoubleArrayOf(channel(xWaveformChannelName)));
					plot = ExpressionLanguage.lineGraphOf(xValueExpression,
							yValueExpression);
				} else {
					plot = null;
				}
				break;
			case OFFSET_INCREMENT:
				if (offset != null && increment != null) {
					plot = ExpressionLanguage.lineGraphOf(yValueExpression,
							latestValueOf(vNumber(offset)),
							latestValueOf(vNumber(increment)));
				} else {
					plot = ExpressionLanguage.lineGraphOf(yValueExpression);
				}
				break;
			default:
				plot = ExpressionLanguage.lineGraphOf(yValueExpression);
				break;
			}
		}
		if (plot == null) {
			return;
		}
		plot.update(new LineGraphRendererUpdate()
				.imageHeight(imageDisplay.getSize().y)
				.imageWidth(imageDisplay.getSize().x)
				.interpolation(InterpolationScheme.LINEAR));
		pv = PVManager.read(plot).notifyOn(SWTUtil.swtThread())
				.maxRate(ofHertz(50));
		pv.addPVReaderListener(new PVReaderListener() {

			@Override
			public void pvChanged() {
				Exception ex = pv.lastException();

				if (ex != null) {
					setLastError(ex);
				}
				if (pv.getValue() != null) {
					setRange(xRangeControl, pv.getValue().getxRange());
					setRange(yRangeControl, pv.getValue().getyRange());
					imageDisplay.setVImage(pv.getValue().getImage());
				} else {
					imageDisplay.setVImage(null);
				}
			}

		});
	}

	/**
	 * A helper function to set all the appropriate
	 * 
	 * @param control
	 */
	private void setRange(StartEndRangeWidget control,
			PlotDataRange plotDataRange) {
		control.setRange(plotDataRange.getStartIntegratedDataRange(),
				plotDataRange.getEndIntegratedDataRange());
	}

	private void resetRange(StartEndRangeWidget control) {
		control.setRanges(0, 0, 1, 1);
	}

	private Map<ISelectionChangedListener, PropertyChangeListener> listenerMap = new HashMap<ISelectionChangedListener, PropertyChangeListener>();

	@Override
	public void addSelectionChangedListener(
			final ISelectionChangedListener listener) {
		PropertyChangeListener propListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName()))
					listener.selectionChanged(new SelectionChangedEvent(
							Line2DPlotWidget.this, getSelection()));
			}
		};
		listenerMap.put(listener, propListener);
		addPropertyChangeListener(propListener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(new Line2DPlotSelection(
				getChannelQuery(), this));
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		removePropertyChangeListener(listenerMap.remove(listener));
	}

	@Override
	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private boolean configurable = true;

	private Line2DPlotConfigurationDialog dialog;

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}

	@Override
	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new Line2DPlotConfigurationDialog(this);
		dialog.open();
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}

	/** Memento tag */
	private static final String MEMENTO_CHANNEL_QUERY = "channelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_SORT_PROPERTY = "sortProperty"; //$NON-NLS-1$
	private static final String MEMENTO_OFFSET = "offset"; //$NON-NLS-1$
	private static final String MEMENTO_INCREMENT = "increment"; //$NON-NLS-1$

	public void saveState(IMemento memento) {
		if (getChannelQuery() != null) {
			memento.putString(MEMENTO_CHANNEL_QUERY, getChannelQuery()
					.getQuery());
		}
		if (getSortProperty() != null) {
			memento.putString(MEMENTO_SORT_PROPERTY, getSortProperty());
		}
		if (getOffset() != null) {
			memento.putString(MEMENTO_OFFSET, getOffset());
		}
		if (getIncrement() != null) {
			memento.putString(MEMENTO_INCREMENT, getIncrement());
		}
	}

	public void loadState(IMemento memento) {
		if (memento != null) {
			if (memento.getString(MEMENTO_SORT_PROPERTY) != null) {
				setSortProperty(memento.getString(MEMENTO_SORT_PROPERTY));
			}
			if (memento.getString(MEMENTO_CHANNEL_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(
						memento.getString(MEMENTO_CHANNEL_QUERY)).build());
			}
			if (memento.getString(MEMENTO_OFFSET) != null) {
				setOffset(memento.getString(MEMENTO_OFFSET));
			}
			if (memento.getString(MEMENTO_INCREMENT) != null) {
				setIncrement(memento.getString(MEMENTO_INCREMENT));
			}
		}
	}
}
