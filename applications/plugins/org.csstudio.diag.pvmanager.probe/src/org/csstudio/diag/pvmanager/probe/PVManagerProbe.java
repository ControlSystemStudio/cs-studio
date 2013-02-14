package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.util.time.TimeDuration.ofHertz;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.MeterWidget;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.TimeoutException;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Enum;
import org.epics.vtype.SimpleValueFormat;
import org.epics.vtype.Time;
import org.epics.vtype.ValueFormat;
import org.epics.vtype.ValueUtil;

/**
 * Probe view.
 */
public class PVManagerProbe extends ViewPart {
	public PVManagerProbe() {
	}

	private static final Logger log = Logger.getLogger(PVManagerProbe.class.getName());

	/**
	 * The ID of the view as specified by the extension point
	 */
	public static final String VIEW_ID = "org.csstudio.diag.pvmanager.probe"; //$NON-NLS-1$
	private static int instance = 0;
	private Label valueLabel;
	private Label timestampLabel;
	private Label statusLabel;
	private Label newValueLabel;
	private Label pvNameLabel;
	private Label timestampField;
	private Label valueField;
	private Label statusField;
	private ComboViewer pvNameField;
	private ComboHistoryHelper pvNameHelper;
	private ErrorBar errorBar;
	private MeterWidget meter;
	private Composite topBox;
	private Composite bottomBox;
	private Button showMeterButton;
	private Button infoButton;
	private GridData gd_valueField;
	private GridData gd_timestampField;
	private GridData gd_statusField;
	private GridLayout gl_topBox;
	private FormData fd_topBox;
	private FormData fd_bottomBox;
	
	private boolean readOnly = true;

	/** Currently displayed pv */
	private ProcessVariable PVName;

	/** Currently connected pv */
	private PV<Object, Object> pv;

	/** Formatting used for the value text field */
	private ValueFormat valueFormat = new SimpleValueFormat(3);

