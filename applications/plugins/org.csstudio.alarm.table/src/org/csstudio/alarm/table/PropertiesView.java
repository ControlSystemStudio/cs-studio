package org.csstudio.alarm.table;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class PropertiesView extends ViewPart {

	public static final String ID = PropertiesView.class.getName();
	
	public PropertiesView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		Table table = new Table(parent, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewer propertiesTabelViewer = new TableViewer(table);
		
		propertiesTabelViewer.setContentProvider(new PropertiesContentProvider());
		propertiesTabelViewer.setLabelProvider(new PropertiesLabelProvider());
		
		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		parent.pack();
		
		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setText("Property");
		column.setWidth(200);
		column = new TableColumn(table, SWT.NONE);
		column.setText("Value");
		column.setWidth(200);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class PropertiesLabelProvider extends LabelProvider {
		
	}
	
	class PropertiesContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			return null;
		}

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
