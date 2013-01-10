package org.csstudio.swt.widgets.natives;

/*
 * Table example snippet: update table item text
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.swt.widgets.natives.SpreadSheetTable.ITableCellEditingListener;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.*;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.junit.Before;
import org.junit.Test;


public class SpreadSheetTableTest {



@Test
public void testMain() {
	final Display display = new Display();
	Shell shell = new Shell(display);
	shell.setBounds(10, 10, 500, 600);
	shell.setLayout(new FillLayout(SWT.VERTICAL));
//	Button bt = new Button(shell, SWT.CHECK);
	final SpreadSheetTable table = new SpreadSheetTable(shell);
//	table.setColumnHeaderVisible(false);
	table.setColumnHeaders(new String[]{"a", "b", "c"});
//	table.setEnabled(false);
	table.setEditable(false);
	table.getTableViewer().getTable().setFont(new Font(Display.getCurrent(), "Verdana", 10, SWT.ITALIC));
//	table.getTableViewer().getTable().setEnabled(false);
	final List<List<String>> input = new ArrayList<List<String>>();
	
	//init table
	int rowCount=300;
	int colCount=3;
	for(int i=0; i<rowCount; i++){
		List<String> colList =new ArrayList<String>(colCount);
		for(int j=0;j<colCount; j++){
			colList.add(""+i*j);
		}
		input.add(colList);
	}
	table.setColumnsCount(colCount);

	table.setInput(input);
	table.setCellForeground(3,2, new RGB(211,1,23));
	
	table.setCellBackground(2,1, new RGB(0,0,255));
	table.setRowBackground(2, new RGB(255,0,5));
	table.setRowForeground(1, new RGB(0,224,0));
//	table.getTableViewer().getTable().getItem(2).setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
//	table.getTableViewer().getTable().getItem(1).setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
//	table.getTableViewer().getTable().getItem(3).setBackground(2, Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
//	table.getTableViewer().getTable().getItem(3).setForeground(1, Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));

	
	table.getTableViewer().getTable().addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseDown(MouseEvent e) {
			
			int[] index = table.getRowColumnIndex(new Point(e.x, e.y));
			if(index != null)
			System.out.println(e.y + " "+ Arrays.toString(index) + " " + table.getCellText(index[0], index[1]));
		
		}
		
	});
	
	table.addCellEditingListener(new ITableCellEditingListener() {
		
		public void cellValueChanged(int row, int col, String oldValue,
				String newValue) {
			System.out.println(row + " " + col + " " + oldValue + " " + newValue);
			
		}
	});
	
	
	Composite composite = new Composite(shell, SWT.None);
	composite.setLayout(new FillLayout(SWT.VERTICAL));
	
	Button button = new Button(composite, SWT.PUSH);
	button.setText("Insert Column");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			table.insertColumn(2);
			table.autoSizeColumns();
		}
	});
	
	 button = new Button(composite, SWT.PUSH);
	button.setText("Delete Column");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			table.deleteColumn(0);
		}
	});
	
	button = new Button(composite, SWT.PUSH);
	button.setText("Insert Row");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			table.insertRow(1);
		}
	});
	
	button = new Button(composite, SWT.PUSH);
	button.setText("Set cell text");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
				
			for(int j=0; j<table.getColumnCount(); j++)
				table.setColumnHeader(j, ""+j);
			table.setColumnHeaders(new String[]{"a","b","c"});
			table.setCellText(2,2,""+Math.random());
			table.autoSizeColumns();
		}
	});
	
	button = new Button(composite, SWT.PUSH);
	button.setText("Set Content");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			String[][] content = table.getSelection();
			table.setContent(content);
		}
	});
	
	button = new Button(composite, SWT.PUSH);
	button.setText("Print Selection");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			String[][] content = table.getSelection();
			for(int i=0; i<content.length; i++)
				System.out.println(Arrays.toString(content[i]));
		}
	});
	
	button = new Button(composite, SWT.PUSH);
	button.setText("Print");
	button.pack();
	button.addListener(SWT.Selection, new Listener() {
		public void handleEvent(Event event) {
			String[][] content = table.getContent();
			for(int i=0; i<content.length; i++)
				System.out.println(Arrays.toString(content[i]));
		}
	});
	

	shell.open();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}
	display.dispose();
}


}