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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
    final public static String ID = "org.csstudio.diag.probe.Probe"; //$NON-NLS-1$

    /** Memento tag */
    final private static String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    /** Memento tag */
    final private static String PV_TAG = "PVName"; //$NON-NLS-1$
    /** Memento tag */
    final private static String METER_TAG = "meter"; //$NON-NLS-1$
    
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
            Plugin.getLogger().debug("Probe displays " //$NON-NLS-1$
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
    private Composite top_box;
    private Composite bottom_box;
    private Button show_meter;


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
            Plugin.getLogger().error("activateWithPV", e); //$NON-NLS-1$
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
        memento.putString(METER_TAG,
                        Boolean.toString(show_meter.getSelection()));
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
    private void createGUI(final Composite parent)
    {
        final FormLayout layout = new FormLayout();
        parent.setLayout(layout);

        // 3 Boxes, connected via form layout: Top, meter, bottom
        //
        // PV Name: ____ name ___________ [Info]
        //              Meter
        // Value     : ____ value ________ [Adjust]
        // Timestamp : ____ time ________  [x] meter
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

        bottom_box = new Composite(parent, 0);
        grid = new GridLayout();
        grid.numColumns = 3;
        bottom_box.setLayout(grid);
        
        label = new Label(bottom_box, 0);
        label.setText(Messages.S_Value);
        label.setLayoutData(new GridData());
        
        lbl_value = new Label(bottom_box, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        lbl_value.setLayoutData(gd);

        final Button btn_adjust = new Button(bottom_box, SWT.PUSH);
        btn_adjust.setText(Messages.S_Adjust);
        btn_adjust.setToolTipText(Messages.S_ModValue);
        btn_adjust.setLayoutData(new GridData());

        // New Row
        label = new Label(bottom_box, 0);
        label.setText(Messages.S_Timestamp);
        label.setLayoutData(new GridData());

        lbl_time = new Label(bottom_box, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        lbl_time.setLayoutData(gd);

        show_meter = new Button(bottom_box, SWT.CHECK);
        show_meter.setText(Messages.S_Meter);
        show_meter.setToolTipText(Messages.S_Meter_TT);
        show_meter.setSelection(true);
        show_meter.setLayoutData(new GridData());
        
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

        btn_info.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {
                showInfo();
            }
        });

        btn_adjust.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {   adjustValue();   }
        });

        show_meter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent ev)
            {   showMeter(show_meter.getSelection());   }
        });

        name_helper.loadSettings();

        if (memento != null)
        {
        	setPVName(memento.getString(PV_TAG));
        	// Per default, the meter is shown.
        	// Hide according to memento.
        	final String show = memento.getString(METER_TAG);
        	if (show != null  &&  show.equals("false")) //$NON-NLS-1$
        	{
        	    show_meter.setSelection(false);
        	    showMeter(false);
        	}
        }
    }

    /** Show or hide the meter */
    protected void showMeter(final boolean show)
    {
        if (show)
        {   // Meter about to become visible
            // Attach bottom box to bottom of screen,
            // and meter stretches between top and bottom box.
            FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.right = new FormAttachment(100, 0);
            fd.bottom = new FormAttachment(100, 0);
            bottom_box.setLayoutData(fd);
        }
        else
        {   // Meter about to be hidden.
            // Attach bottom box to top box.
            FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.top = new FormAttachment(top_box);
            fd.right = new FormAttachment(100, 0);
            bottom_box.setLayoutData(fd);
        }
        meter.setVisible(show);
        meter.getShell().layout(true, true);
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
        Plugin.getLogger().debug("setPVName(" + pv_name+ ")");

        // Close a previous channel
        disposeChannel();

        // Reset rest of GUI
        lbl_value.setText("");
        lbl_time.setText("");
        value.reset();
        meter.setEnabled(false);
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
            Plugin.getLogger().error(Messages.S_CreateError, ex);
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
        Plugin.getLogger().debug("Probe pvValueUpdate: " + pv.getName()); //$NON-NLS-1$
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
            Plugin.getLogger().error("pvValueUpdate error", e); //$NON-NLS-1$
            updateStatus(e.getMessage());
        }
    }

    /** Closes a channel and releases resource */
    private void disposeChannel()
    {
        if (pv != null)
        {
            Plugin.getLogger().debug("Probe: disposeChannel " + pv.getName()); //$NON-NLS-1$
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
            Plugin.getLogger().error(Messages.S_AdjustFailed, ex);
            updateStatus(Messages.S_AdjustFailed + ex.getMessage());
        }
    }
}
