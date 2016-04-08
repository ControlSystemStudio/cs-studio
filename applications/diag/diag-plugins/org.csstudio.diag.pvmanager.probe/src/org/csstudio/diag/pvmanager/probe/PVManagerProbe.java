package org.csstudio.diag.pvmanager.probe;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.diirt.datasource.formula.ExpressionLanguage.formula;
import static org.diirt.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.ui.util.widgets.PVFormulaInputBar;
import org.diirt.datasource.PV;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.TimeoutException;
import org.diirt.datasource.expression.DesiredRateReadWriteExpression;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

/**
 * Probe view.
 */
public class PVManagerProbe extends ViewPart {

    public PVManagerProbe() {
    }

    private static final Logger log = Logger.getLogger(PVManagerProbe.class
            .getName());

    // The ID of the view as specified by the extension point
    public static final String VIEW_ID = "org.csstudio.diag.pvmanager.probe"; //$NON-NLS-1$

    // Next secondary view ID, i.e. next instance of probe should use this number.
    // SYNC on PVManagerProbe.class for access
    private static int next_instance = 1;

    private PVFormulaInputBar pvFomulaInputBar;
    private ErrorBar errorBar;

    private Composite mainPanel;
    private ViewerPanel viewerPanel;
    private ValuePanel valuePanel;
    private Composite statusBarPanel;
    private ChangeValuePanel changeValuePanel;
    private MetadataPanel metadataPanel;
    private DetailsPanel detailsPanel;

    private Composite statusBar;
    private Label statusLabel;
    private Label statusField;

    private Action showHideAction;
    private Action copyValueAction;

    private Map<Composite, MenuItem> sectionToMenu = new HashMap<>();

    private String pvFormula;
    private PV<?, Object> pv;

    // Needed to make sure a timeout does not override an exception
    // TODO: put this in pvmanager itself?
    private Exception lastError = null;

    private SimpleDataTextExport export = new SimpleDataTextExport();

    // Memento keys
    private IMemento memento = null;
    private static final String MEMENTO_PVFORMULA_LIST = "pvFormulaList"; //$NON-NLS-1$
    private static final String MEMENTO_PVFORMULA = "pvFormula"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_VIEWER = "showViewer"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_VALUE = "showValue"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_CHANGE_VALUE = "showChangeValue"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_METADATA = "showMetadata"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_DETAILS = "showDetails"; //$NON-NLS-1$

