/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.sb.ui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.util.CalibrationSource;
import org.csstudio.askap.sb.util.EphemerisDataModel;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author wu049
 * @created Sep 6, 2010
 * 
 */
public class SourceSelectionDialog extends Dialog {
	
	private static final Logger logger = Logger.getLogger(SourceSelectionDialog.class.getName());
	
	EphemerisDataModel dataModel = new EphemerisDataModel();
	
	Shell shell = null;
	CalibrationSource source = null;

	private Text nameField;
	private Combo catalogueCombo;
	private Table sourceTable;
	
	private Label messageCountLabel;

	public static void main(String[] args) {
		
		final Display display = new Display();
		Shell shell = new Shell(display);

		SourceSelectionDialog dialog = new SourceSelectionDialog(shell);
		
		CalibrationSource source = dialog.open();
		
		if (source==null) {
			System.out.println("null");
			return;
		}
		
		System.out.println(source.toString());
	}
	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public SourceSelectionDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL | SWT.RESIZE );
	}
	
	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public CalibrationSource open() {
		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText("Calibration Source Selection");
		createContents(shell);
		shell.setSize(1000, 500);
		shell.open();
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		// Return the entered value, or null
		return source;
	}
	
	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell page) {
		
		GridLayout gridLayout = new GridLayout(2, false);
		page.setLayout(gridLayout);			
		
		
		(new Label(page, SWT.NONE)).setText("Name");				
		nameField = new Text(page, 0);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		nameField.setLayoutData(gridData);
		
		(new Label(page, SWT.NONE)).setText("Catalogue");
		catalogueCombo = new Combo(page, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		catalogueCombo.setLayoutData(gridData);
		String[] categories = getCategories(page);
		catalogueCombo.add("All catalogues");
		
		if (categories != null) {
			for (String s : categories)
				catalogueCombo.add(s);
		}
		
		catalogueCombo.select(0);
			
		Button searchButton = new Button(page, SWT.PUSH);
		searchButton.setText("Search");
		
		searchButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent event) {
				searchSource();
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		messageCountLabel = new Label(page, 0);
		messageCountLabel.setText("0 messages");
		messageCountLabel.setAlignment(SWT.RIGHT);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		messageCountLabel.setLayoutData(gridData);
		
		createSourceTable(page);
		
		page.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(sourceTable);
			}
		});

		Button okButton = new Button(page, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent event) {
				TableItem rows[] = sourceTable.getSelection();
				if (rows==null || rows.length==0) {
					MessageDialog.openInformation(page, "Info", "Please select a Calibration Source!");
					return;
				}
				
				source = (CalibrationSource) rows[0].getData();
				page.close();	
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		Button cancelButton = new Button(page, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent event) {
				source = null;
				page.close();	
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
				
		page.pack();
	}
	
	private String[] getCategories(Shell shell) {
		String categories[] = null;
		try {
			categories = dataModel.getCategories();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not get categories", e);
            ExceptionDetailsErrorDialog.openError(shell,
                    "ERROR",
                    "Could not get categories",
                    e);
		}
		
		return categories;
	}

	/**
	 * 
	 */
	protected void searchSource() {
		String name = nameField.getText();
		String catalogue = catalogueCombo.getText();
		
		name = name==null ? "":name;
		
		if (catalogueCombo.getSelectionIndex()==0)
			catalogue = "";
		
		List<CalibrationSource> sources;
		try {
			sources = dataModel.searchSource(name, catalogue);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error while searching for sources: name=\"" 
					+ name + "\" category=\"" + catalogue + "\"", e);
			
            ExceptionDetailsErrorDialog.openError(shell,
                    "ERROR",
                    "Error while searching for sources: name=\"" 
        					+ name + "\" category=\"" + catalogue + "\"",
                    e);

			return;
		}
		populateTable(sources);
	}
	/**
	 * @param page
	 */
	private void createSourceTable(Shell page) {
		sourceTable = new Table(page, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		sourceTable.setLinesVisible (true);
		sourceTable.setHeaderVisible (true);
		
		TableColumn column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("Name");
		
		column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("Frame");

		column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("RA");
		
		column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("Dec");
		
		column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("Catalogue");
		
		column = new TableColumn (sourceTable, SWT.NONE);
		column.setText ("Flux");
		
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;	
		gd.verticalAlignment = GridData.FILL;	
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = 2;
		sourceTable.setLayoutData(gd);
		
		setTableSize(sourceTable);
	}
	
	void populateTable(List<CalibrationSource> sourceList) {
		sourceTable.removeAll();
		
		if (sourceList==null) {
			messageCountLabel.setText("0 messages");
			return;
		}
		
		for (CalibrationSource source : sourceList) {
			TableItem row = new TableItem(sourceTable, 0);
			row.setText(new String[] {source.name, source.frame,
						source.getC1(), source.getC2(), source.catalogue, source.magnitude});
			row.setData(source);
		}
		sourceTable.redraw();
		messageCountLabel.setText("" + sourceList.size() + " messages");		
	}
		
	private void setTableSize(Table table) {
		Rectangle area = table.getParent().getClientArea();
		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getVerticalBar();
		int width = area.width - table.computeTrim(0,0,0,0).width - vBar.getSize().x;
		if (size.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns 
			// smaller first and then resize the table to
			// match the client area width
			table.getColumn(0).setWidth(width * 20/100);
			table.getColumn(1).setWidth(width * 20/100);
			table.getColumn(2).setWidth(width * 20/100);
			table.getColumn(3).setWidth(width * 20/100);
			table.getColumn(4).setWidth(width * 10/100);
			table.getColumn(5).setWidth(width * 10/100);
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table 
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			table.getColumn(0).setWidth(width * 20/100);
			table.getColumn(1).setWidth(width * 20/100);
			table.getColumn(2).setWidth(width * 20/100);
			table.getColumn(3).setWidth(width * 20/100);
			table.getColumn(4).setWidth(width * 10/100);
			table.getColumn(5).setWidth(width * 10/100);
		}
		table.pack();		
	}	
}