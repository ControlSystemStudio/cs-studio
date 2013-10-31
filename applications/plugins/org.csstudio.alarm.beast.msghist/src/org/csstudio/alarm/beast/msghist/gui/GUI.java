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
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
 * @author benhadj naceur
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
    
    /** The scheduled executor service. */
    private ScheduledExecutorService scheduledExecutorService = null;

    /** The time unit. */
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    
    /** The image refresh button */
    private Image imageAutoRefreshRun, imageManualRefresh = null;
    
    /** The auto refresh enable msg. */
    private String autoRefreshEnableMsg = "Enable auto refresh";
    
    private int autoRefreshPeriod;
    
    /** The auto refresh disable msg. */
    private String autoRefreshDisableMsg = "Automatic refresh every ";
    
    /** The end time. */
    private String endTime = "now";
    
    /** The log info msg auto refresh started. */
    private String LOG_INFO_MSG_AUTO_REFRESH_STARTED = "Auto refresh is running every " ;
    
    /** The log info msg auto refresh stopped. */
    private String LOG_INFO_MSG_AUTO_REFRESH_STOPPED = "Auto refresh is stopped ";
    
    /** The log info msg auto refresh condition not verified. */
    private String LOG_INFO_MSG_AUTO_REFRESH_CONDITION_NOT_VERIFIED = "Cannot start auto refresh one of conditions is not verified ";
    
    
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
        this.autoRefreshPeriod = Preferences.getAutoRefreshPeriod();
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
        
        //listener on preferences
    	final IPreferenceStore archiveRDBStore = new ScopedPreferenceStore(
    			InstanceScope.INSTANCE, org.csstudio.alarm.beast.msghist.Activator.ID);
    	archiveRDBStore.addPropertyChangeListener(new IPropertyChangeListener() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
			 */
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				if (Preferences.AUTO_REFRESH_PERIOD.equals(propertyChangeEvent.getProperty())) {
					autoRefreshPeriod = Preferences.getAutoRefreshPeriod();
					if (autoRefreshPeriod == 0) {
						resetAutoRefresh();
					} else if (autoRefreshPeriod > 0) {
						activateAutoRefresh();
					}
				}
			}
		});

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
        
        refresh = new Button(parent, SWT.PUSH);
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
                updateTimeRange(start.getText(), end.getText());
            }
        });
        
        initializeAutoRefresh();
        autoRefresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
				// No automatic refresh When the period is set to 0
				// updates only if the "End" time is set to now
            	autoRefreshPeriod = Preferences.getAutoRefreshPeriod();
				if (autoRefreshPeriod == 0
						|| !endTime.equals(model.getEndSpec())) {
					disableAutoRefresh();
					Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_CONDITION_NOT_VERIFIED);
					return;
				}
				// normal case
				if (!autoRefresh.getSelection()) {
					if (scheduledExecutorService == null) {
						scheduledExecutorService = Executors
								.newScheduledThreadPool(1);
						scheduledExecutorService.scheduleAtFixedRate(
								new StartAutoRefreshTask(), 0,
								autoRefreshPeriod, timeUnit);
						Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STARTED + Preferences.getAutoRefreshPeriod() + " seconds");
					}
					autoRefresh.setToolTipText(autoRefreshDisableMsg + Preferences.getAutoRefreshPeriod() + " " + timeUnit.toString());
				} else if (scheduledExecutorService != null) {
					scheduledExecutorService.shutdown();
					scheduledExecutorService = null;
				    autoRefresh.setToolTipText(autoRefreshEnableMsg);
				    Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STOPPED);
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
            }
        });
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
				
				// Check auto refresh cases: period > 0 second and end time is set to  now
				if (Preferences.getAutoRefreshPeriod() == 0
						|| !endTime.equals(model.getEndSpec())) {
					final Display display = table_viewer.getTable()
							.getDisplay();
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							resetAutoRefresh();
						}
					});
				}
			} catch (Exception ex) {
				MessageDialog.openError(times.getShell(), "Error",
						"Error during the refresh of the model :\n" + ex.getMessage());
			}
		}
    }

	
	/**
	 * Reset auto refresh.
	 */
	public void resetAutoRefresh() {
		if (scheduledExecutorService !=null && !scheduledExecutorService.isShutdown()) {
  			scheduledExecutorService.shutdownNow();
          	scheduledExecutorService = null;
        
			disableAutoRefresh();
        	Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STOPPED);
  	   } 
	}
	
	/**
	 * Initialize auto refresh.
	 * <p>Start auto refresh only if conditions are verified: <br />
	 * <li>period is not set to 0</li>
	 * <li>end time is set to "now"</li>
	 */
	private void initializeAutoRefresh() {
		 // No automatic refresh When the period is set to 0
		 // AND updates only if the "End" time is set to now
		autoRefreshPeriod = Preferences.getAutoRefreshPeriod();
		if (autoRefreshPeriod == 0 || !this.endTime.equals(this.model.getEndSpec())) {
			Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_CONDITION_NOT_VERIFIED);
			return;
		}
		
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(
				new StartAutoRefreshTask(), 0,
				autoRefreshPeriod, timeUnit);
		enableAutoRefresh();
		Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STARTED + Preferences.getAutoRefreshPeriod() + " seconds");
	}
	
	
	/**
	 * Activate auto refresh.
	 */
	private void activateAutoRefresh() {
		if (scheduledExecutorService != null) return;
		Activator.getLogger().log(Level.INFO, "Auto refresh is running every " + autoRefreshPeriod + " seconds");
		enableAutoRefresh();
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(
				new StartAutoRefreshTask(), 0,
				autoRefreshPeriod, timeUnit);
	
	}
	
    
	/**
	 * Enable auto refresh.
	 */
	private void enableAutoRefresh() {
			autoRefresh.setToolTipText(autoRefreshDisableMsg + Preferences.getAutoRefreshPeriod() + " " + timeUnit.toString());
			autoRefresh.setSelection(false);
	}
	
	
	/**
	 * Disable auto refresh.
	 */
	private void disableAutoRefresh() {
		autoRefresh.setToolTipText(autoRefreshEnableMsg);
		autoRefresh.setSelection(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetDisposed(DisposeEvent disposeEvent) {
		// stop scheduler auto refresh
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
		}
		Activator.getLogger().log(Level.INFO, LOG_INFO_MSG_AUTO_REFRESH_STOPPED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onErrorModel(String errorMsg) {
		// if exception stop scheduler auto refresh and reset button
		final Display display = table_viewer.getTable().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				resetAutoRefresh();
			}
		});
		Activator.getLogger().log(Level.WARNING, errorMsg);
	}
	
}
