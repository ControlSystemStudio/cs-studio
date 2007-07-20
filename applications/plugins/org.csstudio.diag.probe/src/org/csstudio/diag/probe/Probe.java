package org.csstudio.diag.probe;

import java.text.NumberFormat;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.util.swt.meter.MeterWidget;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Main Eclipse ViewPart of the Probe plug-in.
 *
 * @author Original by Ken Evans (APS)
 * @author Kay Kasemir
 * @author Jan Hatje
 * @author Helge Rickens
 */
public class Probe extends ViewPart implements PVListener
{
    /** Multiple Probe views are allowed.
     *  Their ID has to be ID + ":<instance>"
     */
    public static final String ID = "org.csstudio.diag.probe.Probe"; //$NON-NLS-1$
    private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    private static final String PV_TAG = "PVName"; //$NON-NLS-1$
    public static final boolean debug = false;
    
    /** Instance number, used to create a unique ID
     *  @see #createNewInstance()
     */
    private static int instance = 0;
    
    /** Memento used to preserve the PV name. */
    private IMemento memento = null;

    // GUI
    private ComboViewer cbo_name;
    private ComboHistoryHelper name_helper;
    private Label lbl_value;
    private Label lbl_time;
    private Label lbl_status;
    private MeterWidget meter;

    /** The process variable that we monitor. */
    private PV pv = null;
    
    /** Most recent value of the pv */
    private ValueInfo value = new ValueInfo();
    
    private NumberFormat period_format;
    
    /** Is this a new channel where we never received a value? */
    private boolean new_channel = true;
    
    final Runnable update_value = new Runnable()
    {
        public void run()
        {   // Might run after the view is already disposed...
            if (lbl_value.isDisposed())
                return;
            lbl_value.setText(value.getValueText());
            lbl_time.setText(value.getTimeText());

            INumericMetaData meta = value.getNumericMetaData();
            if (meta == null)
                meter.setEnabled(false);
            else
            {   // Configure on first value from new channel
                if (new_channel)
                {
                    if (meta.getDisplayLow() < meta.getDisplayHigh())
                    {
                        meter.configure(meta.getDisplayLow(),
                                        meta.getAlarmLow(),
                                        meta.getWarnLow(),
                                        meta.getWarnHigh(),
                                        meta.getAlarmHigh(),
                                        meta.getDisplayHigh(),
                                        meta.getPrecision());
                        meter.setEnabled(true);
                    }
                    else
                        meter.setEnabled(false);
                }
                meter.setValue(value.getDouble());
            }
            if (Probe.debug)
                System.out.println("Probe displays " //$NON-NLS-1$
                                + lbl_time.getText()
                                + " " + lbl_value.getText()); //$NON-NLS-1$

            final double period = value.getUpdatePeriod();
            if (period > 0)
                lbl_status.setText(Messages.S_Period
                            + period_format.format(period)
                            + Messages.S_Seconds);
            else
                lbl_status.setText(Messages.S_OK);
            new_channel = false;
        }
    };


