package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.onSWTThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariableName;
import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.util.swt.meter.MeterWidget;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
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

	private static final Logger log = Logger.getLogger(PVManagerProbe.class.getName());

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String SINGLE_VIEW_ID = "org.csstudio.diag.pvmanager.probe.SingleView";
	public static final String MULTIPLE_VIEW_ID = "org.csstudio.diag.pvmanager.probe.MultipleView";
	private static int instance = 0;

	// GUI
	private Label lblAlarm;
	private Label alarmField;
	private ComboViewer cbo_name;
	private ComboHistoryHelper name_helper;
	private Label valueField;
	private Label timeField;
	private Label statusField;
	private MeterWidget meter;
	private Composite top_box;
	private Composite bottom_box;
	private Button show_meter;
	private Button btn_save_to_ioc;

	/** Currently displayed pv */
	private ProcessVariableName PVName;

	/** Currently connected pv */
	private PV<?> pv;

	/** Formatting used for the value text field */
	private ValueFormat valueFormat = new SimpleValueFormat(3);

	/** Formatting used for the time text field */
	private TimeStampFormat timeFormat = new TimeStampFormat("yyyy/MM/dd HH:mm:ss.N Z");

	// No writing to ioc option.
	// private ICommandListener saveToIocCmdListener;

	private Text new_value;

	private static final String SECURITY_ID = "operating";

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
	private GridData gd_valueField;
	private GridData gd_timeField;
	private GridData gd_statusField;

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
			memento.putString(PV_TAG, PVName.getProcessVariableName());
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
		top_box = new Composite(parent, 0);
		GridLayout grid = new GridLayout();
		grid.numColumns = 3;
		top_box.setLayout(grid);

		Label label = new Label(top_box, SWT.READ_ONLY);
		label.setText(Messages.S_PVName);

		cbo_name = new ComboViewer(top_box, SWT.SINGLE | SWT.BORDER);
		cbo_name.getCombo().setToolTipText(Messages.S_EnterPVName);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		cbo_name.getCombo().setLayoutData(gd);

		final Button btn_info = new Button(top_box, SWT.PUSH);
		btn_info.setText(Messages.S_Info);
		btn_info.setToolTipText(Messages.S_ObtainInfo);

		// New Box with only the meter
		meter = new MeterWidget(parent, 0);
		meter.setEnabled(false);

		// Button Box
		bottom_box = new Composite(parent, 0);
		grid = new GridLayout();
		grid.numColumns = 3;
		bottom_box.setLayout(grid);

		label = new Label(bottom_box, 0);
		label.setText(Messages.S_Value);

		valueField = new Label(bottom_box, SWT.BORDER);
		gd_valueField = new GridData();
		gd_valueField.grabExcessHorizontalSpace = true;
		gd_valueField.horizontalAlignment = SWT.FILL;
		valueField.setLayoutData(gd_valueField);

		show_meter = new Button(bottom_box, SWT.CHECK);
		show_meter.setText(Messages.S_Meter);
		show_meter.setToolTipText(Messages.S_Meter_TT);
		show_meter.setSelection(true);

		// New Row
		label = new Label(bottom_box, 0);
		label.setText(Messages.S_Timestamp);

		timeField = new Label(bottom_box, SWT.BORDER);
		gd_timeField = new GridData();
		gd_timeField.grabExcessHorizontalSpace = true;
		gd_timeField.horizontalAlignment = SWT.FILL;
		timeField.setLayoutData(gd_timeField);

		btn_save_to_ioc = new Button(bottom_box, SWT.PUSH);
		btn_save_to_ioc.setText(Messages.S_SaveToIoc);
		btn_save_to_ioc.setToolTipText(Messages.S_SaveToIocTooltip);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		btn_save_to_ioc.setLayoutData(gd);
		btn_save_to_ioc.setEnabled(canExecute);

		lblAlarm = new Label(bottom_box, SWT.NONE);
		lblAlarm.setText("Alarm:");

		alarmField = new Label(bottom_box, SWT.BORDER);
		alarmField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		alarmField.setText("");
		new Label(bottom_box, SWT.NONE);

		// New Row
		final Label new_value_label = new Label(bottom_box, 0);
		new_value_label.setText(Messages.S_NewValueLabel);
		new_value_label.setVisible(false);

		new_value = new Text(bottom_box, SWT.BORDER);
		new_value.setToolTipText(Messages.S_NewValueTT);
		new_value.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		new_value.setVisible(false);
		// new_value.setText(value.getValueDisplayText());
		new_value.setText("TEXT");

		final Button btn_adjust = new Button(bottom_box, SWT.CHECK);
		btn_adjust.setText(Messages.S_Adjust);
		btn_adjust.setToolTipText(Messages.S_ModValue);
		btn_adjust.setEnabled(canExecute);

		// Status bar
		label = new Label(bottom_box, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = grid.numColumns;
		label.setLayoutData(gd);

		label = new Label(bottom_box, 0);
		label.setText(Messages.S_Status);

		statusField = new Label(bottom_box, SWT.BORDER);
		statusField.setText(Messages.S_Waiting);
		gd_statusField = new GridData();
		gd_statusField.grabExcessHorizontalSpace = true;
		gd_statusField.horizontalAlignment = SWT.FILL;
		gd_statusField.horizontalSpan = grid.numColumns - 1;
		statusField.setLayoutData(gd_statusField);

		// Connect the 3 boxes in form layout
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		top_box.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(top_box);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(bottom_box);
		meter.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		bottom_box.setLayoutData(fd);

		// Connect actions
		name_helper = new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), PV_LIST_TAG, cbo_name) {
			@Override
			public void newSelection(final ProcessVariableName pv_name) {
				setPVName(pv_name);
			}
		};

		cbo_name.getCombo().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				if (pv != null)
					pv.close();
				name_helper.saveSettings();
			}
		});

		btn_info.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				showInfo();
			}
		});

		btn_adjust.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				final boolean enable = btn_adjust.getSelection();
				new_value_label.setVisible(enable);
				new_value.setVisible(enable);
			}
		});

		new_value.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// adjustValue(new_value.getText().trim());
			}
		});

		btn_save_to_ioc.addSelectionListener(new SelectionAdapter() {
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

		show_meter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				showMeter(show_meter.getSelection());
			}
		});

		name_helper.loadSettings();

		if (memento != null && memento.getString(PV_TAG) != null) {
			setPVName(new ProcessVariableName(memento.getString(PV_TAG)));
			// Per default, the meter is shown.
			// Hide according to memento.
			final String show = memento.getString(METER_TAG);
			if ((show != null) && show.equals("false")) //$NON-NLS-1$
			{
				show_meter.setSelection(false);
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
			bottom_box.setLayoutData(fd);
		} else { // Meter about to be hidden.
			// Attach bottom box to top box.
			final FormData fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.top = new FormAttachment(top_box);
			fd.right = new FormAttachment(100, 0);
			bottom_box.setLayoutData(fd);
		}
		meter.setVisible(show);
		meter.getShell().layout(true, true);
	}

	protected void showInfo() {
		final String nl = "\n"; //$NON-NLS-1$

		final StringBuilder info = new StringBuilder();
		if (pv == null) {
			info.append(Messages.S_NotConnected).append(nl);
		} else {
			Object value = pv.getValue();
			Alarm alarm = Util.alarmOf(value);
			Display display = Util.displayOf(value);
			Class<?> type = Util.typeOf(value);

			//info.append(Messages.S_ChannelInfo).append("  ").append(pv.getName()).append(nl); //$NON-NLS-1$
			if (pv.getValue() == null) {
				info.append(Messages.S_STATEDisconn).append(nl);
			} else {
				if (alarm != null
						&& AlarmSeverity.UNDEFINED.equals(alarm
								.getAlarmSeverity())) {
					info.append(Messages.S_STATEDisconn).append(nl);
				} else {
					info.append(Messages.S_STATEConn).append(nl);
				}
			}

			if (type != null) {
				info.append("Data type: ").append(type.getSimpleName())
						.append(nl);
			}

			if (display != null) {
				info.append("Numeric display information:").append(nl)
						.append("  Low display limit: ")
						.append(display.getLowerDisplayLimit()).append(nl)
						.append("  Low alarm limit: ")
						.append(display.getLowerAlarmLimit()).append(nl)
						.append("  Low warn limit: ")
						.append(display.getLowerWarningLimit()).append(nl)
						.append("  High warn limit: ")
						.append(display.getUpperWarningLimit()).append(nl)
						.append("  High alarm limit: ")
						.append(display.getUpperAlarmLimit()).append(nl)
						.append("  High display limit: ")
						.append(display.getUpperDisplayLimit()).append(nl);
			}

			if (value instanceof org.epics.pvmanager.data.Enum) {
				Enum enumValue = (Enum) value;
				info.append("Enum metadata: ")
						.append(enumValue.getLabels().size()).append(" labels")
						.append(nl);
				for (String label : enumValue.getLabels()) {
					info.append("  ").append(label).append(nl);
				}
			}

		}
		if (info.length() == 0) {
			info.append(Messages.S_NoInfo);
		}
		final MessageBox box = new MessageBox(valueField.getShell(),
				SWT.ICON_INFORMATION);
		if (pv == null) {
			box.setText(Messages.S_Info);
		} else {
			box.setText("Channel information for " + pv.getName());
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
	public void setPVName(ProcessVariableName pvName) {
		log.log(Level.FINE, "setPVName ({0})", pvName);

		// If we are already scanning that pv, do nothing
		if (this.PVName != null && this.PVName.equals(pvName)) {
			// XXX Seems like something is clearing the combo-box,
			// reset to the actual pv...
			cbo_name.getCombo().setText(pvName.getProcessVariableName());
		}

		// The PV is different, so disconnect and reset the visuals
		if (pv != null)
			pv.close();

		setValue(null);
		setAlarm(null);
		setTime(null);
		setMeter(null, null);

		// If name is blank, update status to waiting and qui
		if ((pvName == null) || pvName.equals("")) {
			cbo_name.getCombo().setText("");
			setStatus(Messages.S_Waiting);
		}

		// If new name, add to history and connect
		name_helper.addEntry(pvName);

		// Update displayed name, unless it's already current
		if (!(cbo_name.getCombo().getText().equals(pvName
				.getProcessVariableName()))) {
			cbo_name.getCombo().setText(pvName.getProcessVariableName());
		}

		setStatus(Messages.S_Searching);
		pv = PVManager.read(channel(pvName.getProcessVariableName()))
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
		this.PVName = pvName;

		// If this is an instance of the multiple view, show the PV name
		// as the title
		if (MULTIPLE_VIEW_ID.equals(getSite().getId())) {
			setPartName(pvName.getProcessVariableName());
		}
	}

	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	public ProcessVariableName getPVName() {
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
			statusField.setText(Messages.S_Waiting);
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
			statusField.setText("Connected");
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
			valueField.setText("");
		} else {
			valueField.setText(value);
		}
	}

	/**
	 * Displays the new alarm.
	 * 
	 * @param alarm a new alarm
	 */
	private void setAlarm(Alarm alarm) {
		if (alarm == null) {
			alarmField.setText("");
		} else {
			alarmField.setText(alarm.getAlarmSeverity() + " - "
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
			timeField.setText("");
		} else {
			timeField.setText(timeFormat.format(time.getTimeStamp()));
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
	public static boolean activateWithPV(ProcessVariableName pvName) {
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
			//	            Plugin.getLogger().error("activateWithPV", e); //$NON-NLS-1$
			e.printStackTrace();
		}
		return false;
	}
}