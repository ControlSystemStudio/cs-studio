/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.alarm.beast.msghist.Activator;
import org.csstudio.alarm.beast.msghist.Preferences;
import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.csstudio.alarm.beast.msghist.model.ModelListener;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * SWT GUI for Model: Table of messages.
 *
 * @author Kay Kasemir
 * @author benhadj naceur @  sopra group - iter
 */
@SuppressWarnings("nls")
public class GUI implements ModelListener, DisposeListener
{
	
	/** The model. */
	final private Model model;

    /** Properties for the table columns. */
    private String properties[];
    
    /** The table_viewer. */
    private TableViewer table_viewer;
    
    /** The end. */
    private Text start, end;
    
    /** The auto refresh. */
    private Button times, filter, refresh, autoRefresh;

    /** The time unit. */
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    
    /** The image refresh button */
    private Image imageAutoRefreshRun, imageManualRefresh = null;
    
    /** The current auto refresh period (milliseconds). */
    private long autoRefreshCurrentPeriod = 0;
    
    /** The auto refresh current start.  */
    private AutoRefreshCurrentState autoRefreshStatus = AutoRefreshCurrentState.STOPPED;
    
    /** The auto refresh enable msg. */
    private String autoRefreshEnableMsg = "auto-refresh is stopped";

    /** The auto refresh disable msg. */
    private String autoRefreshDisableMsg = "automatic refresh at ";
    
    /** The end time. */
    private String endTime = "now";
    
    /** The log info msg auto refresh started. */
    private String LOG_INFO_MSG_AUTO_REFRESH_STARTED = "auto refresh at " ;
    
    /** The log info msg auto refresh stopped. */
    private String LOG_INFO_MSG_AUTO_REFRESH_STOPPED = "auto refresh is stopped ";
    
    /** The log info msg auto refresh condition not verified. */
    @SuppressWarnings("unused")
	private String LOG_INFO_MSG_AUTO_REFRESH_CONDITION_NOT_VERIFIED = "cannot start auto refresh one of conditions are not verified ";
    
    /** The timer auto refresh. */
    private Timer timerAutoRefresh = new Timer("");
    
    /** The property change listener. */
    private IPropertyChangeListener iPropertyChangeListener = null;
    
    /** The archive rdb store. */
    private IPreferenceStore archiveRDBStore = null;
    
    /**
     * Construct GUI.
     *
     * @param site Workbench site or <code>null</code>.
     * @param parent Parent widget/shell
     * @param model Model to display in GUI
     */
    public GUI(IWorkbenchPartSite site, final Composite parent, final Model model)
    {
        this.model = model;
        this.imageAutoRefreshRun = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/pause.gif").createImage();
        this.imageManualRefresh = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, "icons/refresh.gif").createImage();
        this.autoRefreshCurrentPeriod = Preferences.getAutoRefreshPeriod();
        try
        {
            createGUI(parent);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(parent.getShell(), "Error",
                "Initialization error: " + ex.getMessage());
            return;
        }

        model.addListener(this);

        connectGUIActions();

        connectContextMenu(site);
        