    /** Create or re-display a probe view with the given PV name.
     *  <p>
     *  Invoked by the PVpopupAction.
     *
     *  @param pv_name The PV to 'probe'
     *  @return Returns <code>true</code> when successful.
     */
    public static boolean activateWithPV(IProcessVariable pv_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            Probe probe = (Probe) page.showView(ID, createNewInstance(),
                                                IWorkbenchPage.VIEW_ACTIVATE);
            probe.setPVName(pv_name.getName());
            return true;
        }
        catch (Exception e)
        {
            Plugin.logException("activateWithPV", e); //$NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }
    
    /** @return a new view instance */
    public static String createNewInstance()
    {
        ++instance;
        return Integer.toString(instance);
    }

    public Probe()
    {
        period_format = NumberFormat.getNumberInstance();
        period_format.setMinimumFractionDigits(2);
        period_format.setMaximumFractionDigits(2);
    }

    /** ViewPart interface, keep the memento. */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** ViewPart interface, persist state */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, cbo_name.getCombo().getText());
    }

    /** ViewPart interface, create UI. */
    @Override
    public void createPartControl(Composite parent)
    {
        createGUI(parent);

        // Enable 'Drop'
        new ProcessVariableDropTarget(cbo_name.getControl())
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
                setPVName(name.getName());
            }
        };

        // In principle, this could allow 'dragging' of PV names.
        // In practice, however, any mouse click & drag only selects
        // portions of the text and moves the cursor. It won't
        // initiate a 'drag'.
        // Maybe it works on some OS? Maybe there's another magic
        // modifier key to force a 'drag'?
        new ProcessVariableDragSource(cbo_name.getControl(), cbo_name);

        makeContextMenu();
    }

    // ViewPart interface
    @Override
    public void setFocus()
    {
        cbo_name.getCombo().setFocus();
    }

    /** Construct GUI. */
    private void createGUI(Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        // PV Name: ____ name ___________ [Info]
        
        // Meter
        
        // Value  : ____ value ________ [Adjust]
        //
        // ------- status --------

        Label pv_label = new Label(parent, SWT.READ_ONLY);
		pv_label.setText(Messages.S_PVName);
        pv_label.setLayoutData(new GridData());

        cbo_name = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
        cbo_name.getCombo().setToolTipText(Messages.S_EnterPVName);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        cbo_name.getCombo().setLayoutData(gd);
        name_helper = new ComboHistoryHelper(
                        Plugin.getDefault().getDialogSettings(),
                        PV_LIST_TAG, cbo_name)
        {
            @Override
            public void newSelection(String pv_name)
            { 
                setPVName(pv_name);   
            }
        };
        cbo_name.getCombo().addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                disposeChannel();
                name_helper.saveSettings();
            }
        });

        Button btn_info = new Button(parent, SWT.PUSH);
        btn_info.setText(Messages.S_Info);
        btn_info.setToolTipText(Messages.S_ObtainInfo);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL; // but don't grab...
        btn_info.setLayoutData(gd);
        btn_info.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {
                showInfo();
            }
        });

        // New Row
        meter = new MeterWidget(parent, 0);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        meter.setLayoutData(gd);

        // New Row
        Label label = new Label(parent, 0);
        label.setText(Messages.S_Value);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_value = new Label(parent, 0);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_value.setLayoutData(gd);

        Button btn_adjust = new Button(parent, SWT.PUSH);
        btn_adjust.setText(Messages.S_Adjust);
        btn_adjust.setToolTipText(Messages.S_ModValue);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL; // but don't grab...
        btn_adjust.setLayoutData(gd);
        btn_adjust.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {   adjustValue();   }
        });

        // New Row
        label = new Label(parent, 0);
        label.setText(Messages.S_Timestamp);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_time = new Label(parent, 0);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns-1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_time.setLayoutData(gd);

        // Status bar
        label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.verticalAlignment = SWT.BOTTOM;
        gd.grabExcessVerticalSpace = true;
        label.setLayoutData(gd);

        label = new Label(parent, 0);
        label.setText(Messages.S_Status);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_status = new Label(parent, SWT.BORDER);
        lbl_status.setText(Messages.S_Waiting);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_status.setLayoutData(gd);

        name_helper.loadSettings();

        if (memento != null)
        	setPVName(memento.getString(PV_TAG));
    }

    /** Add context menu.
     *  Basically empty, only contains MB_ADDITIONS to allow object contribs.
     *  <p>
     *  TODO: This doesn't work on all platforms.
     *  On Windows, the combo box already comes with a default context menu
     *  for cut/copy/paste/select all/...
     *  Sometimes you see the CSS context menu on right-click,
     *  and sometimes you don't.
     */
    private void makeContextMenu()
    {
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                manager.add(new Separator(
                                IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        Control control = cbo_name.getControl();
        //Control control = pv_label;
        Menu menu = manager.createContextMenu(control);
        control.setMenu(menu);
        getSite().registerContextMenu(manager, cbo_name);
    }

    /** Update the PV name that is probed.
     *  <p>
     *  Opens a new channel, closing any old one first
     *  @param name
     */
    @SuppressWarnings("nls")
    public boolean setPVName(String pv_name)
    {
        if (Probe.debug)
            Plugin.logInfo("setPVName(" + pv_name+ ")");

        // Close a previous channel
        disposeChannel();

        // Reset rest of GUI
        lbl_value.setText("");
        lbl_time.setText("");
        value.reset();
        new_channel = true;
        
        // Check the name
        if (pv_name == null || pv_name.equals(""))
        {
            cbo_name.getCombo().setText("");
            updateStatus(Messages.S_Waiting);
            return false;
        }
        
        name_helper.addEntry(pv_name);
        cbo_name.setSelection(
            new StructuredSelection(
                        CentralItemFactory.createProcessVariable(pv_name)));
        // Update displayed name, unless it's already current
        if (! (cbo_name.getCombo().getText().equals(pv_name)))
            cbo_name.getCombo().setText(pv_name);

        // Create a new channel
        try
        {
            updateStatus(Messages.S_Searching);
            pv = PVFactory.createPV(pv_name);
            pv.addListener(this);
            pv.start();
        }
        catch (Exception ex)
        {
            Plugin.logException(Messages.S_CreateError, ex);
            updateStatus(Messages.S_CreateError + ex.getMessage());
            return false;
        }
        return true;
    }

    // PVListener
    public void pvDisconnected(PV pv)
    {
        updateStatus(Messages.S_Disconnected);
    }

    // PVListener
    public void pvValueUpdate(PV pv)
    {
        if (Probe.debug)
            Plugin.logInfo("Probe pvValueUpdate: " + pv.getName()); //$NON-NLS-1$
        // We might receive events after the view is already disposed....
        if (lbl_value.isDisposed())
            return;
        try
        {
            value.update(pv.getValue());
            // Perform update in GUI thread.
            Display.getDefault().asyncExec(update_value);
        }
        catch (Exception e)
        {
            Plugin.logException("pvValueUpdate error", e); //$NON-NLS-1$
            updateStatus(e.getMessage());
        }
    }

    /** Closes a channel and releases resource */
    private void disposeChannel()
    {
        if (pv != null)
        {
            if (debug)
                System.out.println("Probe: disposeChannel " + pv.getName()); //$NON-NLS-1$
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
    }

    /** Updates the status bar with given string.
     *  <p>
     *  Thread safe.
     */
    private void updateStatus(final String text)
    {
        if (text != null)
        {   // Make it run in the SWT UI thread
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    lbl_status.setText(text);
                }
            });
        }
    }

    /**
     * Info button selection handler
     * @param ev
     */
    private void showInfo()
    {
        final String nl = "\n"; //$NON-NLS-1$

        final StringBuffer info = new StringBuffer();
        if (pv == null)
        {
            info.append(Messages.S_NotConnected + nl);
        }
        else
        {
            info.append(nl + Messages.S_ChannelInfo + "  " + pv.getName() + nl); //$NON-NLS-1$
            if (pv.isConnected())
                info.append(Messages.S_STATEConn + nl);
            else
                info.append(Messages.S_STATEDisconn + nl);
            final IValue value = pv.getValue();
            if (value != null)
            {
                final IMetaData meta = value.getMetaData();
                if (meta != null)
                    info.append(meta.toString());
            }
        }
        if (info.length() == 0)
            info.append(Messages.S_NoInfo);
        MessageBox box =
            new MessageBox(lbl_value.getShell(), SWT.ICON_INFORMATION);
        box.setText(Messages.S_Info);
        box.setMessage(info.toString());
        box.open();
    }

    /** Interactively adjust the PV's value. */
    private void adjustValue()
    {
        try
        {
            if (pv == null)
            {
                updateStatus(Messages.S_NoChannel);
                return;
            }
            if (!pv.isConnected())
            {
                updateStatus(Messages.S_NotConnected);
                return;
            }
            InputDialog inputDialog =
                    new InputDialog(lbl_value.getShell(),
                        Messages.S_AdjustValue, Messages.S_Value,
                        value.getValueText(), null);
                if (inputDialog.open() == Window.OK)
                    pv.setValue(inputDialog.getValue());
        }
        catch (Exception ex)
        {
            Plugin.logException(Messages.S_AdjustFailed, ex);
            updateStatus(Messages.S_AdjustFailed + ex.getMessage());
        }
    }
}
