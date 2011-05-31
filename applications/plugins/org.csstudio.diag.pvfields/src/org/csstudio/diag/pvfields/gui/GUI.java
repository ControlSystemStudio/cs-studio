/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.gui;

import java.io.FileWriter;
import java.io.IOException;

import org.csstudio.diag.pvfields.model.PVFieldsListener;
import org.csstudio.diag.pvfields.model.PVFieldsModel;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

// TODO Externalize strings
@SuppressWarnings("nls")
public class GUI implements PVFieldsListener
{
    final private PVFieldsModel model;
    final private Composite parent;

    /** GUI Elements */
    private TableViewer fields_table;
    private ComboViewer cbo_name;
    private Text rec_type_value;
    private ComboViewer field_value;
    private Text file_value;
    private Text ioc_value;
    private Text boot_date_value;
    private Table fields_table_widget;
    private Text pvLabel;
    private Text fileName;
    private Button toFileButton;

    // These filter names are displayed to the user in the file dialog. Note that
    // the inclusion of the actual extension in parentheses is optional, and
    // doesn't have any effect on which files are displayed.
    private static final String[] FILTER_NAMES = {
        "Comma Separated Values Files (*.csv)", "All Files (*.*)"};

    // These filter extensions are used to filter which files are displayed.
    private static final String[] FILTER_EXTS = { "*.csv", "*.*"};

    public GUI(final Composite parent, final PVFieldsModel model)
    {
        this.model = model;
        this.parent = parent;

        final GridLayout layout = new GridLayout(5, false);
        parent.setLayout(layout);

        // New row
        Label label = new Label(parent, SWT.READ_ONLY);
        label.setText("PV Name/Filter: ");
        label.setLayoutData(new GridData());

        cbo_name = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
        cbo_name.getCombo().setToolTipText("Enter PV Name To Look Up or Enter Text to be included in multiple Process Variables.  Use '%' as wildcard..");
        cbo_name.getCombo().setLayoutData(new GridData(SWT.FILL, 0, true, false));

        label = new Label(parent, SWT.RIGHT);
        label.setText("Field:");
        label.setLayoutData(new GridData());

        field_value = new ComboViewer(parent, SWT.BORDER);
        field_value.getCombo().setToolTipText(
        		"Enter Field Value or Comma Delimited List of Field Values "+
        		"ie. 'ASG,%N,VAL,HIGH,LOW' or 'VAL, %S%' or leave blank.");
        field_value.getCombo().setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        // New row
        pvLabel = new Text (parent, SWT.READ_ONLY);
        pvLabel.setText("<pv>");
        pvLabel.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        label = new Label(parent, SWT.NONE);
        label.setText("File Name:");
        label.setLayoutData(new GridData());

        fileName = new Text(parent, SWT.BORDER);
        fileName.setLayoutData(new GridData());

        //Button
        toFileButton = new Button(parent, SWT.PUSH);
        toFileButton.setText("Export to File");
        toFileButton.setLayoutData(new GridData());

        // New row
        label = new Label(parent, 0);
        label.setText("Record Type:");
        label.setLayoutData(new GridData());

        rec_type_value = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        rec_type_value.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        label = new Label(parent, SWT.RIGHT);
        label.setText("IOC Name:");
        label.setLayoutData(new GridData());

        ioc_value = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        ioc_value.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        // New row
        label = new Label(parent, 0);
        label.setText("Boot Date:");
        label.setLayoutData(new GridData());

        boot_date_value = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        boot_date_value.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        label = new Label(parent, 0);
        label.setText("Boot File:");
        label.setLayoutData(new GridData());

        file_value = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        file_value.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        // Rest: Fields Table
        // TableColumnLayout requires its own table parent
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        fields_table_widget = new Table(table_parent, SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION);
        fields_table_widget.setHeaderVisible(true);
        fields_table_widget.setLinesVisible(true);

        TableColumn col = new TableColumn(fields_table_widget, SWT.LEFT);
        col.setText("Field");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(100, 50));