		iPropertyChangeListener = new IPropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange
			 * (org.eclipse.jface.util.PropertyChangeEvent)
			 */
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if (Preferences.AUTO_REFRESH_PERIOD.equals(propertyChangeEvent
						.getProperty())) {
					long newAutoRefreshPeriod = Preferences
							.getAutoRefreshPeriod();
					if (newAutoRefreshPeriod == 0) { // error case => execute stop auto-refresh
						autoRefreshCurrentPeriod = newAutoRefreshPeriod;
						stopAutoRefresh();
					} else if (newAutoRefreshPeriod != autoRefreshCurrentPeriod
							&& newAutoRefreshPeriod > 0) {
						if (AutoRefreshCurrentState.STARTED
								.equals(autoRefreshStatus)) { // if auto-refresh is running
							restartAutoRefresh(newAutoRefreshPeriod, true); // restart with the new refresh period
							return;
						}
						// start auto-refresh and change its current state
						autoRefreshCurrentPeriod = newAutoRefreshPeriod;
						startAutoRefresh();
					}
				}
			}
		};

        //listener on preferences
		archiveRDBStore = new ScopedPreferenceStore(
    			InstanceScope.INSTANCE, org.csstudio.alarm.beast.msghist.Activator.ID);
    	archiveRDBStore.addPropertyChangeListener(iPropertyChangeListener);

        // Publish the current selection to the site
        // (to allow context menu extensions based on the selection)
        if (site != null)
        	site.setSelectionProvider(table_viewer);
        
    }

    /**
     * Gets the selection provider.
     *
     * @return Table which provides the currently selected message
     */
	public ISelectionProvider getSelectionProvider()
	{
		return table_viewer;
	}

	/**
	 * Update Model's time range, display exception in dialog box.
	 * If all goes well, GUI should update in response to model's
	 * update event.
	 *
	 * @param start_spec the start_spec
	 * @param end_spec the end_spec
	 */
    private void updateTimeRange(final String start_spec, final String end_spec)
    {
        try
        {
            model.setTimerange(start_spec, end_spec);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(times.getShell(),
                    "Error",
                    "Error in start/end times:\n" + ex.getMessage());
        }
    }

    /** Update Model's filter, display exception in dialog box.
     *  If all goes well, GUI should update in response to model's
     *  update event.
     */
    private void updateFilters()
    {
        final FilterDialog dlg = new FilterDialog(filter.getShell(),
                properties, model.getFilters());
        if (dlg.open() != Window.OK)
            return;
        try
        {
            model.setFilters(dlg.getFilters());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(times.getShell(), "Error",
                    "Error in filter:\n" + ex.getMessage());
        }
    }

    /**
     * Create GUI elements.
     *
     * @param parent Parent shell/site/window
     * @throws Exception on error
     */
    private void createGUI(final Composite parent) throws Exception
    {
    	GridLayout layout = new GridLayout();
        layout.numColumns = 8;
        parent.setLayout(layout);
        GridData gd;

        // Start: ___start__  End: ___end___ [Times] [Filter]
        Label l = new Label(parent, 0);
        l.setText("Start:");
        l.setLayoutData(new GridData());

        start = new Text(parent, SWT.BORDER);
        start.setToolTipText("Enter start time");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        start.setLayoutData(gd);

        l = new Label(parent, 0);
        l.setText("End:");
        l.setLayoutData(new GridData());

        end = new Text(parent, SWT.BORDER);
        end.setToolTipText("Enter end time");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end.setLayoutData(gd);

        times = new Button(parent, SWT.PUSH);
        times.setText("Times");
        times.setToolTipText("Configure time range");
        times.setLayoutData(new GridData());

        filter = new Button(parent, SWT.PUSH);
        filter.setText("Filter");
        filter.setToolTipText("Configure filters");
        filter.setLayoutData(new GridData());
        
        refresh = new Button(parent, SWT.PUSH | SWT.NO_FOCUS);
        refresh.setImage(imageManualRefresh);
        refresh.setToolTipText("Manual refresh");
        refresh.setLayoutData(new GridData());
        
        autoRefresh = new Button(parent, SWT.TOGGLE);
        autoRefresh.setImage(imageAutoRefreshRun);
        autoRefresh.setSelection(true);
		autoRefresh.setToolTipText(autoRefreshEnableMsg);
        autoRefresh.setLayoutData(new GridData());

        // New row: Table of messages
        // TableColumnLayout requires the TableViewer to be in its own Composite
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        // Auto-size table columns
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        table_viewer = new TableViewer(table_parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table_viewer.setContentProvider(new MessageContentProvider());
        ColumnViewerToolTipSupport.enableFor(table_viewer);

        // Columns display message properties
        final PropertyColumnPreference[] col_pref =
        						Preferences.getPropertyColumns();
        properties = new String[col_pref.length];
        for (int i=0; i<col_pref.length; ++i)
        {
            properties[i] = col_pref[i].getName();
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            final TableColumn table_col = view_col.getColumn();
            table_col.setText(col_pref[i].getName());
            table_col.setMoveable(true);
            table_layout.setColumnData(table_col,
                    new ColumnWeightData(col_pref[i].getWeight(), col_pref[i].getSize()));
            // Seq, ID columns are special
            if (properties[i].equalsIgnoreCase(Message.SEQ))
            {
                view_col.setLabelProvider(new SeqProvider());
                // Sort numerically by sequence
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(new SeqColumnSortingSelector(table_viewer, col));
            }
            else if (properties[i].equalsIgnoreCase(Message.ID))
            {
                view_col.setLabelProvider(new IDProvider());
                // Sort numerically by ID
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(new IDColumnSortingSelector(table_viewer, col));
            }
            // SEVERITY type columns have special color coding
            else if (properties[i].toLowerCase().indexOf(Message.SEVERITY.toLowerCase()) >= 0)
            {
            	view_col.setLabelProvider(
            			new SeverityLabelProvider(properties[i], parent));
            	// Sort alphabetically
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(
                        new PropertyColumnSortingSelector(table_viewer, col, properties[i]));
            }
            else // other columns display & sort property as string
            {
            	view_col.setLabelProvider(new PropertyLabelProvider(properties[i]));
            	final TableColumn col = view_col.getColumn();
                col.addSelectionListener(
                        new PropertyColumnSortingSelector(table_viewer, col, properties[i]));
            }
        }

        table_viewer.setInput(model);
        
        parent.addDisposeListener(this);
    }

    /**
     * Connect listeners to GUI elements.
     */
    private void connectGUIActions()
    {
        times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final StartEndDialog dlg =
                    new StartEndDialog(times.getShell(),
                            start.getText(), end.getText());
                if (dlg.open() != Window.OK)
                    return;
                updateTimeRange(dlg.getStartSpecification(),
                            dlg.getEndSpecification());
            }
        });

        
        final SelectionListener start_end_handler = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                updateTimeRange(start.getText(), end.getText());
            }
        };
        
        start.addSelectionListener(start_end_handler);
        end.addSelectionListener(start_end_handler);

        filter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                updateFilters();
            }
        });

        // Double-click on message opens detail
        table_viewer.getTable().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
            	new OpenViewAction(IPageLayout.ID_PROP_SHEET).run();
            }
        });
        
        refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
            	refresh.getParent().getShell().setFocus();
                updateTimeRange(start.getText(), end.getText());
            }
        });
        
        initializeAutoRefresh(autoRefreshCurrentPeriod, true);
        autoRefresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
				if (!autoRefresh.getSelection()) {
					startAutoRefresh();	
				} else {
					stopAutoRefresh();
				}
			}
        });
    }

    /**
     * Add context menu to table.
     *
     * @param site the site
     */
    private void connectContextMenu(final IWorkbenchPartSite site)
    {
        final Table table = table_viewer.getTable();
        final MenuManager manager = new MenuManager();
        manager.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET, "Show Detail"));
        if(SingleSourcePlugin.getUIHelper().getUI().equals(UI.RCP)) {
        	manager.add(new ExportAction(table.getShell(), model));
        }
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        table.setMenu(manager.createContextMenu(table));

        // Allow extensions to add to the context menu
        if (site != null)
        	site.registerContextMenu(manager, table_viewer);
    }

    /**
     * Update GUI when model changed.
     *
     * @param model the model
     * @see ModelListener
     */
    @Override
    public void modelChanged(final Model model)
    {   // Can be called from background thread...
    	if (table_viewer.getTable().isDisposed()) return;
    	final Display display = table_viewer.getTable().getDisplay();
    	display.asyncExec(new Runnable()
        {
			@Override
            public void run()
            {
                if (start.isDisposed())
                    return;
                if (!start.isFocusControl())
                	start.setText(model.getStartSpec());
                if (!end.isFocusControl()) 
                	end.setText(model.getEndSpec());
                
                //refresh table and keep selections
                int[] tableSelectionIndices = table_viewer.getTable().getSelectionIndices();
                Message[] messages = new Message[tableSelectionIndices.length];
                
                for (int i = 0; i < tableSelectionIndices.length; i++) {
					int index = tableSelectionIndices[i];
					messages[i] = (Message) table_viewer.getElementAt(index);
				}
                table_viewer.refresh();
                
                if (messages.length != 0) {
                	List<Message> listMsgSelect = new ArrayList<Message>();
                	Message[] msgModel = model.getMessages();
                	for (int i = 0; i < msgModel.length; i++) {
                		for (int j = 0; j < messages.length; j++) {
                			if (msgModel[i].getId() == messages[j].getId()) 
    							listMsgSelect.add(msgModel[i]);
                		} // FOR messages
					} // FOR msgModel
                	table_viewer.setSelection(new StructuredSelection(listMsgSelect), true);
                }
                restartAutoRefresh(autoRefreshCurrentPeriod, false);
            }
        });
    	
    }
    

    /**
     * Initialize auto refresh.
     *
     * @param delay the delay
     */
    private void initializeAutoRefresh(long delay, boolean isPeriodUpdated) {
    	// check conditions
    	if (!checkAutoRefreshConditions()) return;
    	// start auto-refresh process
    	autoRefreshStatus = AutoRefreshCurrentState.STARTED;
    	timerAutoRefresh = new Timer("Timer auto-refresh");
    	timerAutoRefresh.schedule(new StartAutoRefreshTask(), delay);
    
    	this.autoRefreshCurrentPeriod = delay;
    	if (isPeriodUpdated) {
    		activateAutoRefresh();
    		Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STARTED 
        			+ TimeUnit.MILLISECONDS.toSeconds(this.autoRefreshCurrentPeriod) 
        			+ " " + TimeUnit.SECONDS.toString().toLowerCase());
    	}
    }
    
    /**
     * Start auto refresh.
     */
    private void startAutoRefresh() {
    	initializeAutoRefresh(autoRefreshCurrentPeriod, true);
    }
    
    
    /**
     * Restart auto refresh.
     *
     * @param delay the delay
     * @param isPeriodUpdated the is period updated
     */
    private void restartAutoRefresh(long delay, boolean isPeriodUpdated) {
    	switch (autoRefreshStatus) {
		case ERROR:
			startAutoRefresh();
			break;
		case STARTED:
			timerAutoRefresh.cancel();
	    	timerAutoRefresh = null;
	    	initializeAutoRefresh(delay, isPeriodUpdated);	
			break;
		case STOPPED:
		default:
			break;
		}
    }
    
    /**
     * Stop auto refresh.
     */
    private void stopAutoRefresh() {
    	if (AutoRefreshCurrentState.STOPPED.equals(autoRefreshStatus) || timerAutoRefresh == null) 
    		return;
    	   	
		timerAutoRefresh.cancel();
	    timerAutoRefresh = null;
	    deactivateAutoRefresh(true);
	    autoRefreshStatus = AutoRefreshCurrentState.STOPPED;
	    Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STOPPED);
    }
    
    
	/**
	 * The Class StartAutoRefreshTask.
	 */
	public class StartAutoRefreshTask extends TimerTask {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				model.refresh();
			} catch (Exception ex) {
				MessageDialog.openError(times.getShell(), "Error",
						"Error during the refresh of the model :\n" + ex.getMessage());
			}
		}
    }

    
	/**
	 * Activate auto refresh.
	 */
	private synchronized void activateAutoRefresh() {
		if (autoRefresh.isDisposed()) return;
		autoRefresh.setToolTipText(autoRefreshDisableMsg 
				+ TimeUnit.MILLISECONDS.toSeconds(this.autoRefreshCurrentPeriod) 
				+ " " + timeUnit.toString().toLowerCase());	
		autoRefresh.setSelection(false);
	}
	
	
	/**
	 * Deactivate auto refresh.
	 *
	 * @param checkCondition the check condition
	 */
	private synchronized void deactivateAutoRefresh(boolean checkCondition) {
		if (autoRefresh.isDisposed()) return;
		// ---- set selection
		autoRefresh.setSelection(true);
		
		// ---- set tooltip text
		// if conditions are not verified set overview of values tooltip
		if (!checkCondition) {
			StringBuilder s = new StringBuilder();
			s.append(autoRefreshEnableMsg);
			if (Preferences.getAutoRefreshPeriod() == 0) {
				s.append("\n \t").append("period set to 0");
			} 
			if (!this.endTime.equals(this.model.getEndSpec())) {
				s.append("\n \t").append("end time not set to \"now\" ");
			}
			autoRefresh.setToolTipText(s.toString());
			return;
		}
		autoRefresh.setToolTipText(autoRefreshEnableMsg);
	}


	/**
	 * Check auto refresh conditions.
	 * <li> refresh period should be set to 0 </li>
	 * <li> end time should be set to "now" </li>
	 *
	 * @return true, if conditions are verified
	 */
	private boolean checkAutoRefreshConditions() {
		if (Preferences.getAutoRefreshPeriod() != 0 && this.endTime.equals(this.model.getEndSpec()))  
			return true;
		// conditions are not verified: 
		deactivateAutoRefresh(false);
		autoRefreshStatus = AutoRefreshCurrentState.ERROR;
//		Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_CONDITION_NOT_VERIFIED);
		return false;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetDisposed(DisposeEvent disposeEvent) {
		stopAutoRefresh();
		archiveRDBStore.removePropertyChangeListener(iPropertyChangeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onErrorModel(String errorMsg) {
		if (table_viewer.getTable().isDisposed()) return;
		// if exception stop scheduler auto refresh and reset button
		final Display display = table_viewer.getTable().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				stopAutoRefresh();
			}
		});
		Activator.getLogger().log(Level.WARNING, errorMsg);
	}
	
	
	/**
	 * The Enum AutoRefreshCurrentState.
	 */
	public enum AutoRefreshCurrentState {
		STARTED, 
		STOPPED, 
		ERROR // auto-refresh stopped and can not start because one of conditions are not validated
	}
	
}