	/** Formatting used for the time text field */
	private TimestampFormat timeFormat = new TimestampFormat("yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

	// No writing to ioc option.
	// private ICommandListener saveToIocCmdListener;

	private Text newValueField;

	/** Memento used to preserve the PV name. */
	private IMemento memento = null;

	/** Memento tag */
	private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
	/** Memento tag */
	private static final String PV_TAG = "PVName"; //$NON-NLS-1$
	/** Memento tag */
	private static final String METER_TAG = "meter"; //$NON-NLS-1$

	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		// Save the memento
		this.memento = memento;
	}

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		// Save the currently selected variable
		if (PVName != null) {
			memento.putString(PV_TAG, PVName.getName());
		}
	}

	public void createPartControl(Composite parent) {
		// Create the view
		final boolean canExecute = true;
		// final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, true);
		
		final FormLayout layout = new FormLayout();
		parent.setLayout(layout);

		// 3 Boxes, connected via form layout: Top, meter, bottom
		//
		// PV Name: ____ name ____________________ [Info]
		// +---------------------------------------------------+
		// | Meter |
		// +---------------------------------------------------+
		// Value : ____ value ________________ [x] meter
		// Timestamp : ____ time _________________ [Save to IOC]
		// [x] Adjust
		// ---------------
		// Status: ...
		//
		// Inside top & bottom, it's a grid layout
		topBox = new Composite(parent, 0);
		GridLayout gl_bottomBox;
		gl_topBox = new GridLayout();
		gl_topBox.numColumns = 3;
		topBox.setLayout(gl_topBox);
		
		errorBar = new ErrorBar(parent, SWT.NONE);
		errorBar.setMarginRight(5);
		errorBar.setMarginLeft(5);
		errorBar.setMarginBottom(5);

		Label label;
		pvNameLabel = new Label(topBox, SWT.READ_ONLY);
		pvNameLabel.setText(Messages.Probe_pvNameLabelText);

		pvNameField = new ComboViewer(topBox, SWT.BORDER);
		pvNameField.getCombo().setToolTipText(Messages.Probe_pvNameFieldToolTipText);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		pvNameField.getCombo().setLayoutData(gd);

		infoButton = new Button(topBox, SWT.PUSH);
		infoButton.setText(Messages.Probe_infoTitle);
		infoButton.setToolTipText(Messages.Probe_infoButtonToolTipText);

		// New Box with only the meter
		meter = new MeterWidget(parent, 0);
		meter.setEnabled(false);

		// Button Box
		bottomBox = new Composite(parent, 0);
		gl_bottomBox = new GridLayout();
		gl_bottomBox.numColumns = 3;
		bottomBox.setLayout(gl_bottomBox);

		valueLabel = new Label(bottomBox, 0);
		valueLabel.setText(Messages.Probe_valueLabelText);

		valueField = new Label(bottomBox, SWT.BORDER);
		gd_valueField = new GridData();
		gd_valueField.grabExcessHorizontalSpace = true;
		gd_valueField.horizontalAlignment = SWT.FILL;
		valueField.setLayoutData(gd_valueField);

		showMeterButton = new Button(bottomBox, SWT.CHECK);
		showMeterButton.setText(Messages.Probe_showMeterButtonText);
		showMeterButton.setToolTipText(Messages.Probe_showMeterButtonToolTipText);
		showMeterButton.setSelection(true);

		// New Row
		timestampLabel = new Label(bottomBox, 0);
		timestampLabel.setText(Messages.Probe_timestampLabelText);

		timestampField = new Label(bottomBox, SWT.BORDER);
		gd_timestampField = new GridData();
		gd_timestampField.grabExcessHorizontalSpace = true;
		gd_timestampField.horizontalAlignment = SWT.FILL;
		timestampField.setLayoutData(gd_timestampField);
		new Label(bottomBox, SWT.NONE);

		// New Row
		newValueLabel = new Label(bottomBox, 0);
		newValueLabel.setText(Messages.Probe_newValueLabelText);
		newValueLabel.setVisible(false);

		newValueField = new Text(bottomBox, SWT.BORDER);
		newValueField.setToolTipText(Messages.Probe_newValueFieldToolTipText);
		newValueField.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		newValueField.setVisible(false);
		newValueField.setText(""); //$NON-NLS-1$

		final Button btn_adjust = new Button(bottomBox, SWT.CHECK);
		btn_adjust.setText(Messages.S_Adjust);
		btn_adjust.setToolTipText(Messages.S_ModValue);
		btn_adjust.setEnabled(canExecute);

		// Status bar
		label = new Label(bottomBox, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = gl_bottomBox.numColumns;
		label.setLayoutData(gd);

		statusLabel = new Label(bottomBox, 0);
		statusLabel.setText(Messages.Probe_statusLabelText);

		statusField = new Label(bottomBox, SWT.BORDER);
		statusField.setText(Messages.Probe_statusWaitingForPV);
		gd_statusField = new GridData();
		gd_statusField.grabExcessHorizontalSpace = true;
		gd_statusField.horizontalAlignment = SWT.FILL;
		gd_statusField.horizontalSpan = gl_bottomBox.numColumns - 1;
		statusField.setLayoutData(gd_statusField);

		// Connect the 3 boxes in form layout
		FormData fd;
		fd_topBox = new FormData();
		fd_topBox.left = new FormAttachment(0, 0);
		fd_topBox.top = new FormAttachment(0, 0);
		fd_topBox.right = new FormAttachment(100, 0);
		topBox.setLayoutData(fd_topBox);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(topBox);
		fd.right = new FormAttachment(100, 0);
		errorBar.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(errorBar);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(bottomBox);
		meter.setLayoutData(fd);

		fd_bottomBox = new FormData();
		fd_bottomBox.left = new FormAttachment(0, 0);
		fd_bottomBox.right = new FormAttachment(100, 0);
		fd_bottomBox.bottom = new FormAttachment(100, 0);
		bottomBox.setLayoutData(fd_bottomBox);

		// Connect actions
		pvNameHelper = new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), PV_LIST_TAG, pvNameField.getCombo()) {
			@Override
			public void newSelection(final String pvName) {
				setPVName(new ProcessVariable(pvName));
			}
		};

		pvNameField.getCombo().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				if (pv != null)
					pv.close();
				pvNameHelper.saveSettings();
			}
		});

		infoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				showInfo();
			}
		});

		btn_adjust.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				final boolean enable = btn_adjust.getSelection();
				newValueLabel.setVisible(enable);
				newValueField.setVisible(enable);
				newValueField.setText(valueField.getText());
			}
		});

		newValueField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				pv.write(newValueField.getText());
			}
		});
		// // Create a listener to enable/disable the Save to IOC button based
		// on
		// // the availability of a command handler.
		// saveToIocCmdListener = new ICommandListener() {
		// public void commandChanged(final CommandEvent commandEvent) {
		// if (commandEvent.isEnabledChanged()) {
		// btn_save_to_ioc.setVisible(commandEvent.getCommand()
		// .isEnabled());
		// }
		// }
		// };
		// // Set the initial vilibility of the button
		// updateSaveToIocButtonVisibility();

		showMeterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				showMeter(showMeterButton.getSelection());
			}
		});

		pvNameHelper.loadSettings();

		if (memento != null && memento.getString(PV_TAG) != null) {
			setPVName(new ProcessVariable(memento.getString(PV_TAG)));
			// Per default, the meter is shown.
			// Hide according to memento.
			final String show = memento.getString(METER_TAG);
			if ((show != null) && show.equals("false")) //$NON-NLS-1$
			{
				showMeterButton.setSelection(false);
				showMeter(false);
			}
		}
	}

	protected void showMeter(final boolean show) {
		if (show) { // Meter about to become visible
			// Attach bottom box to bottom of screen,
			// and meter stretches between top and bottom box.
			final FormData fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.right = new FormAttachment(100, 0);
			fd.bottom = new FormAttachment(100, 0);
			bottomBox.setLayoutData(fd);
		} else { // Meter about to be hidden.
			// Attach bottom box to top box.
			final FormData fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.top = new FormAttachment(topBox);
			fd.right = new FormAttachment(100, 0);
			bottomBox.setLayoutData(fd);
		}
		meter.setVisible(show);
		meter.getShell().layout(true, true);
	}
	
	private String pvNameWithDataSource() {
		DataSource defaultDS = PVManager.getDefaultDataSource();
		String pvName = PVName.getName();
		if (defaultDS instanceof CompositeDataSource) {
			CompositeDataSource composite = (CompositeDataSource) defaultDS;
			if (!pvName.contains(composite.getDelimiter())) {
				pvName = composite.getDefaultDataSource() + composite.getDelimiter() + pvName;
			}
		}

		return pvName;
	}

	protected void showInfo() {
		final String nl = "\n"; //$NON-NLS-1$
		final String space = " "; //$NON-NLS-1$
		final String indent = "  "; //$NON-NLS-1$

		final StringBuilder info = new StringBuilder();
		if (pv == null) {
			info.append(Messages.Probe_infoStateNotConnected).append(nl);
		} else {
			Object value = pv.getValue();
			Alarm alarm = ValueUtil.alarmOf(value);
			Display display = ValueUtil.displayOf(value);
			Class<?> type = ValueUtil.typeOf(value);
			ChannelHandler handler = PVManager.getDefaultDataSource().getChannels().get(pvNameWithDataSource());
			
			if (handler != null) {
				SortedMap<String, Object> sortedProperties = new TreeMap<String, Object>(handler.getProperties());
				if (!sortedProperties.isEmpty()) {
					info.append("Channel details:").append(nl);
					for (Map.Entry<String, Object> entry : sortedProperties.entrySet()) {
						info.append(indent).append(entry.getKey()).append(" = ").append(entry.getValue()).append(nl);
					}
				}
			}

			//info.append(Messages.S_ChannelInfo).append("  ").append(pv.getName()).append(nl); //$NON-NLS-1$
			if (pv.getValue() == null) {
				info.append(Messages.Probe_infoStateDisconnected).append(nl);
			} else {
				if (alarm != null
						&& AlarmSeverity.UNDEFINED.equals(alarm
								.getAlarmSeverity())) {
					info.append(Messages.Probe_infoStateDisconnected).append(nl);
				} else {
					info.append(Messages.Probe_infoStateConnected).append(nl);
				}
			}

			if (type != null) {
				info.append(Messages.Probe_infoDataType).append(space).append(type.getSimpleName())
						.append(nl);
			}

			if (display != null) {
				info.append(Messages.Probe_infoNumericDisplay).append(nl)
						.append(indent).append(Messages.Probe_infoLowDisplayLimit).append(space)
						.append(display.getLowerDisplayLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoLowAlarmLimit).append(space)
						.append(display.getLowerAlarmLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoLowWarnLimit).append(space)
						.append(display.getLowerWarningLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoHighWarnLimit).append(space)
						.append(display.getUpperWarningLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoHighAlarmLimit).append(space)
						.append(display.getUpperAlarmLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoHighDisplayLimit).append(space)
						.append(display.getUpperDisplayLimit()).append(nl);
			}

			if (value instanceof org.epics.vtype.Enum) {
				Enum enumValue = (Enum) value;
				info.append(Messages.Probe_infoEnumMetadata).append(space)
						.append(enumValue.getLabels().size()).append(space).append(Messages.Probe_infoLabels)
						.append(nl);
				for (String label : enumValue.getLabels()) {
					info.append(indent).append(label).append(nl);
				}
			}

		}
		if (info.length() == 0) {
			info.append(Messages.Probe_infoNoInfoAvailable);
		}
		final MessageBox box = new MessageBox(valueField.getShell(),
				SWT.ICON_INFORMATION);
		if (pv == null) {
			box.setText(Messages.Probe_infoTitle);
		} else {
			box.setText(Messages.Probe_infoChannelInformationFor + pv.getName());
		}
		box.setMessage(info.toString());
		box.open();
	}

	/**
	 * Changes the PV currently displayed by probe.
	 * 
	 * @param pvName
	 *            the new pv name or null
	 */
	public void setPVName(ProcessVariable pvName) {
		log.log(Level.FINE, "setPVName ({0})", pvName); //$NON-NLS-1$

		// If we are already scanning that pv, do nothing
		if (this.PVName != null && this.PVName.equals(pvName)) {
			return;
		}

		// The PV is different, so disconnect and reset the visuals
		if (pv != null) {
			pv.close();
			pv = null;
		}

		setValue(null, null);
		setTime(null);
		setMeter(null, null);
		setLastError(null);
		setReadOnly(true);

		// If name is blank, update status to waiting and quit
		if ((pvName.getName() == null) || pvName.getName().trim().isEmpty()) {
			pvNameField.getCombo().setText(""); //$NON-NLS-1$
			setStatus(Messages.Probe_statusWaitingForPV);
		}

		// Update displayed name, unless it's already current
		if (!(pvNameField.getCombo().getText().equals(pvName
				.getName()))) {
			pvNameHelper.changeSelection(pvName.getName());
		}

		setStatus(Messages.Probe_statusSearching);
		pv = PVManager.readAndWrite(channel(pvName.getName()))
				.timeout(ofMillis(5000), "No connection after 5s. Still trying...")
				.readListener(new PVReaderListener<Object>() {
					@Override
					public void pvChanged(PVReaderEvent<Object> event) {
						Object obj = pv.getValue();
						setLastError(pv.lastException());
						setValue(valueFormat.format(obj), ValueUtil.alarmOf(obj));
						setTime(ValueUtil.timeOf(obj));
						setMeter(ValueUtil.numericValueOf(obj), ValueUtil.displayOf(obj));
						if (pv.isConnected()) {
							setStatus(Messages.Probe_statusConnected);
						} else {
							setStatus(Messages.Probe_statusSearching);
						}
					}
				})
				.writeListener(new PVWriterListener<Object>() {
					@Override
					public void pvChanged(PVWriterEvent<Object> event) {
						Exception lastException = pv.lastWriteException();
						setReadOnly(!pv.isWriteConnected());
					}
				})
				.notifyOn(swtThread(this)).asynchWriteAndMaxReadRate(ofHertz(25));
		
		
		this.PVName = pvName;

		// Show the PV name  as the title
		setPartName(pvName.getName());
	}

	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	public ProcessVariable getPVName() {
		return this.PVName;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	/**
	 * Modifies the prove status.
	 * 
	 * @param status new status to be displayed
	 */
	private void setStatus(String status) {
		if (status == null) {
			statusField.setText(Messages.Probe_statusWaitingForPV);
		} else {
			statusField.setText(status);
		}
	}

	private Exception lastError = null;
	
	/**
	 * Displays the last error in the status.
	 * 
	 * @param ex an exception
	 */
	private void setLastError(Exception ex) {
		// If a timeout comes after an error, ignore it
		if (ex instanceof TimeoutException && lastError != null) {
			return;
		}
		errorBar.setException(ex);
		lastError = ex;
	}

	/**
	 * Displays the new value.
	 * 
	 * @param value a new value
	 */
	private void setValue(String value, Alarm alarm) {
		// Calculate alarm string
		String alarmString = "";
		if (alarm != null)
			alarmString = alarmToString(alarm);
		
		// Calculate value string
		String valueString = "";
		if (value != null)
			valueString = value;
		
		String mergedString = valueString;
		if (!alarmString.isEmpty())
			mergedString = mergedString + " " + alarmString;
		
		valueField.setText(mergedString);
		if (newValueField.isVisible() && !newValueField.isFocusControl()) {
			newValueField.setText(valueString);
		}
	}

	/**
	 * Displays the new alarm.
	 * 
	 * @param alarm a new alarm
	 */
	private String alarmToString(Alarm alarm) {
		if (alarm == null || alarm.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
			return ""; //$NON-NLS-1$
		} else {
			return "[" + alarm.getAlarmSeverity() + " - " //$NON-NLS-1$
					+ alarm.getAlarmName() + "]";
		}
	}

	/**
	 * Displays the new time.
	 * 
	 * @param time a new time
	 */
	private void setTime(Time time) {
		if (time == null) {
			timestampField.setText(""); //$NON-NLS-1$
		} else {
			timestampField.setText(timeFormat.format(time.getTimestamp()));
		}
	}

	/**
	 * Displays a new value in the meter.
	 * 
	 * @param value the new value
	 * @param display the display information
	 */
	private void setMeter(Double value, Display display) {
		if (value == null || display == null || !ValueUtil.displayHasValidDisplayLimits(display)) {
			meter.setEnabled(false);
			// meter.setValue(0.0);
		} else if (display.getUpperDisplayLimit() <= display
				.getLowerDisplayLimit()) {
			meter.setEnabled(false);
			// meter.setValue(0.0);
		} else {
			meter.setEnabled(true);
			meter.setLimits(display.getLowerDisplayLimit(),
					display.getLowerAlarmLimit(),
					display.getLowerWarningLimit(),
					display.getUpperWarningLimit(),
					display.getUpperAlarmLimit(),
					display.getUpperDisplayLimit(), 1);
			meter.setValue(value);
		}
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		newValueField.setEditable(!readOnly);
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (pv != null)
			pv.close();
		super.dispose();
	}
}