        col = new TableColumn(fields_table_widget, SWT.LEFT);
        col.setText("DBD Type");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(100, 50));

        col = new TableColumn(fields_table_widget, SWT.LEFT);
        col.setText("Value in File");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(50, 150));

        col = new TableColumn(fields_table_widget, SWT.LEFT);
        col.setText("Live Value");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(50, 150));

        // TableViewer interface the plain device_table_widget
        // to our "model":
        fields_table = new TableViewer(fields_table_widget);
        // Turns request for table row into "Device"
        fields_table.setContentProvider(new FieldProvider(fields_table, model));
        // Turns request for column 0, 1, 2, ... into Device's name, parent, ...
        fields_table.setLabelProvider(new FieldLabelProvider());

        hookListeners();
    }

    void hookListeners()
    {

    	/**
         * Listens for the enter key to be pressed. The value is then passed to
         * the Control and used to query for associated process variables fields
         * that are like the value entered.
         */

        field_value.getCombo().addSelectionListener(new SelectionListener()
        {
			@Override
            public void widgetDefaultSelected(SelectionEvent e) {
            	String pv = cbo_name.getCombo().getText().trim();
                String field = field_value.getCombo().getText().trim();
                if (field.length() <= 0)
                	field = null;
                model.setPV(pv,field);
			}

			@Override
            public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
        });

        // Subscribe to changes
        model.addListener(this);

        // Stop model when GUI is disposed
        fields_table.getTable().addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
            	model.removeListener(GUI.this);
            	model.disconnectCurrentFields();
            }
        });

        //Update values in upper section with appropriate values
        fields_table_widget.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
            	PVInfo pv = model.getPVInfoRow(fields_table_widget.getSelectionIndex());
            	pvLabel.setText(pv.getPVName());
            	rec_type_value.setText(pv.getType());
                ioc_value.setText(pv.getFEC());
                boot_date_value.setText(pv.getDate());
                file_value.setText(pv.getFileName());
                }
          });

        toFileButton.addSelectionListener(new SelectionAdapter()
        {
        	@Override
        	public void widgetSelected(SelectionEvent event) {

        		try{
        			FileDialog dlg = new FileDialog(parent.getShell(), SWT.SAVE);
        			dlg.setFilterNames(FILTER_NAMES);
        	        dlg.setFilterExtensions(FILTER_EXTS);
    	        	String fileEntry = fileName.getText();
        	        if (fileEntry != "") {
        	        	if (fileEntry == "")System.out.println( "It's ull");
        	        	String path = fileEntry.substring(0, fileEntry.lastIndexOf("\\")+1);
        	        	String fileN = fileEntry.substring(fileEntry.lastIndexOf("\\")+1);
        	        	if (path != null) dlg.setFilterPath(path);
        	        	if (fileN != null) dlg.setFileName(fileN);
        	        }
        	        String fn = dlg.open();
        	        if (fn != null) {
        	          fileName.setText(fn);

        	          FileWriter writer = new FileWriter(fn);
	                  PVInfo[] pvs = model.getPVInfoAll();

	       		      for (int i = 0; i<pvs.length; i++){
		        		writer.append( pvs[i].getPVName() + "\t" );
		        		writer.append( pvs[i].getType() + "\t"  );
		        		writer.append( pvs[i].getFEC() + "\t"  );
		        		writer.append( pvs[i].getDate() + "\t" );
		        		writer.append( pvs[i].getFileName() + "\t" );
		        		writer.append( pvs[i].getFieldName() + "\t" );
		        		writer.append( pvs[i].getFieldType() + "\t" );
		        		writer.append( pvs[i].getOrigValue() + "\t" );
		        		writer.append( pvs[i].getCurrentValue() + "\t" );
		        		//writer.append( fields_table.getElementAt(i).toString());
		        		writer.append('\n');
	      		      }
	        			writer.flush();
	        			writer.close();
        	        }
        		}
        		catch (IOException e) {
        			 e.printStackTrace();
        		}
        	}
        });
    }

    /**
     * Provide Fields ComboViewer so that Eclipse View can use it as a selection
     * provider
     */
    public ComboViewer getPVViewer()
    {
        return cbo_name;
    }

    /**
     * Provide Fields ComboViewer so that Eclipse View can use it as a selection
     * provider
     */
    public ComboViewer getFieldViewer()
    {
        return field_value;
    }

    /**
     * Provide Fields table viewer so that Eclipse View can use it as a
     * selection provider
     */
    public TableViewer getFieldsTable()
    {
        return fields_table;
    }

    /**
     * Provide Fields combo name so that Eclipse View can use it as a selection
     * provider
     */
    public String getPVName()
    {
        return cbo_name.getCombo().getText();
    }

    /**
     * Provide Fields combo name so that Eclipse View can use it as a selection
     * provider
     */
    public String getFieldValue()
    {
        return field_value.getCombo().getText();
    }

    /**
     * Set PV within combo viewer
     */
    public void setPVName(final String pv_name, final String field_name)
    {
    	model.setPV(pv_name,field_name);
    	setTableColHead();
        cbo_name.getCombo().setText(pv_name);
        if (field_name==null) field_value.getCombo().setText("");
    	else field_value.getCombo().setText(field_name);
    }



    // @see PVFieldsListener
    @Override
    public void fieldChanged(final PVInfo field)
    {
        // This could be called from a non-GUI thread, for example the model's
        // database reader or from the PV subscription thread
        Display.getDefault().asyncExec(new Runnable()
        {
        	@Override
            public void run()
            {
                final PVInfo pv = model.getPVInfoRow(0);
        		if (fields_table.getControl().isDisposed())
              	{
            		return;
            	}
            	if (field == null)
                {
                    // Update whole model


    	            pvLabel.setText(pv.getPVName());
                	rec_type_value.setText(pv.getType());
                    ioc_value.setText(pv.getFEC());
                    boot_date_value.setText(pv.getDate());
                    file_value.setText(pv.getFileName());
                    setTableColHead();
                    fields_table.setItemCount(model.getPVInfoListCount());
                    fields_table.refresh();
                }
                else
                {
                    // Update only affected row
                	setTableColHead();
                    fields_table.refresh(field);
                }
            }
        });
    }


    /**
     * Clear the last text from the Fields table.
     */
    public void clearLastPV()
    {
    	pvLabel.setText("");
        rec_type_value.setText("");
        ioc_value.setText("");
        boot_date_value.setText("");
        file_value.setText("");
        fields_table.setItemCount(0);
        fields_table.refresh();
    }

    /**
     * Set Column Headers for first two columns of Table
     */
    public void setTableColHead() {
    	if (model.alterColumnData()){
        	fields_table_widget.getColumn(0).setText("Field");
        	fields_table_widget.getColumn(1).setText("DBD Type");
        	}
        	else{
            	fields_table_widget.getColumn(0).setText("Parameter");
            	fields_table_widget.getColumn(1).setText("Field");
            	}
    }
}
