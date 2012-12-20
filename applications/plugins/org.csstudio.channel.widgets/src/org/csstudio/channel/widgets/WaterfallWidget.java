package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubles;
import static org.epics.pvmanager.extra.ExpressionLanguage.waterfallPlotOf;
import static org.epics.pvmanager.extra.WaterfallPlotParameters.adaptiveRange;
import static org.epics.pvmanager.extra.WaterfallPlotParameters.colorScheme;
import static org.epics.pvmanager.extra.WaterfallPlotParameters.pixelDuration;
import static org.epics.pvmanager.extra.WaterfallPlotParameters.scrollDown;
import static org.epics.pvmanager.util.TimeDuration.hz;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
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
import org.csstudio.ui.util.widgets.RangeWidget;
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
import org.eclipse.ui.IMemento;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VImage;
import org.epics.pvmanager.extra.ColorScheme;
import org.epics.pvmanager.extra.WaterfallPlot;
import org.epics.pvmanager.extra.WaterfallPlotParameters;
import org.epics.pvmanager.util.TimeDuration;

public class WaterfallWidget extends AbstractChannelQueryResultWidget
implements ConfigurableWidget, ISelectionProvider {
	
	private VImageDisplay imageDisplay;
	private RangeWidget rangeWidget;
	private WaterfallPlotParameters parameters = WaterfallPlotParameters.defaults();
	private WaterfallPlot plot;
	private ErrorBar errorBar;
	private GridData gd_rangeWidget;
	private boolean editable = true;
	
	private String sortProperty;
	
	public String getSortProperty() {
		return sortProperty;
	}

	/**
	 * Creates a new widget.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public WaterfallWidget(Composite parent, int style) {
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
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		errorBar.setMarginBottom(5);
		
		rangeWidget = new RangeWidget(this, SWT.NONE);
		rangeWidget.addRangeListener(new RangeListener() {
			
			@Override
			public void rangeChanged() {
				parameters = parameters.with(pixelDuration(TimeDuration.asTimeDuration(TimeDuration.nanos((long) (rangeWidget.getDistancePerPx() * 1000000000)))));
				if (plot != null) {
					plot.with(parameters);
				}
			}
		});
		gd_rangeWidget = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_rangeWidget.widthHint = 61;
		rangeWidget.setLayoutData(gd_rangeWidget);
		
		imageDisplay = new VImageDisplay(this);
		imageDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		imageDisplay.setStretched(SWT.HORIZONTAL);
		imageDisplay.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				if (imageDisplay.getSize().y != 0) {
					changePlotHeight(imageDisplay.getSize().y);
				}
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do
			}
		});
		
		// Set the parameters to the default
		parametersChanged();
	}
	
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		imageDisplay.setMenu(menu);
	}
	
	// The pv name for waveform
	private String waveformPVName;
	// The pv names for multiple channels
	private List<String> scalarPVNames;
	// The pv created by pvmanager
	private PVReader<VImage> pv;
	
	/**
	 * The pv name to connect to.
	 * 
	 * @return the current property value
	 */
	public String getWaveformPVName() {
		return waveformPVName;
	}
	
	/**
	 * Changes the pv name to connect to. Triggers a reconnection.
	 * 
	 * @param pvName the new property value
	 */
	public void setWaveformPVName(String pvName) {
		// Guard from double calls
		if (this.waveformPVName != null && this.waveformPVName.equals(pvName)) {
			return;
		}
		
		this.scalarPVNames = null;
		this.waveformPVName = pvName;
		reconnect();
	}
	
	public List<String> getScalarPVNames() {
		return scalarPVNames;
	}
	
	public void setScalarPVNames(List<String> scalarPVNames) {
		// Guard from double calls
		if (this.scalarPVNames != null && this.scalarPVNames.equals(scalarPVNames)) {
			return;
		}
		
		this.waveformPVName = null;
		this.scalarPVNames = scalarPVNames;
		reconnect();
	}
	
	/**
	 * Whether the user is able to customize the widget.
	 * 
	 * @return true if it can be customized
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * Changes whether the user is able to customize the widget.
	 * 
	 * @param editable true if it can be customized
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	// Displays the last error generated
	private void setLastError(Exception ex) {
		errorBar.setException(ex);
	}
	
	private void parametersChanged() {
		// Make sure image alignment and the range direction
		// are consistent with the scroll direction
		if (parameters.isScrollDown()) {
			imageDisplay.setAlignment(SWT.LEFT | SWT.TOP);
			rangeWidget.setStartPosition(SWT.UP);
		} else {
			imageDisplay.setAlignment(SWT.LEFT | SWT.BOTTOM);
			rangeWidget.setStartPosition(SWT.DOWN);
		}
		
		// Make sure the range is consistent with the image resolution
		rangeWidget.setDistancePerPx(TimeDuration.durationOf(parameters.getPixelDuration()).getNanoSec() / 1000000000.0);
	}
	
	// Reconnects the pv
	private void reconnect() {
		// First de-allocate current pv if any
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		if (waveformPVName != null && !waveformPVName.trim().isEmpty()) {
			int color = (getBackground().getRed() << 16) + (getBackground().getGreen() << 8) + getBackground().getBlue();
			plot = waterfallPlotOf(vDoubleArrayOf(channel(waveformPVName))).with(parameters, WaterfallPlotParameters.backgroundColor(color));
			parameters = plot.getParameters();
			pv = PVManager.read(plot)
				.notifyOn(SWTUtil.swtThread()).every(hz(50));
			pv.addPVReaderListener(new PVReaderListener() {
				
				@Override
				public void pvChanged() {
					setLastError(pv.lastException());
					imageDisplay.setVImage(pv.getValue());
				}
			});
			return;
		}
		
		if (scalarPVNames != null && !scalarPVNames.isEmpty()) {
			int color = (getBackground().getRed() << 16) + (getBackground().getGreen() << 8) + getBackground().getBlue();
			plot = waterfallPlotOf(vDoubles(scalarPVNames)).with(parameters, WaterfallPlotParameters.backgroundColor(color));
			parameters = plot.getParameters();
			pv = PVManager.read(plot)
				.notifyOn(SWTUtil.swtThread()).every(hz(50));
			pv.addPVReaderListener(new PVReaderListener() {
				
				@Override
				public void pvChanged() {
					setLastError(pv.lastException());
					imageDisplay.setVImage(pv.getValue());
				}
			});
			return;
		}
	}
	
	private void changePlotHeight(int newHeight) {
		parameters = parameters.with(WaterfallPlotParameters.height(newHeight));
		if (plot != null) {
			plot.with(parameters);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Changes the parameters used for the waterfall plot.
	 * 
	 * @param parameters a set of waterfall plot parameter
	 */
	public void setWaterfallPlotParameters(WaterfallPlotParameters parameters) {
		this.parameters = parameters;
		parametersChanged();
		if (plot != null) {
			plot.with(parameters);
		}
	}

	/**
	 * The parameters used for the waterfall plot.
	 * 
	 * @return waterfall plot parameters
	 */
	WaterfallPlotParameters getWaterfallPlotParameters() {
		return parameters;
	}
	
	public int getScrollDirection() {
		if (parameters.isScrollDown())
			return SWT.DOWN;
		else
			return SWT.UP;
	}
	
	public void setScrollDirection(int direction) {
		if (direction == SWT.UP) {
			setWaterfallPlotParameters(parameters.with(scrollDown(false)));
		} else if (direction == SWT.DOWN) {
			setWaterfallPlotParameters(parameters.with(scrollDown(true)));
		} else {
			throw new IllegalArgumentException("Scroll direction must be SWT.UP or SWT.DOWN");
		}
	}
	
	public boolean isAdaptiveRange() {
		return parameters.isAdaptiveRange();
	}
	
	public void setAdaptiveRange(boolean adaptiveRange) {
		setWaterfallPlotParameters(parameters.with(adaptiveRange(adaptiveRange)));
	}
	
	public TimeDuration getPixelDuration() {
		return TimeDuration.durationOf(parameters.getPixelDuration());
	}
	
	public void setPixelDuration(TimeDuration pixelDuration) {
		setWaterfallPlotParameters(parameters.with(pixelDuration(pixelDuration)));
	}
	
	public ColorScheme getColorScheme() {
		return parameters.getColorScheme();
	}
	
	public void setColorScheme(ColorScheme colorScheme) {
		setWaterfallPlotParameters(parameters.with(colorScheme(colorScheme)));
	}
	
	/**
	 * Changes whether the range should be displayed.
	 * 
	 * @param showRange true if range should be displayed
	 */
	public void setShowTimeAxis(boolean showRange) {
		rangeWidget.setVisible(showRange);
		
		// Making the range invisible is not enough to not show it.
		// We have to change the layout so that the width is
		// zero and redo the layout
		if (showRange) {
			gd_rangeWidget.widthHint = 61;
			rangeWidget.setLayoutData(gd_rangeWidget);
		} else {
			gd_rangeWidget.widthHint = 0;
			rangeWidget.setLayoutData(gd_rangeWidget);
		}
		layout();
	}
	
	/**
	 * Whether the range should be displayed.
	 * 
	 * @return true if the range is displayed
	 */
	public boolean isShowTimeAxis() {
		return rangeWidget.isVisible();
	}

	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;

		queryExecuted(result);
	}

	@Override
	protected void queryCleared() {
		setWaveformPVName(null);
		setScalarPVNames(null);
		
		// Clean up old image and previous error
		imageDisplay.setVImage(null);
		setLastError(null);
	}
	
	private Result result;

	@Override
	protected void queryExecuted(Result result) {
		if (result == null)
			return;
		
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
					Collections.sort(sortedChannels,
							new Comparator<Channel>() {
								@Override
								public int compare(Channel o1,
										Channel o2) {
									return findProperty(o1).compareTo(
											findProperty(o2));
								}

								public Double findProperty(
										Channel channel) {
									for (Property property : channel
											.getProperties()) {
										if (property.getName().equals(
												getSortProperty())) {
											return Double
													.parseDouble(property
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

		if (finalChannels == null || finalChannels.isEmpty()) {
			setWaveformPVName(getChannelQuery().getQuery());
		} else if (finalChannels.size() == 1) {
			setWaveformPVName(finalChannels.get(0));
		} else
			setScalarPVNames(finalChannels);
	}


	private boolean configurable = true;

	private WaterfallConfigurationDialog dialog;

	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new WaterfallConfigurationDialog(this);
		dialog.open();
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldConfigurable = configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldConfigurable,
				configurable);
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}

	private Map<ISelectionChangedListener, PropertyChangeListener> listenerMap = new HashMap<ISelectionChangedListener, PropertyChangeListener>();
	
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		PropertyChangeListener propListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName()))
					listener.selectionChanged(new SelectionChangedEvent(WaterfallWidget.this, getSelection()));
			}
		};
		listenerMap.put(listener, propListener);
		addPropertyChangeListener(propListener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(new WaterfallSelection(getChannelQuery(), this));
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
	
	
	/** Memento tag */
	private static final String MEMENTO_CHANNEL_QUERY = "channelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_ADAPTIVE_RANGE = "adaptiveRange"; //$NON-NLS-1$
	private static final String MEMENTO_PIXEL_DURATION = "pixelDuration"; //$NON-NLS-1$
	private static final String MEMENTO_SCROLL_DIRECTION = "scrollDirection"; //$NON-NLS-1$
	private static final String MEMENTO_SHOW_TIME_AXIS = "showTimeAxis"; //$NON-NLS-1$
	private static final String MEMENTO_SORT_PROPERTY = "sortProperty"; //$NON-NLS-1$
	
	public void saveState(IMemento memento) {
		if (getChannelQuery() != null) {
			memento.putString(MEMENTO_CHANNEL_QUERY, getChannelQuery().getQuery());
		}
		memento.putBoolean(MEMENTO_ADAPTIVE_RANGE, isAdaptiveRange());
		memento.putInteger(MEMENTO_PIXEL_DURATION, (int) getPixelDuration().getNanoSec());
		memento.putInteger(MEMENTO_SCROLL_DIRECTION, getScrollDirection());
		memento.putBoolean(MEMENTO_SHOW_TIME_AXIS, isShowTimeAxis());
		if (getSortProperty() != null) {
			memento.putString(MEMENTO_SORT_PROPERTY, getSortProperty());
		}
	}
	
	public void loadState(IMemento memento) {
		if (memento != null) {
			if (memento.getBoolean(MEMENTO_ADAPTIVE_RANGE) != null) {
				setAdaptiveRange(memento.getBoolean(MEMENTO_ADAPTIVE_RANGE));
			}
			if (memento.getInteger(MEMENTO_PIXEL_DURATION) != null) {
				setPixelDuration(TimeDuration.nanos(memento.getInteger(MEMENTO_PIXEL_DURATION)));
			}
			if (memento.getInteger(MEMENTO_SCROLL_DIRECTION) != null) {
				setScrollDirection(memento.getInteger(MEMENTO_SCROLL_DIRECTION));
			}
			if (memento.getBoolean(MEMENTO_SHOW_TIME_AXIS) != null) {
				setShowTimeAxis(memento.getBoolean(MEMENTO_SHOW_TIME_AXIS));
			}
			if (memento.getString(MEMENTO_SORT_PROPERTY) != null) {
				setSortProperty(memento.getString(MEMENTO_SORT_PROPERTY));
			}
			if (memento.getString(MEMENTO_CHANNEL_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(memento.getString(MEMENTO_CHANNEL_QUERY)).build());
			}
		}
	}
	

}
