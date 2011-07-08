package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.onSWTThread;
import static org.epics.pvmanager.ExpressionLanguage.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Enum;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.Time;
import org.epics.pvmanager.data.Util;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.util.TimeStampFormat;

/**
 * Probe view.
 */
public class PVManagerProbe extends ViewPart {
	public PVManagerProbe() {
	}

	private static final Logger log = Logger.getLogger(PVManagerProbe.class.getName());

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String SINGLE_VIEW_ID = "org.csstudio.diag.pvmanager.probe.SingleView"; //$NON-NLS-1$
	public static final String MULTIPLE_VIEW_ID = "org.csstudio.diag.pvmanager.probe.MultipleView"; //$NON-NLS-1$
	private static int instance = 0;

	// GUI
	private Label alarmLabel;
	private Label valueLabel;
	private Label timestampLabel;
	private Label statusLabel;
	private Label newValueLabel;
	private Label pvNameLabel;
	private Label timestampField;
	private Label alarmField;
	private Label valueField;
	private Label statusField;
	private ComboViewer pvNameField;
	private ComboHistoryHelper pvNameHelper;
	private MeterWidget meter;
	private Composite topBox;
	private Composite bottomBox;
	private Button showMeterButton;
	private Button saveToIocButton;
	private Button infoButton;
	private GridData gd_valueField;
	private GridData gd_timestampField;
	private GridData gd_statusField;
	private GridLayout gl_topBox;
	private FormData fd_topBox;
	private FormData fd_bottomBox;

	/** Currently displayed pv */
	private ProcessVariable PVName;

	/** Currently connected pv */
	private PV<?> pv;
	
	/** Current pv write */
	private PVWriter<Object> pvWriter;

	/** Formatting used for the value text field */
	private ValueFormat valueFormat = new SimpleValueFormat(3);

