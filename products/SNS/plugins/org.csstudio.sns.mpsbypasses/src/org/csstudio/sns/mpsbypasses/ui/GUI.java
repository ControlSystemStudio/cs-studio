package org.csstudio.sns.mpsbypasses.ui;

import java.util.Date;

import org.csstudio.apputil.ui.swt.TableColumnSortHelper;
import org.csstudio.sns.mpsbypasses.Preferences;
import org.csstudio.sns.mpsbypasses.model.Bypass;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.csstudio.sns.mpsbypasses.model.BypassModelListener;
import org.csstudio.sns.mpsbypasses.model.BypassState;
import org.csstudio.sns.mpsbypasses.model.Request;
import org.csstudio.sns.mpsbypasses.model.RequestState;
import org.csstudio.sns.mpsbypasses.modes.BeamMode;
import org.csstudio.sns.mpsbypasses.modes.BeamModeListener;
import org.csstudio.sns.mpsbypasses.modes.BeamModeMonitor;
import org.csstudio.sns.mpsbypasses.modes.MachineMode;
import org.csstudio.sns.mpsbypasses.modes.MachineModeListener;
import org.csstudio.sns.mpsbypasses.modes.MachineModeMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for {@link BypassModel}
 *  @author Delphy Armstrong - Original MPSBypassGUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUI implements BypassModelListener, MachineModeListener, BeamModeListener
{
	final private BypassModel model;
	private BypassColors colors;
	private TableViewer bypass_table;
	private Text txt_total;
	private Text txt_bypassed;
	private Text txt_bypassable;
	private Text txt_not_bypassable;
	private Text txt_disconnected;
	private Text txt_error;
	private Combo cmb_modes;
	private Combo cmb_states;
	private Combo cmb_requested;
	private Text txt_rtdl_beam;
	private Text txt_mps_beam;
	private Text txt_rtdl_machine;
	private Text txt_mps_machine;

	private MachineModeMonitor machine_mode_monitor;
	private BeamModeMonitor beam_mode_monitor;

	/** Throttle to reduce number of full table updates */
	private GUIUpdateThrottle full_table_update = new GUIUpdateThrottle(200, 2000)
	{
		@Override
		protected void fire()
		{
			final Table table = bypass_table.getTable();
			if (table.isDisposed())
				return;
			table.getDisplay().asyncExec(new Runnable()
			{
				@Override
	            public void run()
	            {
					if (table.isDisposed())
						return;
					bypass_table.setInput(model.getBypasses());
					displayCount(txt_total, model.getTotal());
					displayCounts();
	            }
			});
		}
	};


	/** Initialize
	 *  @param parent
	 *  @param model
	 *  @param site Site to register context menu or <code>null</code>
	 *  @throws Exception on error
	 */
	public GUI(final Composite parent, final BypassModel model, final IWorkbenchPartSite site) throws Exception
    {
		this.model = model;
		colors = new BypassColors(parent);
		createComponents(parent);

		bypass_table.setInput(model.getBypasses());
		model.addListener(this);

		machine_mode_monitor = new MachineModeMonitor(this);
		machine_mode_monitor.start();

		beam_mode_monitor = new BeamModeMonitor(this);
		beam_mode_monitor.start();

		full_table_update.start();

		parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				full_table_update.dispose();
				machine_mode_monitor.stop();
				beam_mode_monitor.stop();
			}
		});

		hookActions();
		createContextMenu(site);
    }

	/** Connect actions to user input */
	private void hookActions()
    {
	    cmb_modes.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectMachineMode();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

	    final SelectionListener filter_handler = new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final BypassState state = BypassState.fromString(cmb_states.getText());
				final RequestState request = RequestState.fromString(cmb_requested.getText());
				model.setFilter(state, request);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		};
		cmb_states.addSelectionListener(filter_handler);
		cmb_requested.addSelectionListener(filter_handler);
    }

	/** Add context menu to table
     *  @param site Site to register context menu or <code>null</code>
	 */
	private void createContextMenu(final IWorkbenchPartSite site)
    {
		final Table table = bypass_table.getTable();

		final MenuManager manager = new MenuManager();
		manager.add(new ELogAction(table.getShell(), model));
		manager.add(new WebPageAction("Enter Bypass Request",
				Preferences.getEnterBypassURL()));
		manager.add(new WebPageAction("Bypass Display",
				Preferences.getViewBypassURL()));
		manager.add(new Separator("additions"));

		final Menu menu = manager.createContextMenu(table);
		table.setMenu(menu);
		if (site != null)
		    site.registerContextMenu(manager, bypass_table);
    }

	/** Start a {@link Job} that loads bypass info from the RDB for the selected mode */
	public void selectMachineMode()
	{
		final MachineMode mode = MachineMode.fromString(cmb_modes.getText());
		final Job job = new Job("Bypasses...")
		{
			@Override
            protected IStatus run(final IProgressMonitor monitor)
            {
				monitor.beginTask("Loading MPS Bypass Info", IProgressMonitor.UNKNOWN);
				try
				{
					model.selectMachineMode(monitor, mode);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				monitor.done();
	            return Status.OK_STATUS;
            }
		};
		job.setUser(true);
		job.schedule();
	}

	/** Create GUI components
	 *  @param parent
	 */
	private void createComponents(final Composite parent)
    {
		parent.setLayout(new GridLayout());

		createSelectionPanel(parent);
		Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(new GridData(SWT.FILL, 0, true, false));

		createCounterPanel(parent);

		final Composite box = new Composite(parent, 0);
		box.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		box.setLayout(new RowLayout());
		createMachineBeamPanel(box);
		createLegend(box);

		bypass_table = createBypassTable(parent);
    }

	/** Set initial focus */
	public void setFocus()
    {
		cmb_modes.setFocus();
    }

	/** Create panel with selector for machine mode etc.
	 *  @param parent
	 */
	private void createSelectionPanel(final Composite parent)
    {
		final Composite box = new Composite(parent, 0);
		box.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		box.setLayout(new GridLayout(7, false));

		Label l = new Label(box, 0);
		l.setText("Machine Mode:");
		l.setLayoutData(new GridData());
		cmb_modes = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmb_modes.setLayoutData(new GridData());
		cmb_modes.setItems(MachineMode.getNames());
		cmb_modes.select(0);

		l = new Label(box, 0);
		l.setText("State:");
		l.setLayoutData(new GridData());
		cmb_states = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmb_states.setLayoutData(new GridData());
		cmb_states.setItems(BypassState.getNames());
		cmb_states.select(0);

		l = new Label(box, 0);
		l.setText("Requested?:");
		l.setLayoutData(new GridData());

		cmb_requested = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmb_requested.setLayoutData(new GridData());
		cmb_requested.setItems(RequestState.getNames());
		cmb_requested.select(0);

		final Button reload = new Button(box, SWT.PUSH);
		reload.setText("Reload");
		reload.setToolTipText("Re-load bypass information from Relational Database");
		reload.setLayoutData(new GridData());
		reload.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectMachineMode();
			}
		});
    }

	/** Create panel with counters
	 *  @param parent
	 */
	private void createCounterPanel(final Composite parent)
    {
		final Group box = new Group(parent, 0);
		box.setText("Counts");
		box.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		box.setLayout(new RowLayout());

		txt_total = createLabeledText(box, "Total:");
		txt_bypassed = createLabeledText(box, "Bypassed:");
		txt_bypassable = createLabeledText(box, "Bypassable:");
		txt_not_bypassable = createLabeledText(box, "Not Bypassable:");
		txt_disconnected = createLabeledText(box, "Disconnected:");
		txt_error = createLabeledText(box, "Error:");
    }

	/** Helper to create Label and Text
	 *  @param parent
	 *  @param label
	 *  @return
	 */
	private static Text createLabeledText(final Composite parent, final String label)
    {
		final Composite box = new Composite(parent, 0);
		final RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.spacing = 0;
		box.setLayout(layout);
		final Label l = new Label(box, 0);
		l.setText(label);
		final Text text = new Text(box, SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
		text.setLayoutData(new RowData(60, SWT.DEFAULT));
		return text;
    }

	/** Create panel with beam mode and machine mode readbacks
     *  @param parent
     */
    private void createMachineBeamPanel(final Composite parent)
    {
    	final Group box = new Group(parent, 0);
    	box.setText("Operating State");
    	box.setLayout(new GridLayout(3, false));

    	//               RTDL   Switch
    	// Beam Mode     ...
    	// Machine Mode  ...
    	Label l = new Label(box, 0);
    	l.setLayoutData(new GridData());

    	l = new Label(box, 0);
    	l.setText("RTDL");
    	l.setLayoutData(new GridData());

    	l = new Label(box, 0);
    	l.setText("Switch");
    	l.setLayoutData(new GridData());


    	l = new Label(box, 0);
    	l.setText("Beam Mode");
    	l.setLayoutData(new GridData());

    	txt_rtdl_beam = new Text(box, SWT.BORDER | SWT.READ_ONLY);
    	txt_rtdl_beam.setText("              ");
    	txt_rtdl_beam.setLayoutData(new GridData(SWT.FILL, 0, true, false));

    	txt_mps_beam = new Text(box, SWT.BORDER | SWT.READ_ONLY);
    	txt_mps_beam.setText("              ");
    	txt_mps_beam.setLayoutData(new GridData(SWT.FILL, 0, true, false));


    	l = new Label(box, 0);
    	l.setText("Machine Mode");
    	l.setLayoutData(new GridData());

    	txt_rtdl_machine = new Text(box, SWT.BORDER | SWT.READ_ONLY);
    	txt_rtdl_machine.setText("              ");
    	txt_rtdl_machine.setLayoutData(new GridData(SWT.FILL, 0, true, false));

    	txt_mps_machine = new Text(box, SWT.BORDER | SWT.READ_ONLY);
    	txt_mps_machine.setText("              ");
    	txt_mps_machine.setLayoutData(new GridData(SWT.FILL, 0, true, false));
    }

	/** Create panel with color legend
     *  @param parent
     */
    private void createLegend(final Composite parent)
    {
    	BypassState[] states = BypassState.values();

    	final Group box = new Group(parent, SWT.BORDER);
    	box.setText("Legend");
    	box.setLayout(new GridLayout(states.length - 1, false));

    	// Bypassed      Not Bypassed ...
    	for (BypassState state : states)
    	{
    		if (state == BypassState.All)
    			continue;

    		final Label l = new Label(box, 0);
    		l.setText(state.toString());
    		l.setLayoutData(new GridData(SWT.LEFT, 0, true, false));
    	}

    	// Requested
    	for (BypassState state : states)
    	{
    		if (state == BypassState.All)
    			continue;

    		final Label l = new Label(box, 0);
    		l.setText(RequestState.Requested.toString());
    		l.setLayoutData(new GridData(SWT.LEFT, 0, true, false));
    		l.setBackground(colors.getBypassColor(state, true));
    	}

    	// Not Requested
    	for (BypassState state : states)
    	{
    		if (state == BypassState.All)
    			continue;

    		final Label l = new Label(box, 0);
    		l.setText(RequestState.NotRequested.toString());
    		l.setLayoutData(new GridData(SWT.LEFT, 0, true, false));
    		l.setBackground(colors.getBypassColor(state, false));
    	}
    }

	/** Create the table of bypasses with sortable columns
	 *  @param parent
	 *  @return {@link TableViewer} for bypass table
	 */
	private TableViewer createBypassTable(final Composite parent)
    {
		// TableColumnLayout needs table to be only child of a parent widget
		final Composite box = new Composite(parent, 0);
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final TableColumnLayout table_layout = new TableColumnLayout();
		box.setLayout(table_layout);

		final TableViewer table_viewer = new TableViewer(box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Table table = table_viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn view_col;
		// Row number
		view_col = createColumn(table_viewer, table_layout, "#", 10);
		view_col.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final TableItem item = (TableItem)cell.getItem();
				final int row = item.getParent().indexOf(item) + 1;
				cell.setText(Integer.toString(row));
			}
		});

		// Bypass Name, sorted by name
		view_col = createColumn(table_viewer, table_layout, "Bypass", 100);
		view_col.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Bypass bypass = (Bypass) cell.getElement();
				cell.setText(bypass.getFullName());
			}

            @Override
            public String getToolTipText(final Object element)
            {
                final Bypass bypass = (Bypass) element;
                final String tt = bypass.getRDBSignalName();
                if (tt == null)
                    return "";
                return "RDB signal '" + tt + "'";
            }
		});
        new TableColumnSortHelper<Bypass>(table_viewer, view_col)
        {
			@Override
            public int compare(final Bypass item1, final Bypass item2)
			{
				return item2.getName().compareTo(item1.getName());
            }
        };

        // Bypass state, sorted by state with fallback to name
		view_col = createColumn(table_viewer, table_layout, "State", 70);
		view_col.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Bypass bypass = (Bypass) cell.getElement();
				cell.setText(bypass.getState().toString());
				final boolean requested = bypass.getRequest() != null;
				cell.setBackground(colors.getBypassColor(bypass.getState(), requested));
			}
		});
        new TableColumnSortHelper<Bypass>(table_viewer, view_col)
        {
			@Override
            public int compare(final Bypass item1, final Bypass item2)
            {
				final int cmp = item2.getState().toString().compareTo(item1.getState().toString());
				if (cmp != 0)
					return cmp;
				return item2.getName().compareTo(item1.getName());
            }
        };

        // Bypass Requestor, sorted by requestor name with fallback to bypass name
		view_col = createColumn(table_viewer, table_layout, "Requestor", 100);
		view_col.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Bypass bypass = (Bypass) cell.getElement();
				final Request request = bypass.getRequest();
				if (request != null)
					cell.setText(request.getRequestor());
				else
					cell.setText("");
			}
		});
        new TableColumnSortHelper<Bypass>(table_viewer, view_col)
        {
			@Override
            public int compare(final Bypass item1, final Bypass item2)
            {
				Request request = item1.getRequest();
				final String requestor1 = request == null ? "" : request.getRequestor();
				request = item2.getRequest();
				final String requestor2 = request == null ? "" : request.getRequestor();
				final int cmp = requestor2.compareTo(requestor1);
				if (cmp != 0)
					return cmp;
				return item2.getName().compareTo(item1.getName());
            }
        };

        // Bypass Request date, sorted by date with fallback to bypass name
		view_col = createColumn(table_viewer, table_layout, "Request Date", 100);
		view_col.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Bypass bypass = (Bypass) cell.getElement();
				final Request request = bypass.getRequest();
				if (request != null)
					cell.setText(request.getDate().toString());
				else
					cell.setText("");
			}
		});
        new TableColumnSortHelper<Bypass>(table_viewer, view_col)
        {
			@Override
            public int compare(final Bypass item1, final Bypass item2)
            {
				Request request = item1.getRequest();
				final Date date1 = request == null ? new Date(0) : request.getDate();
				request = item2.getRequest();
				final Date date2 = request == null ? new Date(0) : request.getDate();
				final int cmp = date2.compareTo(date1);
				if (cmp != 0)
					return cmp;
				return item2.getName().compareTo(item1.getName());
            }
        };

		table_viewer.setContentProvider(ArrayContentProvider.getInstance());

        ColumnViewerToolTipSupport.enableFor(table_viewer);

		return table_viewer;
    }

	/** Helper for creating table column
	 *  @param table_viewer
	 *  @param table_layout
	 *  @param title
	 *  @param weight Column size weight
	 *  @return
	 */
	private TableViewerColumn createColumn(final TableViewer table_viewer, TableColumnLayout table_layout,
			final String title, final int weight)
	{
		final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
		final TableColumn column = view_col.getColumn();
		column.setText(title);
		column.setMoveable(true);
		column.setResizable(true);
		table_layout.setColumnData(column, new ColumnWeightData(weight));
		return view_col;
	}

	/** {@inheritDoc} */
	@Override
    public void modelLoaded(final BypassModel model, final Exception error)
    {
		final Table table = bypass_table.getTable();
		if (table.isDisposed())
			return;
		table.getDisplay().asyncExec(new Runnable()
		{
			@Override
            public void run()
            {
				if (table.isDisposed())
					return;
				bypass_table.setInput(model.getBypasses());
				// Was there an error?
				if (error != null)
				{
					MessageDialog.openError(table.getShell(), "Database Error",
						NLS.bind("Error loading bypass information from database:\n{0}",
								error.getMessage()));
					return;
				}
				displayCount(txt_total, model.getTotal());
				displayCounts();
				// Start model updates
				try
				{
					model.start();
				}
				catch (Exception ex)
				{
					MessageDialog.openError(table.getShell(), "Database Error",
							NLS.bind("Error starting bypass updates from EPICS:\n{0}",
									ex.getMessage()));
				}
            }
		});
    }

	/** Update display for one bypass and counts
	 * {@inheritDoc}
	 */
	@Override
    public void bypassChanged(final Bypass bypass)
    {
		final Table table = bypass_table.getTable();
		if (table.isDisposed())
			return;
		table.getDisplay().asyncExec(new Runnable()
		{
			@Override
            public void run()
            {
				if (table.isDisposed())
					return;
				bypass_table.refresh(bypass);
				displayCounts();
            }
		});
    }

	/** Refresh table
	 *  {@inheritDoc}
	 */
	@Override
    public void bypassesChanged()
    {
		full_table_update.trigger();
    }

	/** Display the model's counts
	 *  (except for 'total' which doesn't change)
	 */
	protected void displayCounts()
    {
		displayCount(txt_bypassed, model.getBypassed());
		displayCount(txt_bypassable, model.getBypassable());
		displayCount(txt_not_bypassable, model.getNotBypassable());
		displayCount(txt_disconnected, model.getDisconnected());
		displayCount(txt_error, model.getInError());
    }

	/** @param txt {@link Text} field
	 *  @param count Number to display in field
	 */
	private static void displayCount(final Text txt, int count)
    {
		txt.setText(Integer.toString(count));
    }

	/** Update machine mode info
	 *  {@inheritDoc}
	 */
	@Override
    public void machineModeUpdate(final MachineMode new_rtdl_mode,
            final MachineMode new_switch_mode)
    {
		if (txt_mps_machine.isDisposed())
			return;
		txt_mps_machine.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				setPossibleNullText(txt_rtdl_machine, new_rtdl_mode);
				setPossibleNullText(txt_mps_machine, new_switch_mode);
			}
		});
    }

	/** Update beam mode info
	 *  {@inheritDoc}
	 */
	@Override
    public void beamModeUpdate(final BeamMode new_rtdl_mode, final BeamMode new_switch_mode)
    {
		if (txt_mps_beam.isDisposed())
			return;
		txt_mps_beam.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				setPossibleNullText(txt_rtdl_beam, new_rtdl_mode);
				setPossibleNullText(txt_mps_beam, new_switch_mode);
			}
		});
    }

	/** @param text Text widget that should display a string
	 *  @param obj Object with string or <code>null</code>
	 */
	protected void setPossibleNullText(final Text text, Object obj)
    {
		if (text.isDisposed())
			return;
		if (obj == null)
			text.setText("  ?  ");
		else
			text.setText(obj.toString());
    }
}