    @Override
    public void init(final IViewSite site, final IMemento memento)
            throws PartInitException {
        super.init(site, memento);

        // For new instances opened while CSS is running,
        // createNewInstance() tracks the secondary view ID.
        // But if this view was 'restored' from a saved workspace,
        // we need to adjust the instance counter to not re-use
        // IDs of restored views.
        int this_instance = 1;
        try
        {
            this_instance = Integer.parseInt(site.getSecondaryId());
        }
        catch (NumberFormatException ex)
        {
            // Ignore, just assume 1
        }
        synchronized (PVManagerProbe.class)
        {
            if (this_instance >= next_instance)
                next_instance = this_instance + 1;
        }

        // Save the memento
        this.memento = memento;
    }

    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        memento.putString(MEMENTO_PVFORMULA, pvFormula);
        memento.putBoolean(MEMENTO_SHOW_VIEWER, sectionToMenu.get(viewerPanel)
                .getSelection());
        memento.putBoolean(MEMENTO_SHOW_VALUE, sectionToMenu.get(valuePanel)
                .getSelection());
        memento.putBoolean(MEMENTO_SHOW_CHANGE_VALUE,
                sectionToMenu.get(changeValuePanel).getSelection());
        memento.putBoolean(MEMENTO_SHOW_METADATA, sectionToMenu.get(metadataPanel)
                .getSelection());
        memento.putBoolean(MEMENTO_SHOW_DETAILS, sectionToMenu.get(detailsPanel)
                .getSelection());
    }

    @Override
	public void createPartControl(Composite parent) {
        GridLayout gl_parent = new GridLayout(1, false);
        gl_parent.verticalSpacing = 0;
        gl_parent.marginWidth = 0;
        gl_parent.marginHeight = 0;
        parent.setLayout(gl_parent);

        pvFomulaInputBar = new PVFormulaInputBar(parent, SWT.None, Activator
                .getDefault().getDialogSettings(), MEMENTO_PVFORMULA_LIST);
        pvFomulaInputBar
                .addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        if ("pvFormula".equals(event.getPropertyName())) { //$NON-NLS-1$
                            setPVFormula((String) event.getNewValue());
                        }
                    }
                });

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pvFomulaInputBar.setLayoutData(gd);

        errorBar = new ErrorBar(parent, SWT.NONE);
        errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        errorBar.setMarginRight(5);
        errorBar.setMarginLeft(5);
        errorBar.setMarginBottom(5);

        ScrolledComposite mainScroll = new ScrolledComposite(parent, SWT.V_SCROLL);
        mainScroll.setExpandHorizontal(true);
        mainScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        mainPanel = new Composite(mainScroll, SWT.NONE) {
            @Override
            public void layout() {
                // TODO Auto-generated method stub
                super.layout();
                mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        };
        GridLayout gl_mainPanel = new GridLayout();
        mainPanel.setLayout(gl_mainPanel);
        mainScroll.setContent(mainPanel);

        viewerPanel = new ViewerPanel(mainPanel, SWT.BORDER);
        viewerPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
                1, 1));
        viewerPanel.setEnabled(false);

        valuePanel = new ValuePanel(mainPanel, SWT.BORDER);
        valuePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        changeValuePanel = new ChangeValuePanel(mainPanel, SWT.BORDER);
        changeValuePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        metadataPanel = new MetadataPanel(mainPanel, SWT.BORDER);
        metadataPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        detailsPanel = new DetailsPanel(mainPanel, SWT.BORDER);
        detailsPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                false, 1, 1));

        // Status bar
        statusBarPanel = new Composite(parent, SWT.NONE);
        GridLayout gl_statusBarPanel = new GridLayout(1, false);
        gl_statusBarPanel.verticalSpacing = 0;
        gl_statusBarPanel.marginWidth = 0;
        gl_statusBarPanel.marginHeight = 0;
        statusBarPanel.setLayout(gl_statusBarPanel);
        statusBarPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));

        Label label = new Label(statusBarPanel, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
                1));
        label.setSize(64, 2);

        statusBar = new Composite(statusBarPanel, SWT.NONE);
        statusBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        statusBar.setLayout(new GridLayout(2, false));

        statusLabel = new Label(statusBar, 0);
        statusLabel.setSize(43, 20);
        statusLabel.setText(Messages.Probe_statusLabelText);

        statusField = new Label(statusBar, SWT.BORDER);
        statusField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        statusField.setSize(326, 22);
        statusField.setText(Messages.Probe_statusWaitingForPV);

        createActions();
        initializeToolBar();

        // Determine initial state
        String initialPVFormula = null;
        boolean showViewer = false;
        boolean showValue = true;
        boolean showChangeValue = true;
        boolean showMetadata = false;
        boolean showDetails = false;

        if (memento != null) {
            initialPVFormula = memento.getString(MEMENTO_PVFORMULA);
            showViewer = nullDefault(memento.getBoolean(MEMENTO_SHOW_VIEWER), showViewer);
            showValue = nullDefault(memento.getBoolean(MEMENTO_SHOW_VALUE),
                    showValue);
            showChangeValue = nullDefault(
                    memento.getBoolean(MEMENTO_SHOW_CHANGE_VALUE), showChangeValue);
            showMetadata = nullDefault(memento.getBoolean(MEMENTO_SHOW_METADATA),
                    showMetadata);
            showDetails = nullDefault(memento.getBoolean(MEMENTO_SHOW_DETAILS),
                    showDetails);
        }
        setPVFormula(initialPVFormula);
        initSection(viewerPanel, showViewer);
        initSection(valuePanel, showValue);
        initSection(changeValuePanel, showChangeValue);
        initSection(metadataPanel, showMetadata);
        initSection(detailsPanel, showDetails);

        parent.layout();
        mainPanel.layout();
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
        setPVFormula(pvName.getName());
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

        valuePanel.changeValue(null, false);
        changeValuePanel.reset();
        metadataPanel.changeValue(null);
        detailsPanel.changeValue(null, null);
        viewerPanel.changeValue(null);
        setLastError(null);
        copyValueAction.setEnabled(false);
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
                    .timeout(Duration.ofMillis(5000),
                            Messages.Probe_retryAfterTimeout)
                    .readListener(new PVReaderListener<Object>() {
                        @Override
                        public void pvChanged(PVReaderEvent<Object> event) {
                            Object value = event.getPvReader().getValue();
                            setLastError(event.getPvReader().lastException());
                            viewerPanel.changeValue(value);
                            if (event.getPvReader().isConnected()) {
                                setStatus(Messages.Probe_statusConnected);
                            } else {
                                setStatus(Messages.Probe_statusSearching);
                            }
                            valuePanel.changeValue(value, event.getPvReader().isConnected());
                            metadataPanel.changeValue(value);
                            copyValueAction.setEnabled(export.canExport(value));
                        }
                    })
                    .readListener(changeValuePanel.getReaderListener())
                    .writeListener(changeValuePanel.getWriterListener())
                    .notifyOn(swtThread(this))
                    .asynchWriteAndMaxReadRate(ofHertz(25));
            changeValuePanel.setPvWriter(pv);
            // Show the PV name as the title
            setPartName(pvFormula);
            detailsPanel.changeValue(expression, pvFormula);
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
	public void setFocus() {
    }

    public static String createNewInstance() {
        synchronized (PVManagerProbe.class)
        {
            return Integer.toString(next_instance++);
        }
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

    private void initializeToolBar() {
        IToolBarManager toolbarManager = getViewSite().getActionBars()
                .getToolBarManager();
        toolbarManager.add(copyValueAction);
        toolbarManager.add(showHideAction);
    }

    private void createActions() {
        // Create the actions
        {
            // Drop down menu to select what to show
            // First selection for All and then each datasource in alphabetical
            // order
            final Menu sectionsMenu = new Menu(pvFomulaInputBar.getParent());
            MenuItem viewerMenuItem = ShowHideForGridLayout
                    .createShowHideMenuItem(sectionsMenu, viewerPanel);
            viewerMenuItem.setText(Messages.Probe_sectionViewer);
            sectionToMenu.put(viewerPanel, viewerMenuItem);
            MenuItem valueMenuItem = ShowHideForGridLayout
                    .createShowHideMenuItem(sectionsMenu, valuePanel);
            valueMenuItem.setText(Messages.Probe_sectionValue);
            sectionToMenu.put(valuePanel, valueMenuItem);
            MenuItem changeValueMenuItem = ShowHideForGridLayout
                    .createShowHideMenuItem(sectionsMenu, changeValuePanel);
            changeValueMenuItem.setText(Messages.Probe_sectionChangeValue);
            sectionToMenu.put(changeValuePanel, changeValueMenuItem);
            MenuItem metadataMenuItem = ShowHideForGridLayout
                    .createShowHideMenuItem(sectionsMenu, metadataPanel);
            metadataMenuItem.setText(Messages.Probe_sectionMetadata);
            sectionToMenu.put(metadataPanel, metadataMenuItem);
            MenuItem detailsMenuItem = ShowHideForGridLayout
                    .createShowHideMenuItem(sectionsMenu, detailsPanel);
            detailsMenuItem.setText(Messages.Probe_sectionDetails);
            sectionToMenu.put(detailsPanel, detailsMenuItem);

            showHideAction = new Action(Messages.Probe_showHideButtonText, SWT.DROP_DOWN) {
                @Override
                public void runWithEvent(Event event) {
                    // Point point = event.
                    ToolItem toolItem = (ToolItem) event.widget;
                    Point point = toolItem.getParent().toDisplay(
                            new Point(toolItem.getBounds().x, toolItem
                                    .getBounds().y
                                    + toolItem.getBounds().height));
                    sectionsMenu.setLocation(point.x, point.y); // waiting
                    sectionsMenu.setVisible(true);
                }
            };
            showHideAction.setImageDescriptor(ResourceManager
                    .getPluginImageDescriptor("org.eclipse.ui", //$NON-NLS-1$
                            "/icons/full/obj16/submenu.gif")); //$NON-NLS-1$

            // showHideAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.csstudio.utility.pvmanager.ui.toolbox",
            // "icons/source.png"));
            showHideAction.setToolTipText(Messages.Probe_showHideButtonToolTipText);
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

        copyValueAction = new Action(Messages.Probe_copyValueToClipboardButtonText, SWT.NONE) {

            @Override
			public void runWithEvent(Event event) {
                try {
                    StringWriter writer = new StringWriter();
                    export.export(pv.getValue(), writer);
                    String text = writer.toString();
                    final Clipboard clipboard = new Clipboard(
                            PlatformUI.getWorkbench().getDisplay());
                        clipboard.setContents(new String[] { text },
                            new Transfer[] { TextTransfer.getInstance() });
                } catch (Exception ex) {
                    ExceptionDetailsErrorDialog.openError(PVManagerProbe.this.mainPanel.getShell(), Messages.Probe_errorCopyValueToClipboard, ex);
                }
            };
        };
        copyValueAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.ui", "/icons/full/etool16/paste_edit.gif")); //$NON-NLS-1$ //$NON-NLS-2$
        copyValueAction.setToolTipText(Messages.Probe_copyValueToClipboardButtonToolTipText);
        copyValueAction.setEnabled(false);
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