	/** Formatting used for the time text field */
	private TimeStampFormat timeFormat = new TimeStampFormat("yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

	// No writing to ioc option.
	// private ICommandListener saveToIocCmdListener;

	private Text newValueField;

	private static final String SECURITY_ID = "operating"; //$NON-NLS-1$

	/** Memento used to preserve the PV name. */
	private IMemento memento = null;

	/** Memento tag */
	private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
	/** Memento tag */
	private static final String PV_TAG = "PVName"; //$NON-NLS-1$
	/** Memento tag */
	private static final String METER_TAG = "meter"; //$NON-NLS-1$

	/**
	 * Id of the save value command.
	 */
	private static final String SAVE_VALUE_COMMAND_ID = "org.csstudio.platform.ui.commands.saveValue"; //$NON-NLS-1$

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

		Label label;
		pvNameLabel = new Label(topBox, SWT.READ_ONLY);
		pvNameLabel.setText(Messages.Probe_pvNameLabelText);

		pvNameField = new ComboViewer(topBox, SWT.SINGLE | SWT.BORDER);
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

		saveToIocButton = new Button(bottomBox, SWT.PUSH);
		saveToIocButton.setText(Messages.Probe_saveToIocButtonText);
		saveToIocButton.setToolTipText(Messages.Probe_saveToIocButtonToolTipText);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		saveToIocButton.setLayoutData(gd);
		saveToIocButton.setEnabled(canExecute);

		alarmLabel = new Label(bottomBox, SWT.NONE);
		alarmLabel.setText(Messages.Probe_alarmLabelText);

		alarmField = new Label(bottomBox, SWT.BORDER);
		alarmField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		alarmField.setText(""); //$NON-NLS-1$
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
				pvWriter.write(newValueField.getText());
			}
		});

		saveToIocButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// saveToIoc();
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

	protected void showInfo() {
		final String nl = "\n"; //$NON-NLS-1$
		final String space = " "; //$NON-NLS-1$
		final String indent = "  "; //$NON-NLS-1$

		final StringBuilder info = new StringBuilder();
		if (pv == null) {
			info.append(Messages.Probe_infoStateNotConnected).append(nl);
		} else {
			Object value = pv.getValue();
			Alarm alarm = Util.alarmOf(value);
			Display display = Util.displayOf(value);
			Class<?> type = Util.typeOf(value);

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

			if (value instanceof org.epics.pvmanager.data.Enum) {
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
			// XXX Seems like something is clearing the combo-box,
			// reset to the actual pv...
			pvNameField.getCombo().setText(pvName.getName());
		}

		// The PV is different, so disconnect and reset the visuals
		if (pv != null) {
			pv.close();
			pv = null;
		}
		
		if (pvWriter != null) {
			pvWriter.close();
			pvWriter = null;
		}

		setValue(null);
		setAlarm(null);
		setTime(null);
		setMeter(null, null);

		// If name is blank, update status to waiting and qui
		if ((pvName == null) || pvName.equals("")) { //$NON-NLS-1$
			pvNameField.getCombo().setText(""); //$NON-NLS-1$
			setStatus(Messages.Probe_statusWaitingForPV);
		}

		// If new name, add to history and connect
		pvNameHelper.addEntry(pvName.getName());

		// Update displayed name, unless it's already current
		if (!(pvNameField.getCombo().getText().equals(pvName
				.getName()))) {
			pvNameField.getCombo().setText(pvName.getName());
		}

		setStatus(Messages.Probe_statusSearching);
		pv = PVManager.read(channel(pvName.getName()))
				.andNotify(onSWTThread()).atHz(25);
		pv.addPVValueChangeListener(new PVValueChangeListener() {

			@Override
			public void pvValueChanged() {
				Object obj = pv.getValue();
				setLastError(pv.lastException());
				setValue(valueFormat.format(obj));
				setAlarm(Util.alarmOf(obj));
				setTime(Util.timeOf(obj));
				setMeter(Util.numericValueOf(obj), Util.displayOf(obj));
			}
		});
		
		try {
			pvWriter = PVManager.write(toChannel(pvName.getName())).async();
			newValueField.setEditable(true);
		} catch (Exception e) {
			newValueField.setEditable(false);
		}
		this.PVName = pvName;

		// If this is an instance of the multiple view, show the PV name
		// as the title
		if (MULTIPLE_VIEW_ID.equals(getSite().getId())) {
			setPartName(pvName.getName());
		}
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

	/**
	 * Displays the last error in the status.
	 * 
	 * @param ex an exception
	 */
	private void setLastError(Exception ex) {
		if (ex == null) {
			statusField.setText(Messages.Probe_statusConnected);
		} else {
			statusField.setText(ex.getMessage());
		}
	}

	/**
	 * Displays the new value.
	 * 
	 * @param value a new value
	 */
	private void setValue(String value) {
		if (value == null) {
			valueField.setText(""); //$NON-NLS-1$
			if (newValueField.isVisible() && !newValueField.isFocusControl()) {
				newValueField.setText("");
			}
		} else {
			valueField.setText(value);
			if (newValueField.isVisible() && !newValueField.isFocusControl()) {
				newValueField.setText(value);
			}
		}
	}

	/**
	 * Displays the new alarm.
	 * 
	 * @param alarm a new alarm
	 */
	private void setAlarm(Alarm alarm) {
		if (alarm == null) {
			alarmField.setText(""); //$NON-NLS-1$
		} else {
			alarmField.setText(alarm.getAlarmSeverity() + " - " //$NON-NLS-1$
					+ alarm.getAlarmStatus());
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
			timestampField.setText(timeFormat.format(time.getTimeStamp()));
		}
	}

	/**
	 * Displays a new value in the meter.
	 * 
	 * @param value the new value
	 * @param display the display information
	 */
	private void setMeter(Double value, Display display) {
		if (value == null || display == null) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (pv != null)
			pv.close();
		super.dispose();
	}

	/**
	 * Open PVManagerProbe initialized to the given PV
	 * 
	 * @param pvName the pv
	 * @return true if successful
	 */
	public static boolean activateWithPV(ProcessVariable pvName) {
		try {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IWorkbenchWindow window = workbench
					.getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();
			final PVManagerProbe probe = (PVManagerProbe) page.showView(
					SINGLE_VIEW_ID, createNewInstance(),
					IWorkbenchPage.VIEW_ACTIVATE);
			probe.setPVName(pvName);
			return true;
		} catch (final Exception e) {
			log.log(Level.WARNING, "Failed while opening probe", e);
		}
		return false;
	}
}