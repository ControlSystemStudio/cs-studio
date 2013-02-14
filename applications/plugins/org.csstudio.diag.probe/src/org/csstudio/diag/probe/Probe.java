/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.probe;

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.ui.swt.ComboHistoryHelper;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.util.swt.meter.MeterWidget;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * Main Eclipse ViewPart of the Probe plug-in.
 *
 * @author Original by Ken Evans (APS)
 * @author Kay Kasemir
 * @author Jan Hatje
 * @author Helge Rickens
 * @author Joerg Rathlev
 */
public class Probe extends ViewPart implements PVListener, ISelectionProvider
{
    final private static Logger logger = Logger.getLogger(Activator.ID);

    /** Multiple Probe views are allowed.
     *  Their ID has to be ID + ":<instance>"
     */
    public static final String ID = "org.csstudio.diag.probe.Probe"; //$NON-NLS-1$

    /** Memento tag */
    private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    /** Memento tag */
    private static final String PV_TAG = "PVName"; //$NON-NLS-1$
    /** Memento tag */
    private static final String METER_TAG = "meter"; //$NON-NLS-1$

	/**
	 * Id of the save value command.
	 */
	private static final String SAVE_VALUE_COMMAND_ID =
		"org.csstudio.platform.ui.commands.saveValue"; //$NON-NLS-1$
	/**
	 * Id of the PV parameter to the save value command.
	 */
	private static final String PV_PARAMETER_ID =
		"org.csstudio.platform.ui.commands.saveValue.pv"; //$NON-NLS-1$
	/**
	 * Id of the value parameter to the save value command.
	 */
	private static final String VALUE_PARAMETER_ID =
		"org.csstudio.platform.ui.commands.saveValue.value"; //$NON-NLS-1$

    private static final String SECURITY_ID = "operating"; //$NON-NLS-1$

	/** Instance number, used to create a unique ID
     *  @see #createNewInstance()
     */
    private static int instance = 0;

    /** Memento used to preserve the PV name. */
    private IMemento memento = null;

    // GUI
    private Combo cbo_name;
    private ComboHistoryHelper name_helper;
    private Label lbl_value;
    private Label lbl_time;
    private Label lbl_status;
    private MeterWidget meter;

    /** The process variable that we monitor. */
    private PV pv = null;

    /** Most recent value of the pv */
    private final ValueInfo value = new ValueInfo();

    private final NumberFormat period_format;

    /** Is this a new channel where we never received a value? */
    private boolean new_channel = true;

    final Runnable update_value = new Runnable()
    {
        @Override
        public void run()
        {   // Might run after the view is already disposed...
            if (lbl_value.isDisposed()) {
                return;
            }
            lbl_value.setText(value.getValueDisplayText());
            lbl_time.setText(value.getTimeText());
            new_value.setText(value.getValueDisplayText());

            final INumericMetaData meta = value.getNumericMetaData();
            if (meta == null) {
                meter.setEnabled(false);
            } else
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
                    } else {
                        meter.setEnabled(false);
                    }
                }
                meter.setValue(value.getDouble());
            }
            logger.log(Level.FINE, "Probe displays {0} {1}", //$NON-NLS-1$
                    new Object[] { lbl_time.getText(),  lbl_value.getText() });

            final double period = value.getUpdatePeriod();
            if (period > 0) {
                lbl_status.setText(Messages.S_Period
                            + period_format.format(period)
                            + Messages.S_Seconds);
            } else {
                lbl_status.setText(Messages.S_OK);
            }
            new_channel = false;
        }
    };
    private Composite top_box;
    private Composite bottom_box;
    private Button show_meter;
    private Button btn_save_to_ioc;
    private ICommandListener saveToIocCmdListener;

    private Text new_value;


    /** Create or re-display a probe view with the given PV name.
     *  <p>
     *  Invoked by the PVpopupAction.
     *
     *  @param pv_name The PV to 'probe'
     *  @return Returns <code>true</code> when successful.
     */
    public static boolean activateWithPV(final ProcessVariable pv_name)
    {
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            final Probe probe = (Probe) page.showView(ID, createNewInstance(),
                                                IWorkbenchPage.VIEW_ACTIVATE);
            if (pv_name != null)
                probe.setPVName(pv_name.getName());
            return true;
        }
        catch (final Exception e)
        {
            logger.log(Level.SEVERE, "Cannot activate probe", e); //$NON-NLS-1$
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
    public void init(final IViewSite site, final IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** ViewPart interface, persist state */
    @Override
    public void saveState(final IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, cbo_name.getText());
        memento.putString(METER_TAG,
                        Boolean.toString(show_meter.getSelection()));
    }

    /** ViewPart interface, create UI. */
    @Override
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);

        // TODO Enable 'Drop'
