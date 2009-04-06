package org.csstudio.diag.pvutil.gui;

import org.csstudio.platform.ui.swt.AutoSizeColumn;
import org.csstudio.platform.ui.swt.AutoSizeControlListener;
import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.csstudio.diag.pvutil.model.PVUtilListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("nls")
public class GUI implements PVUtilListener
        

{
    private Display display = Display.getDefault();
    private PVUtilModel model;
    /** Sash for the two GUI sub-sections divided by the moving bar */
    private SashForm form;
    private int form_weights[] = new int[] { 50, 50 };
    
    /** GUI Elements */
    private Text fecEntry, recPVFilterEntry;
    private Button clearDeviceButton, clearPVButton, clearAllButton;
    public List deviceList;
    public ListViewer deviceListViewer;
    private TableViewer pv_table;
    
    /** Enumerator that dictates what text elements to manipulate when a button is pressed */
    public enum ItemIndex {
        /** The front end controller name */
        FEC,
        /** The recFilter(PV filter) */
        PV,
        /** Changes both to default */
        All
    }

    public GUI(Composite shell, final PVUtilModel model)
    {
    	this.model = model;
        shell.setLayout(new FillLayout());

        // Split into upper and lower sash
        form = new SashForm(shell, SWT.VERTICAL | SWT.BORDER);
        form.setLayout(new FillLayout());
        createUpperSash(form);
        createLowerSash(form);
        form.setWeights(form_weights);
        hookListeners();
    }

