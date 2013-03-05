package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.util.time.TimeDuration.ofHertz;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.MeterWidget;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;

/**
 * Probe view.
 */
public class PVManagerProbe extends ViewPart {
	public PVManagerProbe() {
	}

	private static final Logger log = Logger.getLogger(PVManagerProbe.class
			.getName());

	/**
	 * The ID of the view as specified by the extension point
	 */
	public static final String VIEW_ID = "org.csstudio.diag.pvmanager.probe"; //$NON-NLS-1$
	private static int instance = 0;
	private Label statusLabel;
	private Label newValueLabel;
	private Label statusField;
	private PVFormulaInputBar pvFomulaInputBar;
	private ErrorBar errorBar;
	private MeterWidget meter;
	private Composite topBox;
	private Composite bottomBox;
	private Button infoButton;
	private GridLayout gl_topBox;

	private boolean readOnly = true;

	/** Currently displayed formula */
	private String pvFormula;

	/** Currently connected formula */
	private PV<?, Object> pv;

	/** Formatting used for the value text field */
	private ValueFormat valueFormat = new SimpleValueFormat(3);

	/** Formatting used for the time text field */
	private TimestampFormat timeFormat = new TimestampFormat(
			"yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

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
		if (pvFormula != null) {
			memento.putString(PV_TAG, pvFormula);
		}
	}

	public void createPartControl(Composite parent) {
		// Create the view
		final boolean canExecute = true;
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);

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
		topBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_bottomBox;
		gl_topBox = new GridLayout();
		gl_topBox.marginWidth = 0;
		gl_topBox.marginHeight = 0;
		gl_topBox.numColumns = 2;
		topBox.setLayout(gl_topBox);

		errorBar = new ErrorBar(parent, SWT.NONE);
		errorBar.setMarginRight(5);
		errorBar.setMarginLeft(5);
		errorBar.setMarginBottom(5);

