package org.csstudio.diag.pvmanager.probe.views;

import org.csstudio.diag.pvmanager.probe.Activator;
import org.csstudio.diag.pvmanager.probe.Messages;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.util.swt.meter.MeterWidget;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VDouble;
import static org.epics.pvmanager.data.ExpressionLanguage.*;

/**
 *
 */

public class PVManagerProbe extends ViewPart implements PVValueChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.diag.pvmanager.probe.views.PVManagerProbe";
	private static int instance = 0;

	PV<VDouble> pv;

	// GUI
	private ComboViewer cbo_name;
	private ComboHistoryHelper name_helper;
	private Label lbl_value;
	private Label lbl_time;
	private Label lbl_status;
	private MeterWidget meter;
	private Composite top_box;
	private Composite bottom_box;
	private Button show_meter;
	private Button btn_save_to_ioc;
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

	/** Is this a new channel where we never received a value? */
	private boolean new_channel = true;

	/**
	 * Id of the save value command.
	 */
	private static final String SAVE_VALUE_COMMAND_ID = "org.csstudio.platform.ui.commands.saveValue"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public PVManagerProbe() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// Create the help context id for the viewer's control
		createGUI(parent);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void createGUI(Composite parent) {
		final boolean canExecute = SecurityFacade.getInstance().canExecute(
				SECURITY_ID, true);
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
		label.setLayoutData(new GridData());

		cbo_name = new ComboViewer(top_box, SWT.SINGLE | SWT.BORDER);
		cbo_name.getCombo().setToolTipText(Messages.S_EnterPVName);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		cbo_name.getCombo().setLayoutData(gd);

		final Button btn_info = new Button(top_box, SWT.PUSH);
		btn_info.setText(Messages.S_Info);
		btn_info.setToolTipText(Messages.S_ObtainInfo);
		btn_info.setLayoutData(new GridData());

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
		label.setLayoutData(new GridData());

		lbl_value = new Label(bottom_box, SWT.BORDER);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		lbl_value.setLayoutData(gd);

		show_meter = new Button(bottom_box, SWT.CHECK);
		show_meter.setText(Messages.S_Meter);
		show_meter.setToolTipText(Messages.S_Meter_TT);
		show_meter.setSelection(true);
		show_meter.setLayoutData(new GridData());

		// New Row
		label = new Label(bottom_box, 0);
		label.setText(Messages.S_Timestamp);
		label.setLayoutData(new GridData());

		lbl_time = new Label(bottom_box, SWT.BORDER);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		lbl_time.setLayoutData(gd);

		btn_save_to_ioc = new Button(bottom_box, SWT.PUSH);
		btn_save_to_ioc.setText(Messages.S_SaveToIoc);
		btn_save_to_ioc.setToolTipText(Messages.S_SaveToIocTooltip);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		btn_save_to_ioc.setLayoutData(gd);
		btn_save_to_ioc.setEnabled(canExecute);

		// New Row
		final Label new_value_label = new Label(bottom_box, 0);
		new_value_label.setText(Messages.S_NewValueLabel);
		new_value_label.setLayoutData(new GridData());
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
		btn_adjust.setLayoutData(new GridData());
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
		label.setLayoutData(new GridData());

		lbl_status = new Label(bottom_box, SWT.BORDER);
		lbl_status.setText(Messages.S_Waiting);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.horizontalSpan = grid.numColumns - 1;
		lbl_status.setLayoutData(gd);

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
			public void newSelection(final String pv_name) {
				setPVName(pv_name);
			}
		};

		cbo_name.getCombo().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				disposeChannel();
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

		if (memento != null) {
			setPVName(memento.getString(PV_TAG));
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

		final StringBuffer info = new StringBuffer();
		if (pv == null) {
			info.append(Messages.S_NotConnected + nl);
		} else {
			info.append(nl + Messages.S_ChannelInfo + "  " + pv.getName() + nl); //$NON-NLS-1$
			if (!pv.isClosed()) {
				info.append(Messages.S_STATEConn + nl);
			} else {
				info.append(Messages.S_STATEDisconn + nl);
			}
			// TODO
			// Add value information
		}
		if (info.length() == 0) {
			info.append(Messages.S_NoInfo);
		}
		final MessageBox box = new MessageBox(lbl_value.getShell(),
				SWT.ICON_INFORMATION);
		box.setText(Messages.S_Info);
		box.setMessage(info.toString());
		box.open();
	}

	protected void disposeChannel() {
		if (pv != null) {
			Activator.getLogger()
					.debug("Probe: disposeChannel " + pv.getName()); //$NON-NLS-1$
			pv.removePVValueChangeListener(this);
			pv.close();
			pv = null;
		}
	}

	/**
	 * 
	 * @param pvName
	 * @return
	 */
	public boolean setPVName(String pvName) {
		Activator.getLogger().debug("setPVName(" + pvName + ")");
		// Reset rest of GUI
		lbl_value.setText("");
		lbl_time.setText("");
		new_value.setText("");
		meter.setEnabled(false);
		new_channel = true;

		// Check the name
		if ((pvName == null) || pvName.equals("")) {
			cbo_name.getCombo().setText("");
			updateStatus(Messages.S_Waiting);
			return false;
		}

		name_helper.addEntry(pvName);
		// Update displayed name, unless it's already current
		if (!(cbo_name.getCombo().getText().equals(pvName))) {
			cbo_name.getCombo().setText(pvName);
		}

		// Create a new channel
		// dispose existing pv
		if (pv != null)
			pv.close();
		int scanRate = 1;
		try {
			updateStatus(Messages.S_Searching);
			pv = PVManager.read(vDouble(pvName)).atHz(scanRate);
			pv.addPVValueChangeListener(this);
		} catch (Throwable ex) {
			Activator.getLogger().error(Messages.S_CreateError, ex);
			updateStatus(Messages.S_CreateError + ex.getMessage());
			return false;
		}
		return true;
		// TODO
		// update the view to some initial value for the pv
	}

	private void updateStatus(final String text) {
		if (text != null) {
			// Make it run in the SWT UI thread
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!lbl_status.isDisposed()) {
						lbl_status.setText(text);
					}
				}
			});
		}
	}

	private void hookContextMenu() {
	}

	private void contributeToActionBars() {
	}

	@SuppressWarnings("unused")
	private void fillLocalPullDown(IMenuManager manager) {
	}

	@SuppressWarnings("unused")
	private void fillContextMenu(IMenuManager manager) {
	}

	@SuppressWarnings("unused")
	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {
	}

	private void hookDoubleClickAction() {
	}

	@SuppressWarnings("unused")
	private void showMessage(String message) {
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

	@Override
	public void pvValueChanged() {
		// System.out.println("Current thread " + Thread.currentThread());
		// System.out.println("GUI thread " +
		// PlatformUI.getWorkbench().getDisplay().getThread());

		if (lbl_value.isDisposed()) {
			return;
		}
		String strValue = pv.getValue().getValue() + " "
				+ pv.getValue().getUnits();
		lbl_value.setText(strValue);

		new_value.setText(strValue);

		// final INumericMetaData meta = value.getNumericMetaData();
		if (pv == null) {
			meter.setEnabled(false);
		} else { // Configure on first value from new channel
			VDouble value = pv.getValue();
			lbl_time.setText(value.getTimeStamp().asDate().toString());
			if (new_channel) {
				if (pv.getValue().getLowerDisplayLimit() < pv.getValue()
						.getUpperDisplayLimit()) {
					meter.configure(value.getLowerDisplayLimit(), value
							.getLowerAlarmLimit(),
							value.getLowerWarningLimit(), value
									.getUpperWarningLimit(), value
									.getUpperAlarmLimit(), value
									.getUpperDisplayLimit(), 1);
					meter.setEnabled(true);
				} else {
					meter.setEnabled(false);
				}
			}
			meter.setValue(value.getValue());
		}
		Activator.getLogger().debug("Probe displays " //$NON-NLS-1$
				+ lbl_time.getText() + " " + lbl_value.getText()); //$NON-NLS-1$
		new_channel = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		pv.close();
	}
}