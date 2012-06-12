package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.util.time.TimeDuration.ofHertz;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery.Result;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.ui.util.widgets.ErrorBar;
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

	private AbstractSelectionProviderWrapper selectionProvider;

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

		if (finalChannels == null || !finalChannels.isEmpty()) {
			setYChannelNames(finalChannels);
		}
	}

	private void setYChannelNames(List<String> finalChannels) {
		this.yChannelNames = finalChannels;
		reconnect();
	}

	private void setLastError(Exception lastException) {
		errorBar.setException(lastException);
	}

	private PVReader<VImage> pv;
	// Y values
	private Collection<String> yChannelNames;
	// X values
	private Collection<String> xChannelNames;

	private void reconnect() {
		if (pv != null) {
			pv.close();
			imageDisplay.setVImage(null);
			plot = null;
		}

		if (yChannelNames == null || yChannelNames.isEmpty()) {
			return;
		}

		if (yChannelNames != null && !yChannelNames.isEmpty()) {
			ChannelExpressionList<VNumber, VNumber> channels = channels(
					yChannelNames, VNumber.class, VNumber.class);
			DesiredRateExpression<VDoubleArray> testChannels = vDoubleArrayOf(latestValueOf(channels));
			plot = ExpressionLanguage.lineGraphOf(testChannels);
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
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		PropertyChangeListener propListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName()))
					listener.selectionChanged(new SelectionChangedEvent(Line2DPlotWidget.this, getSelection()));
			}
		};
		listenerMap.put(listener, propListener);
		addPropertyChangeListener(propListener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(new Line2DPlotSelection(getChannelQuery(), this));
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
	
	@Override
	public boolean isConfigurable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openConfigurationDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void configurationDialogClosed() {
		// TODO Auto-generated method stub
		
	}
}