		pvFomulaInputBar = new PVFormulaInputBar(topBox, SWT.None, Activator
				.getDefault().getDialogSettings(), PV_LIST_TAG);
		pvFomulaInputBar
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if ("pvFormula".equals(event.getPropertyName())) {
							setPVFormula((String) event.getNewValue());
						}
					}
				});

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		pvFomulaInputBar.setLayoutData(gd);

		// Button Box
		bottomBox = new Composite(parent, 0);
		bottomBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gl_bottomBox = new GridLayout();
		gl_bottomBox.numColumns = 3;
		bottomBox.setLayout(gl_bottomBox);
		
				// New Box with only the meter
				meter = new MeterWidget(bottomBox, 0);
				meter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
				meter.setEnabled(false);
		
		valueBox = new ValueBox(bottomBox, SWT.BORDER);
		valueBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

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
				
						infoButton = new Button(topBox, SWT.PUSH);
						infoButton.setText(Messages.Probe_infoTitle);
						infoButton.setToolTipText(Messages.Probe_infoButtonToolTipText);
						
								// Connect actions
						
								infoButton.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(final SelectionEvent ev) {
										showInfo();
									}
								});
		
		composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
						// Status bar
						Label label = new Label(composite_1, SWT.SEPARATOR | SWT.HORIZONTAL);
						label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
						label.setSize(64, 2);
										
												statusLabel = new Label(composite_1, 0);
												statusLabel.setText(Messages.Probe_statusLabelText);
								
										statusField = new Label(composite_1, SWT.BORDER);
										statusField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
										statusField.setText(Messages.Probe_statusWaitingForPV);

		btn_adjust.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent ev) {
				final boolean enable = btn_adjust.getSelection();
				newValueLabel.setVisible(enable);
				newValueField.setVisible(enable);
				newValueField.setText(valueFormat.format(pv.getValue()));
			}
		});

		newValueField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				pv.write(newValueField.getText());
			}
		});
		
		showMeter(false);		
		createActions();
		initializeToolBar();

		if (memento != null && memento.getString(PV_TAG) != null) {
			setPVFormula(memento.getString(PV_TAG));
			// Per default, the meter is shown.
			// Hide according to memento.
			final String show = memento.getString(METER_TAG);
			if ((show != null) && show.equals("false")) //$NON-NLS-1$
			{
				showMeter(false);
			}
		}
		parent.layout();
	}
	
	private void hideSection(Composite section) {
		GridData data = (GridData) section.getLayoutData();
		if (data.heightHint != 0) {
			data.heightHint = 0;
		}
	}
	
	private void showSection(Composite section) {
		GridData data = (GridData) section.getLayoutData();
		if (data.heightHint != -1) {
			data.heightHint = -1;
		}
	}

	protected void showMeter(final boolean show) {
		if (show) {
			showSection(meter);
		} else {
			hideSection(meter);
		}
		meter.getParent().layout();
//		if (show) { // Meter about to become visible
//			// Attach bottom box to bottom of screen,
//			// and meter stretches between top and bottom box.
//			final FormData fd = new FormData();
//			fd.left = new FormAttachment(0, 0);
//			fd.right = new FormAttachment(100, 0);
//			fd.bottom = new FormAttachment(100, 0);
//			bottomBox.setLayoutData(fd);
//		} else { // Meter about to be hidden.
//			// Attach bottom box to top box.
//			final FormData fd = new FormData();
//			fd.left = new FormAttachment(0, 0);
//			fd.top = new FormAttachment(topBox);
//			fd.right = new FormAttachment(100, 0);
//			bottomBox.setLayoutData(fd);
//		}
//		meter.setVisible(show);
//		meter.getShell().layout(true, true);
	}

	private String pvNameWithDataSource() {
		DataSource defaultDS = PVManager.getDefaultDataSource();
		String pvName = pvFormula;
		if (defaultDS instanceof CompositeDataSource) {
			CompositeDataSource composite = (CompositeDataSource) defaultDS;
			if (!pvName.contains(composite.getDelimiter())) {
				pvName = composite.getDefaultDataSource()
						+ composite.getDelimiter() + pvName;
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
			ChannelHandler handler = PVManager.getDefaultDataSource()
					.getChannels().get(pvNameWithDataSource());

			if (handler != null) {
				SortedMap<String, Object> sortedProperties = new TreeMap<String, Object>(
						handler.getProperties());
				if (!sortedProperties.isEmpty()) {
					info.append("Channel details:").append(nl);
					for (Map.Entry<String, Object> entry : sortedProperties
							.entrySet()) {
						info.append(indent).append(entry.getKey())
								.append(" = ").append(entry.getValue())
								.append(nl);
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
					info.append(Messages.Probe_infoStateDisconnected)
							.append(nl);
				} else {
					info.append(Messages.Probe_infoStateConnected).append(nl);
				}
			}

			if (type != null) {
				info.append(Messages.Probe_infoDataType).append(space)
						.append(type.getSimpleName()).append(nl);
			}

			if (display != null) {
				info.append(Messages.Probe_infoNumericDisplay).append(nl)
						.append(indent)
						.append(Messages.Probe_infoLowDisplayLimit)
						.append(space).append(display.getLowerDisplayLimit())
						.append(nl).append(indent)
						.append(Messages.Probe_infoLowAlarmLimit).append(space)
						.append(display.getLowerAlarmLimit()).append(nl)
						.append(indent).append(Messages.Probe_infoLowWarnLimit)
						.append(space).append(display.getLowerWarningLimit())
						.append(nl).append(indent)
						.append(Messages.Probe_infoHighWarnLimit).append(space)
						.append(display.getUpperWarningLimit()).append(nl)
						.append(indent)
						.append(Messages.Probe_infoHighAlarmLimit)
						.append(space).append(display.getUpperAlarmLimit())
						.append(nl).append(indent)
						.append(Messages.Probe_infoHighDisplayLimit)
						.append(space).append(display.getUpperDisplayLimit())
						.append(nl);
			}

			if (value instanceof org.epics.vtype.Enum) {
				Enum enumValue = (Enum) value;
				info.append(Messages.Probe_infoEnumMetadata).append(space)
						.append(enumValue.getLabels().size()).append(space)
						.append(Messages.Probe_infoLabels).append(nl);
				for (String label : enumValue.getLabels()) {
					info.append(indent).append(label).append(nl);
				}
			}

		}
		if (info.length() == 0) {
			info.append(Messages.Probe_infoNoInfoAvailable);
		}
		final MessageBox box = new MessageBox(valueBox.getShell(),
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
		setPVFormula("'" + pvName.getName() + "'");
	}

	public void setPVFormula(String pvFormula) {
		log.log(Level.FINE, "setPVFormula ({0})", pvFormula); //$NON-NLS-1$

		// If we are already scanning that pv, do nothing
		if (this.pvFormula != null && this.pvFormula.equals(pvFormula)) {
			return;
		}

		this.pvFormula = pvFormula;

		// The PV is different, so disconnect and reset the visuals
		if (pv != null) {
			pv.close();
			pv = null;
		}

		valueBox.changeValue(null);
		setTime(null);
		setMeter(null, null);
		setLastError(null);
		setReadOnly(true);
		// If name is blank, update status to waiting and quit
		if ((pvFormula == null) || pvFormula.trim().isEmpty()) {
			setStatus(Messages.Probe_statusWaitingForPV);
			pvFormula = null;
		}

		// Update displayed name, unless it's already current
		if (!(Objects.equals(pvFomulaInputBar.getPVFormula(), pvFormula))) {
			pvFomulaInputBar.setPVFormula(pvFormula);
		}

		if (pvFormula != null) {
			setStatus(Messages.Probe_statusSearching);
			pv = PVManager
					.readAndWrite(formula(pvFormula))
					.timeout(ofMillis(5000),
							"No connection after 5s. Still trying...")
					.readListener(new PVReaderListener<Object>() {
						@Override
						public void pvChanged(PVReaderEvent<Object> event) {
							Object obj = event.getPvReader().getValue();
							setLastError(event.getPvReader().lastException());
							setTime(ValueUtil.timeOf(obj));
							setMeter(ValueUtil.numericValueOf(obj),
									ValueUtil.displayOf(obj));
							if (event.getPvReader().isConnected()) {
								setStatus(Messages.Probe_statusConnected);
							} else {
								setStatus(Messages.Probe_statusSearching);
							}
							valueBox.changeValue(obj);
						}
					})
					.writeListener(new PVWriterListener<Object>() {
						@Override
						public void pvChanged(PVWriterEvent<Object> event) {
							Exception lastException = event.getPvWriter()
									.lastWriteException();
							setReadOnly(!event.getPvWriter().isWriteConnected());
						}
					}).notifyOn(swtThread(this))
					.asynchWriteAndMaxReadRate(ofHertz(25));
			// Show the PV name as the title
			setPartName(pvFormula);
		}

	}

	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	// public ProcessVariable getPVName() {
	// return this.PVName;
	// }

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
	 * @param status
	 *            new status to be displayed
	 */
	private void setStatus(String status) {
		if (status == null) {
			statusField.setText(Messages.Probe_statusWaitingForPV);
		} else {
			statusField.setText(status);
		}
	}

	private Exception lastError = null;
	private ValueBox valueBox;
	private Composite composite_1;

	/**
	 * Displays the last error in the status.
	 * 
	 * @param ex
	 *            an exception
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
	 * Displays the new alarm.
	 * 
	 * @param alarm
	 *            a new alarm
	 */
	private String alarmToString(Alarm alarm) {
		if (alarm == null
				|| alarm.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
			return ""; //$NON-NLS-1$
		} else {
			return "[" + alarm.getAlarmSeverity() + " - " //$NON-NLS-1$
					+ alarm.getAlarmName() + "]";
		}
	}

	/**
	 * Displays the new time.
	 * 
	 * @param time
	 *            a new time
	 */
	private void setTime(Time time) {
//		if (time == null) {
//			timestampField.setText(""); //$NON-NLS-1$
//		} else {
//			timestampField.setText(timeFormat.format(time.getTimestamp()));
//		}
	}
	
	private Action showHideAction;
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(showHideAction);
	}

	private void createActions() {
		// Create the actions
		{
			// Drop down menu to select what to show
			// First selection for All and then each datasource in alphabetical order
			final Menu sectionsMenu = new Menu(topBox);
			MenuItem meterMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, meter);
			meterMenuItem.setText("Meter");
			
			showHideAction = new Action("Show/Hide", SWT.DROP_DOWN) {
				@Override
				public void runWithEvent(Event event) {
					//Point point = event.
					ToolItem toolItem = (ToolItem) event.widget;
					Point point = toolItem.getParent().toDisplay(new Point(toolItem.getBounds().x, toolItem.getBounds().y + toolItem.getBounds().height));
					sectionsMenu.setLocation(point.x, point.y); // waiting
					sectionsMenu.setVisible(true);
				}
			};
			showHideAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.ui", "/icons/full/obj16/submenu.gif"));

//			showHideAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox", "icons/source.png"));
			showHideAction.setToolTipText("Show/Hide");
			showHideAction.setMenuCreator(new IMenuCreator() {
				
				
				
				@Override
				public Menu getMenu(Menu parent) {
					return sectionsMenu;
				}
				
				@Override
				public Menu getMenu(Control parent) {
					// TODO Auto-generated method stub
					return sectionsMenu;
				}
				
				@Override
				public void dispose() {
					sectionsMenu.dispose();
				}
			});
		}
	}
	
	/**
	 * Displays a new value in the meter.
	 * 
	 * @param value
	 *            the new value
	 * @param display
	 *            the display information
	 */
	private void setMeter(Double value, Display display) {
		if (value == null || display == null
				|| !ValueUtil.displayHasValidDisplayLimits(display)) {
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