    /** Create the upper sash: Various tabs */
    private void createUpperSash(final SashForm form)
    {
        // Composite Containing the CTabFolder
        Composite topOfFormComposite = new Composite(form, SWT.NULL);
        GridLayout layout = new GridLayout();
        topOfFormComposite.setLayout(layout);
        
        //Tab folder start.
        GridData gd = new GridData(GridData.FILL_BOTH);
        
        layout = new GridLayout();
        layout.numColumns = 3;
        topOfFormComposite.setLayout(layout);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        topOfFormComposite.setLayoutData(gd);


        Label l = new Label(topOfFormComposite, 0);
        l.setText("List Filter:");
        gd = new GridData();
        l.setLayoutData(gd);

        fecEntry = new Text(topOfFormComposite, SWT.BORDER);
        fecEntry.setToolTipText("Enter Filter As Appropriate for Data Source (Maybe % or * would help).");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fecEntry.setLayoutData(gd);

        clearDeviceButton = new Button(topOfFormComposite, SWT.PUSH | SWT.CENTER);
        clearDeviceButton.setText("Clear Device");
        clearDeviceButton.setLayoutData(new GridData());

        // List Box of IOCs
        deviceList = new List(topOfFormComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = layout.numColumns;
        int listHeight = deviceList.getItemHeight() * 6;
        Rectangle trim = deviceList.computeTrim(0, 0, 0, listHeight);
        gd.heightHint = trim.height;
        gd.widthHint = 200;
        deviceList.setLayoutData(gd);
 
        // TableViewer interface the plain device_table_widget to our "model":
        deviceListViewer = new ListViewer(deviceList);
 
    }
    

    /** Create the lower sash: PV Name filter, PV list */
    private void createLowerSash(final SashForm form)
    {
        Composite container = new Composite(form, SWT.NULL);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        container.setLayout(layout);

        // Row: PV Table Label, Button, Button
        Label l = new Label(container, 0);
        l.setText("Process Variables:");
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.LEFT;
        l.setLayoutData(gd);

        // Row: Text Entry for PV filter.
        recPVFilterEntry = new Text(container, SWT.BORDER);
        recPVFilterEntry.setToolTipText("Enter full PV name for quick results or search by using '%'");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        recPVFilterEntry.setLayoutData(gd);

        clearPVButton = new Button(container, SWT.PUSH | SWT.CENTER);
        clearPVButton.setText("Clear PV");
        clearPVButton.setLayoutData(new GridData());

        clearAllButton = new Button(container, SWT.PUSH | SWT.CENTER);
        clearAllButton.setText("Reset All");
        gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gd.horizontalSpan = 3;
        clearAllButton.setLayoutData(gd);

        // Rest: PV Table
        Table device_table_widget = new Table(container, SWT.VIRTUAL
                | SWT.MULTI);

        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        device_table_widget.setLayoutData(gd);
        device_table_widget.setHeaderVisible(true);
        device_table_widget.setLinesVisible(true);

        AutoSizeColumn.make(device_table_widget, "Process Variable", 200, 100);
        AutoSizeColumn.make(device_table_widget, "Info", 210, 50);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(device_table_widget);

        // TableViewer interface the plain device_table_widget
        // to our "model":
        pv_table = new TableViewer(device_table_widget);
        // Turns request for table row into "Device"
        pv_table.setContentProvider(new PVProvider(pv_table, model));
        // Turns request for column 0, 1, 2, ... into Device's name, parent, ...
        pv_table.setLabelProvider(new PVLabelProvider());
    }
    
    void hookListeners()
    {
         /**
         * Listens for the return key in the List Filter Text Box.
         * Then passes the entered value to the FEC model
         * so that the FEC list can be filtered by the selection.  
         * 
         * Selecting this eliminates previous selections.
         */
        fecEntry.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    String fecName = fecEntry.getText().trim();
                    model.setFECFilter(fecName);
                }
            }
        });

        /**
         * Listens for a selection of an FEC from the FEC List.
         * The value is then passed to the Control 
         * and is used to query for associated process variables.
         */
         deviceList.addSelectionListener(new SelectionAdapter()
        {
        	@Override
            public void widgetSelected(SelectionEvent event)
            {
        		final String[] dvc = deviceList.getSelection();
        		model.setFECName(dvc[0]);
            }
        });

        /**
         * Listens for the enter key to be pressed.
         * The value is then passed to the Control and 
         * used to query for associated
         * process variables that are like the value entered.
         */
        recPVFilterEntry.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    final String pvFilter = recPVFilterEntry.getText().trim();
                    model.setPVFilter(pvFilter);
                 }
            }
        });

        /**
         * This button clears current device selection
         * and removes any previous selection in the list.
         */
        clearDeviceButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
            	model.setObjectClear(ItemIndex.FEC);
                fecEntry.setText("");
                deviceList.deselectAll();
            }
        });

        /**
         * This button clears current PV filter.
         */
        clearPVButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
            	model.setObjectClear(ItemIndex.PV);
                recPVFilterEntry.setText("");
            }
        });

        /**
         * This button clears all the current selections and text boxes.
         */
        clearAllButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
            	model.setObjectClear(ItemIndex.All);
                recPVFilterEntry.setText("");
                fecEntry.setText("");
                deviceList.deselectAll();
                reInitialize();
            }
        });
        
        
        //Initialize these in the fec_model.
        model.setFECFilter(model.getStartDeviceID());
        fecEntry.setText(model.getStartDeviceID());

        // Subscribe to changes
        model.addListener(this);
    }

    /**
     * Provide PV table viewer so that Eclipse View can use it as a selection
     * provider
     */
    public TableViewer getPVTableViewer()
    {
        return pv_table;
    }
    
    /** Provide access to the PV Filter text to allow drag/drop connections */
    public Text getPVFilterText()
    {
        return recPVFilterEntry;
    }

    /**
     * Provide Device List so that Eclipse View can use it as a selection
     * provider
     */
    public ListViewer getDeviceList()
    {
        return deviceListViewer;
    }

    /** Provide access to the Device Filter text to allow drag/drop connections */
    public Text getDeviceFilterText()
    {
        return fecEntry;
    }
    
    /** Clears the FEC list and then re-populates it based on the new criteria
     *  @see PVUtilListener
     */
    public void pvUtilChanged(final ChangeEvent what)
    {
        // This could be called from a non-GUI thread, for example the model's
        // database reader.
        display.asyncExec(new Runnable()
        {
            public void run()
            {
            	switch (what)
            	{
            	case FEC_CHANGE:
	                final int N = model.getFECCount();
	                deviceList.removeAll();
	                for (int i = 0; i < N; i++)
	                {
	                    final String fec = model.getFEC(i).getName();
	                    deviceList.add(fec);
	                }
	                break;
            	case PV_CHANGE:
	                // Setting the count causes an update
	                pv_table.setItemCount(model.getPVCount());
	                pv_table.refresh();
	                break;
            	}
            }
        });
    }

    /**
     * Resets the Filters used by the control so that the 
     * plug-in acts as it did when started.
     */
    public void reInitialize()
    {
        model.setFECFilter(model.getStartDeviceID());
        fecEntry.setText(model.getStartDeviceID());
        model.setPVFilter("");
    }

    public void setFocus()
    {
        // Set focus FEC Filter
    	fecEntry.setFocus();
    }
}