//        new ProcessVariableDropTarget(cbo_name)
//        {
//            @Override
//            public void handleDrop(final IProcessVariable name,
//                                   final DropTargetEvent event)
//            {
//                setPVName(name.getName());
//            }
//        };

        // In principle, this could allow 'dragging' of PV names.
        // In practice, however, any mouse click & drag only selects
        // portions of the text and moves the cursor. It won't
        // initiate a 'drag'.
        // Maybe it works on some OS? Maybe there's another magic
        // modifier key to force a 'drag'?
//        new ProcessVariableDragSource(cbo_name, this);

        makeContextMenu();
    }

    // ViewPart interface
    @Override
    public void setFocus()
    {
        cbo_name.setFocus();
    }

    /** Construct GUI. */
    private void createGUI(final Composite parent)
    {
        final boolean canExecute = SecurityFacade.getInstance().canExecute(SECURITY_ID, true);
        final FormLayout layout = new FormLayout();
        parent.setLayout(layout);

        // 3 Boxes, connected via form layout: Top, meter, bottom
        //
        // PV Name: ____ name ____________________ [Info]
        // +---------------------------------------------------+
        // |                    Meter                          |
        // +---------------------------------------------------+
        // Value     : ____ value ________________ [x] meter
        // Timestamp : ____ time _________________ [Save to IOC]
        //                                         [x] Adjust
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

        cbo_name = new Combo(top_box, SWT.SINGLE | SWT.BORDER);
        cbo_name.setToolTipText(Messages.S_EnterPVName);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        cbo_name.setLayoutData(gd);

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
        new_value.setText(value.getValueDisplayText());

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
        name_helper = new ComboHistoryHelper(
                        Activator.getDefault().getDialogSettings(),
                        PV_LIST_TAG, cbo_name)
        {
            @Override
            public void newSelection(final String pv_name)
            {
                setPVName(pv_name);
            }
        };

        cbo_name.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(final DisposeEvent e)
            {
                setPV(null);
                name_helper.saveSettings();
            }
        });

        btn_info.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent ev)
            {
                showInfo();
            }
        });

        btn_adjust.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent ev)
            {
                final boolean enable = btn_adjust.getSelection();
                new_value_label.setVisible(enable);
                new_value.setVisible(enable);
            }
        });

        new_value.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                adjustValue(new_value.getText().trim());
            }
        });

        btn_save_to_ioc.addSelectionListener(new SelectionAdapter()
        {
        	@Override
        	public void widgetSelected(final SelectionEvent e)
        	{
        		saveToIoc();
        	}
        });
        // Create a listener to enable/disable the Save to IOC button based on
        // the availability of a command handler.
        saveToIocCmdListener = new ICommandListener()
        {
			@Override
            public void commandChanged(final CommandEvent commandEvent)
			{
				if (commandEvent.isEnabledChanged())
				{
					btn_save_to_ioc.setVisible(
							commandEvent.getCommand().isEnabled());
				}
			}
        };
        // Set the initial vilibility of the button
        updateSaveToIocButtonVisibility();

        show_meter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent ev)
            {   showMeter(show_meter.getSelection());   }
        });

        name_helper.loadSettings();

        if (memento != null)
        {
        	setPVName(memento.getString(PV_TAG));
        	// Per default, the meter is shown.
        	// Hide according to memento.
        	final String show = memento.getString(METER_TAG);
        	if ((show != null)  &&  show.equals("false")) //$NON-NLS-1$
        	{
        	    show_meter.setSelection(false);
        	    showMeter(false);
        	}
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
	   	if (saveToIocCmdListener != null) {
    		final Command svc = getSaveValueCommand();
    		svc.removeCommandListener(saveToIocCmdListener);
    	}
    }

    /**
     * Saves the current value to the IOC.
     */
    private void saveToIoc()
    {
		final IHandlerService handlerService =
			(IHandlerService) getSite().getService(IHandlerService.class);
		try {
			final ParameterizedCommand cmd = createParameterizedSaveValueCommand();
			handlerService.executeCommand(cmd, null);
		} catch (final ExecutionException e) {
			// Execution of the command handler failed.
		    logger.log(Level.SEVERE, "Error executing save value command.", e); //$NON-NLS-1$
			MessageDialog.openError(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocExecutionError);
		} catch (final NotDefinedException e) {
			// Thrown if the command or one of the parameters is undefined.
			// This should never happen (the command id is defined in the
			// platform). Log an error, disable the button, and return.
            logger.log(Level.SEVERE, "Save value command is not defined.", e); //$NON-NLS-1$
			MessageDialog.openError(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotDefinedError);
			btn_save_to_ioc.setEnabled(false);
		} catch (final NotEnabledException e) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotEnabled);
			updateSaveToIocButtonVisibility();
		} catch (final NotHandledException e) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.S_ErrorDialogTitle,
					Messages.S_SaveToIocNotEnabled);
			updateSaveToIocButtonVisibility();
		}
	}

	/**
	 * Updates the visibility state of the Save to IOC button.
	 */
	private void updateSaveToIocButtonVisibility()
	{
		btn_save_to_ioc.setVisible(getSaveValueCommand().isEnabled());
	}

	/**
	 * Creates a save value command parameterized for saving the currently
	 * displayed value.
	 *
	 * @return the parameterized command.
	 * @throws NotDefinedException
	 *             if one of the parameter ids is undefined (this should never
	 *             happen).
	 */
	private ParameterizedCommand createParameterizedSaveValueCommand()
			throws NotDefinedException
	{
		final Command saveValueCommand = getSaveValueCommand();
		final IParameter pvParamter = saveValueCommand.getParameter(PV_PARAMETER_ID);
		final Parameterization pvParameterization = new Parameterization(
				pvParamter, pv.getName());
		final IParameter valueParameter = saveValueCommand.getParameter(VALUE_PARAMETER_ID);
		final Parameterization valueParameterization = new Parameterization(
				valueParameter, value.getValueString());
		final ParameterizedCommand cmd =
			new ParameterizedCommand(saveValueCommand,
					new Parameterization[] { pvParameterization, valueParameterization });
		return cmd;
	}

	/**
	 * Returns the save value command.
	 *
	 * @return the save value command.
	 */
	private Command getSaveValueCommand()
	{
		final ICommandService commandService =
			(ICommandService) getSite().getService(ICommandService.class);
		return commandService.getCommand(SAVE_VALUE_COMMAND_ID);
	}

	/** Show or hide the meter */
    protected void showMeter(final boolean show)
    {
        if (show)
        {   // Meter about to become visible
            // Attach bottom box to bottom of screen,
            // and meter stretches between top and bottom box.
            final FormData fd = new FormData();
            fd.left = new FormAttachment(0, 0);
            fd.right = new FormAttachment(100, 0);
            fd.bottom = new FormAttachment(100, 0);
            bottom_box.setLayoutData(fd);
        }
        else
        {   // Meter about to be hidden.
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
        final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.addMenuListener(new IMenuListener()
        {
            @Override
            public void menuAboutToShow(final IMenuManager manager)
            {
                manager.add(new Separator(
                                IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        //Control control = pv_label;
        final Menu menu = manager.createContextMenu(cbo_name);
        cbo_name.setMenu(menu);
        getSite().registerContextMenu(manager, this);
    }

    /** Update the PV name that is probed.
     *  <p>
     *  Opens a new channel, closing any old one first
     *  @param name
     */
    @SuppressWarnings("nls")
    public boolean setPVName(final String pv_name)
    {
        logger.log(Level.FINE, "setPVName {0}", pv_name); //$NON-NLS-1$

        // Reset GUI
        lbl_value.setText("");
        lbl_time.setText("");
        value.reset();
        new_value.setText("");
        meter.setEnabled(false);
        new_channel = true;

        // Check the name
        if ((pv_name == null) || pv_name.equals(""))
        {
            cbo_name.setText("");
            updateStatus(Messages.S_Waiting);
            setPV(null);
            return false;
        }

        name_helper.addEntry(pv_name);
        // Update displayed name, unless it's already current
        if (! (cbo_name.getText().equals(pv_name)))
            cbo_name.setText(pv_name);

        // Create a new channel
        try
        {
            updateStatus(Messages.S_Searching);

            final PV new_pv = PVFactory.createPV(pv_name);
            setPV(new_pv);
            new_pv.addListener(this);
            new_pv.start();
        }
        catch (final Exception ex)
        {
            updateStatus(Messages.S_CreateError + ex.getMessage());
            return false;
        }
        return true;
    }

    // PVListener
    @Override
    public void pvDisconnected(final PV pv)
    {
        updateStatus(Messages.S_Disconnected);
    }

    // PVListener
    @Override
    public void pvValueUpdate(final PV pv)
    {
        logger.log(Level.FINE, "Probe pvValueUpdate: {0}", pv.getName()); //$NON-NLS-1$

        // We might receive events after the view is already disposed or we're already looking at a different PV ....
        if (pv != this.pv  ||  lbl_value.isDisposed())
            return;
        try
        {
            final IValue newVal = pv.getValue();
            value.update(newVal);
            // Perform update in GUI thread.
            Display.getDefault().asyncExec(update_value);
        }
        catch (final Exception e)
        {
            updateStatus(e.getMessage());
        }
    }

    /** Update the PV, closing the previously used PV
     *  @param new_pv New PV, may be <code>null</code>
     */
    private synchronized void setPV(final PV new_pv)
    {
    	if (pv != null)
    	{
            logger.log(Level.FINE, "Probe: disposeChannel {0}", pv.getName()); //$NON-NLS-1$
            pv.removeListener(this);
	        pv.stop();
    	}
    	pv = new_pv;
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
                @Override
                public void run()
                {
                    if (! lbl_status.isDisposed()) {
                        lbl_status.setText(text);
                    }
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
            if (pv.isConnected()) {
                info.append(Messages.S_STATEConn + nl);
            } else {
                info.append(Messages.S_STATEDisconn + nl);
            }
            final IValue value = pv.getValue();
            if (value != null)
            {
                final IMetaData meta = value.getMetaData();
                if (meta != null) {
                    info.append(meta.toString());
                }
            }
        }
        if (info.length() == 0) {
            info.append(Messages.S_NoInfo);
        }
        final MessageBox box =
            new MessageBox(lbl_value.getShell(), SWT.ICON_INFORMATION);
        box.setText(Messages.S_Info);
        box.setMessage(info.toString());
        box.open();
    }

    /** Interactively adjust the PV's value. */
    private void adjustValue(final String new_value)
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
            pv.setValue(new_value);
        }
        catch (final Throwable ex)
        {
            updateStatus(Messages.S_AdjustFailed + ex.getMessage());
        }
    }

    /** Minimal ISelectionProvider */
	@Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
	    // Not implemented
    }

    /** Minimal ISelectionProvider */
	@Override
    public void removeSelectionChangedListener(
            ISelectionChangedListener listener)
    {
	    // Not implemented
    }

    /** Provide PV name for context menu object contributions
     *  ISelectionProvider
     */
	@Override
    public ISelection getSelection()
    {
		return new StructuredSelection(new ProcessVariable(cbo_name.getText()));
    }

    /** ISelectionProvider */
	@Override
    public void setSelection(ISelection selection)
    {
	    // Not implemented
    }
}
