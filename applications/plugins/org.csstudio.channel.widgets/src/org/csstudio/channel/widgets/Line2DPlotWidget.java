package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.util.time.TimeDuration.ofHertz;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQueryListener;
import gov.bnl.channelfinder.api.ChannelQuery.Result;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.ui.util.widgets.ErrorBar;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraphRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.expression.ChannelExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.LineGraphPlot;

public class Line2DPlotWidget extends AbstractChannelQueryResultWidget
		implements ISelectionProvider, ConfigurableWidget {

	private VImageDisplay imageDisplay;
	private LineGraphPlot plot;
	private ErrorBar errorBar;

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

		setLayout(new GridLayout(1, false));

		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		errorBar.setMarginBottom(5);

		imageDisplay = new VImageDisplay(this);
		imageDisplay.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				true, 1, 1));
		imageDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
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

		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName());

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
		setYChannelNames(null);
		setxChannelNames(null);

		imageDisplay.setVImage(null);
		setLastError(null);
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
		} else if (finalChannels == null) {
			// assumes the entered string to be an waveform pv
			setyWaveformChannelName(getChannelQuery().getQuery());
		}
	}

	private PVReader<VImage> pv;
	// Y values
	private Collection<String> yChannelNames;
	private String yWaveformChannelName;

	// X values
	private ChannelQuery xChannelQuery;
	private Collection<String> xChannelNames;
	private String xWaveformChannelName;

	public Collection<String> getxChannelNames() {
		return xChannelNames;
	}

	public void setxChannelNames(Collection<String> xChannelNames) {
		if (this.xChannelNames != null
				&& this.xChannelNames.equals(xChannelNames)) {
			return;
		}
		this.xWaveformChannelName = null;
		this.xChannelNames = xChannelNames;
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

		this.xChannelNames = null;
		this.xWaveformChannelName = xWaveformChannelName;
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
		this.yWaveformChannelName = null;
		this.yChannelNames = yChannelNames;
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
		this.yChannelNames = null;
		this.yWaveformChannelName = yWaveformChannelName;
		reconnect();
	}

	public ChannelQuery getXChannelQuery() {
		return xChannelQuery;
	}

	public void setXChannelQuery(ChannelQuery xChannelQuery) {
		// If new query is the same, don't change -- you would re-trigger the
		// query for nothing
		if (getXChannelQuery() != null
				&& getXChannelQuery().equals(xChannelQuery))
			return;

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
		changeSupport.firePropertyChange("xChannelQuery", oldXValue,
				xChannelQuery);
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
		}else if (finalChannels == null) {
			// assumes the entered string to be an waveform pv
			setxWaveformChannelName(getXChannelQuery().getQuery());
		}
	}

	private void xChannelQueryCleared() {
		setxChannelNames(null);
		imageDisplay.setVImage(null);
		setLastError(null);
		reconnect();
	}

	private void setLastError(Exception lastException) {
		errorBar.setException(lastException);
	}

	private void reconnect() {
		if (pv != null) {
			pv.close();
			imageDisplay.setVImage(null);
			plot = null;
		}

		if ((yChannelNames == null || yChannelNames.isEmpty())
				&& yWaveformChannelName == null) {
			return;
		}

		DesiredRateExpression<VDoubleArray> yValueExpression = null;
		// Determine the expression for the y values.
		if (yChannelNames != null && !yChannelNames.isEmpty()) {
			yValueExpression = vDoubleArrayOf(latestValueOf(channels(
					yChannelNames, VNumber.class, VNumber.class)));
		} else if (yWaveformChannelName != null) {
			// create a plot using the yWavefor
			yValueExpression = latestValueOf(vDoubleArrayOf(channel(yWaveformChannelName)));
		}

		DesiredRateExpression<VDoubleArray> xValueExpression = null;
		// Determine the expression for the x values.
		if (xChannelNames != null && !xChannelNames.isEmpty()) {
			xValueExpression = vDoubleArrayOf(latestValueOf(channels(
					xChannelNames, VNumber.class, VNumber.class)));
		} else if (xWaveformChannelName != null) {
			xValueExpression = latestValueOf(vDoubleArrayOf(channel(xWaveformChannelName)));
		}

		if (xValueExpression == null) {
			// create a simple plot using the yExpression alone
			plot = ExpressionLanguage.lineGraphOf(yValueExpression);
		} else {
			// create a graph using both the expressions
			plot = ExpressionLanguage.lineGraphOf(xValueExpression,
					yValueExpression);
		}

		if (plot == null) {
			plot = ExpressionLanguage
					.lineGraphOf(vDoubleArrayOf(latestValueOf(channels(
							yChannelNames, VNumber.class, VNumber.class))));
		}

		plot.update(new LineGraphRendererUpdate()
				.imageHeight(imageDisplay.getSize().y)
				.imageWidth(imageDisplay.getSize().x)
				.interpolation(InterpolationScheme.LINEAR));
		pv = PVManager.read(plot).maxRate(ofHertz(50));
		pv.addPVReaderListener(new PVReaderListener() {

			@Override
			public void pvChanged() {
				if (pv.lastException() != null)
					setLastError(pv.lastException());
				if (pv.getValue() != null) {
					getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							imageDisplay.setVImage(pv.getValue());
						}
					});
				}
			}

		});
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

}
