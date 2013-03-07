package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.util.time.TimeDuration.ofHertz;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.MeterWidget;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.TimeoutException;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.vtype.Display;
import org.epics.vtype.ValueUtil;

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
	private Label statusField;
	private PVFormulaInputBar pvFomulaInputBar;
	private ErrorBar errorBar;
	private MeterWidget meterPanel;
	private Composite topBox;
	private Composite mainSection;
	private GridLayout gl_topBox;
	
	private Map<Composite, MenuItem> sectionToMenu = new HashMap<>();

	/** Currently displayed formula */
	private String pvFormula;

	/** Currently connected formula */
	private PV<?, Object> pv;

	/** Memento used to preserve the PV name. */
	private IMemento memento = null;

	/** Memento tags */
	private static final String MEMENTO_PVFORMULA_LIST = "pvFormulaList"; //$NON-NLS-1$
	private static final String MEMENTO_PVFORMULA = "pvFormula"; //$NON-NLS-1$
	private static final String METER_TAG = "meter"; //$NON-NLS-1$
	private static final String MEMENTO_VALUE = "showValue"; //$NON-NLS-1$
	private static final String MEMENTO_CHANGE_VALUE = "showChangeValue"; //$NON-NLS-1$
	private static final String MEMENTO_METADATA = "showMetadata"; //$NON-NLS-1$
	private static final String MEMENTO_DETAILS = "showDetails"; //$NON-NLS-1$

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
		memento.putString(MEMENTO_PVFORMULA, pvFormula);
		memento.putBoolean(METER_TAG, sectionToMenu.get(meterPanel).getSelection());
		memento.putBoolean(MEMENTO_VALUE, sectionToMenu.get(valuePanel).getSelection());
		memento.putBoolean(MEMENTO_CHANGE_VALUE, sectionToMenu.get(changeValuePanel).getSelection());
		memento.putBoolean(MEMENTO_METADATA, sectionToMenu.get(metadataPanel).getSelection());
		memento.putBoolean(MEMENTO_DETAILS, sectionToMenu.get(detailsPanel).getSelection());
	}

	public void createPartControl(Composite parent) {
		// Create the view
		final boolean canExecute = true;
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);

		topBox = new Composite(parent, 0);
		topBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_mainSection;
		gl_topBox = new GridLayout();
		gl_topBox.marginWidth = 0;
		gl_topBox.marginHeight = 0;
		topBox.setLayout(gl_topBox);

		errorBar = new ErrorBar(parent, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		errorBar.setMarginRight(5);
		errorBar.setMarginLeft(5);
		errorBar.setMarginBottom(5);

		pvFomulaInputBar = new PVFormulaInputBar(topBox, SWT.None, Activator
				.getDefault().getDialogSettings(), MEMENTO_PVFORMULA_LIST);
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
		mainSection = new Composite(parent, SWT.NONE);
		mainSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gl_mainSection = new GridLayout();
		mainSection.setLayout(gl_mainSection);
		
				// New Box with only the meter
				meterPanel = new MeterWidget(mainSection, 0);
				meterPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
				meterPanel.setEnabled(false);
		
		valuePanel = new ValuePanel(mainSection, SWT.BORDER);
		valuePanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		changeValuePanel = new ChangeValuePanel(mainSection, SWT.BORDER);
		changeValuePanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		metadataPanel = new MetadataPanel(mainSection, SWT.BORDER);
		metadataPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		detailsPanel = new DetailsPanel(mainSection, SWT.BORDER);
		detailsPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		statusBarPanel = new Composite(parent, SWT.NONE);
		GridLayout gl_statusBarPanel = new GridLayout(1, false);
		gl_statusBarPanel.verticalSpacing = 0;
		gl_statusBarPanel.marginWidth = 0;
		gl_statusBarPanel.marginHeight = 0;
		statusBarPanel.setLayout(gl_statusBarPanel);
		statusBarPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
						// Status bar
						Label label = new Label(statusBarPanel, SWT.SEPARATOR | SWT.HORIZONTAL);
						label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
						label.setSize(64, 2);
												
												statusBar = new Composite(statusBarPanel, SWT.NONE);
												statusBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
												statusBar.setLayout(new GridLayout(2, false));
												
														statusLabel = new Label(statusBar, 0);
														statusLabel.setSize(43, 20);
														statusLabel.setText(Messages.Probe_statusLabelText);
														
																statusField = new Label(statusBar, SWT.BORDER);
																statusField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
																statusField.setSize(326, 22);
																statusField.setText(Messages.Probe_statusWaitingForPV);
		
		ShowHideForGridLayout.hide(meterPanel);
		createActions();
		initializeToolBar();
		
		// Determine initial state
		String initialPVFormula = null;
		boolean showMeter = false;
		boolean showValue = true;
		boolean showChangeValue = true;
		boolean showMetadata = false;
		boolean showDetails = false;

		if (memento != null) {
			initialPVFormula = memento.getString(MEMENTO_PVFORMULA);
			showMeter = nullDefault(memento.getBoolean(METER_TAG), showMeter);
			showValue = nullDefault(memento.getBoolean(MEMENTO_VALUE), showValue);
			showChangeValue = nullDefault(memento.getBoolean(MEMENTO_CHANGE_VALUE), showChangeValue);
			showMetadata = nullDefault(memento.getBoolean(MEMENTO_METADATA), showMetadata);
			showDetails = nullDefault(memento.getBoolean(MEMENTO_DETAILS), showDetails);
		}
		setPVFormula(initialPVFormula);
		initSection(meterPanel, showMeter);
		initSection(valuePanel, showValue);
		initSection(changeValuePanel, showChangeValue);
		initSection(metadataPanel, showMetadata);
		initSection(detailsPanel, showDetails);

		parent.layout();
	}
	
	private void initSection(Composite section, boolean show) {
		ShowHideForGridLayout.setShow(section, show);
		sectionToMenu.get(section).setSelection(show);
	}
	
	private boolean nullDefault(Boolean value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
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

		valuePanel.changeValue(null);
		changeValuePanel.setPV(null);
		metadataPanel.changeValue(null);
		detailsPanel.changeValue(null, null);
		setMeter(null, null);
		setLastError(null);
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
			DesiredRateReadWriteExpression<?, Object> expression = formula(pvFormula);
			pv = PVManager
					.readAndWrite(expression)
					.timeout(ofMillis(5000),
							"No connection after 5s. Still trying...")
					.readListener(new PVReaderListener<Object>() {
						@Override
						public void pvChanged(PVReaderEvent<Object> event) {
							Object value = event.getPvReader().getValue();
							setLastError(event.getPvReader().lastException());
							setMeter(ValueUtil.numericValueOf(value),
									ValueUtil.displayOf(value));
							if (event.getPvReader().isConnected()) {
								setStatus(Messages.Probe_statusConnected);
							} else {
								setStatus(Messages.Probe_statusSearching);
							}
							valuePanel.changeValue(value);
							metadataPanel.changeValue(value);
						}
					})
					.notifyOn(swtThread(this))
					.asynchWriteAndMaxReadRate(ofHertz(25));
			changeValuePanel.setPV(pv);
			// Show the PV name as the title
			setPartName(pvFormula);
			detailsPanel.changeValue(expression, pvFormula);
		}

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
	private ValuePanel valuePanel;
	private Composite statusBarPanel;

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
	
	private Action showHideAction;
	private ChangeValuePanel changeValuePanel;
	private Composite statusBar;
	private MetadataPanel metadataPanel;
	private DetailsPanel detailsPanel;
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
			MenuItem meterMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, meterPanel);
			meterMenuItem.setText("Meter");
			sectionToMenu.put(meterPanel, meterMenuItem);
			MenuItem valueMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, valuePanel);
			valueMenuItem.setText("Value");
			sectionToMenu.put(valuePanel, valueMenuItem);
			MenuItem changeValueMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, changeValuePanel);
			changeValueMenuItem.setText("Change value");
			sectionToMenu.put(changeValuePanel, changeValueMenuItem);
			MenuItem metadataMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, metadataPanel);
			metadataMenuItem.setText("Metadata");
			sectionToMenu.put(metadataPanel, metadataMenuItem);
			MenuItem detailsMenuItem = ShowHideForGridLayout.createShowHideMenuItem(sectionsMenu, detailsPanel);
			detailsMenuItem.setText("Details");
			sectionToMenu.put(detailsPanel, detailsMenuItem);
			
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
			meterPanel.setEnabled(false);
			// meter.setValue(0.0);
		} else if (display.getUpperDisplayLimit() <= display
				.getLowerDisplayLimit()) {
			meterPanel.setEnabled(false);
			// meter.setValue(0.0);
		} else {
			meterPanel.setEnabled(true);
			meterPanel.setLimits(display.getLowerDisplayLimit(),
					display.getLowerAlarmLimit(),
					display.getLowerWarningLimit(),
					display.getUpperWarningLimit(),
					display.getUpperAlarmLimit(),
					display.getUpperDisplayLimit(), 1);
			meterPanel.setValue(value);
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
}
