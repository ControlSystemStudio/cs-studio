package org.csstudio.channel.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PVTableByPropertyConfigurationPanel extends
AbstractConfigurationComposite {
	private Combo rowProperty;
	private Combo columnProperty;
	private StringListSelectionWidget columnTags;

	public PVTableByPropertyConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblRow = new Label(this, SWT.NONE);
		FormData fd_lblRow = new FormData();
		fd_lblRow.top = new FormAttachment(0, 13);
		fd_lblRow.left = new FormAttachment(0, 10);
		lblRow.setLayoutData(fd_lblRow);
		lblRow.setText("Row:");
		
		rowProperty = new Combo(this, SWT.NONE);
		FormData fd_rowProperty = new FormData();
		fd_rowProperty.left = new FormAttachment(lblRow, 6);
		fd_rowProperty.top = new FormAttachment(0, 10);
		rowProperty.setLayoutData(fd_rowProperty);
		rowProperty.addSelectionListener(forwardSelectionListener("rowProperty"));
		
		Label lblColumn = new Label(this, SWT.NONE);
		fd_rowProperty.right = new FormAttachment(lblColumn, -6);
		FormData fd_lblColumn = new FormData();
		fd_lblColumn.left = new FormAttachment(50, 0);
		fd_lblColumn.top = new FormAttachment(0, 13);
		lblColumn.setLayoutData(fd_lblColumn);
		lblColumn.setText("Column:");
		
		columnProperty = new Combo(this, SWT.NONE);
		FormData fd_columnProperty = new FormData();
		fd_columnProperty.left = new FormAttachment(lblColumn, 6);
		fd_columnProperty.top = new FormAttachment(lblRow, -3, SWT.TOP);
		columnProperty.setLayoutData(fd_columnProperty);
		columnProperty.addSelectionListener(forwardSelectionListener("columnProperty"));
		
		Label lblColumnTags = new Label(this, SWT.NONE);
		FormData fd_lblColumnTags = new FormData();
		fd_lblColumnTags.top = new FormAttachment(rowProperty, 6);
		fd_lblColumnTags.left = new FormAttachment(0, 10);
		lblColumnTags.setLayoutData(fd_lblColumnTags);
		lblColumnTags.setText("Column tags:");
		
		columnTags = new StringListSelectionWidget(this, SWT.NONE);
		fd_columnProperty.right = new FormAttachment(columnTags, 0, SWT.RIGHT);
		FormData fd_columnTags = new FormData();
		fd_columnTags.bottom = new FormAttachment(100, -5);
		fd_columnTags.right = new FormAttachment(100, -5);
		fd_columnTags.top = new FormAttachment(lblColumnTags, 6);
		fd_columnTags.left = new FormAttachment(0, 5);
		columnTags.setLayoutData(fd_columnTags);
		columnTags.addPropertyChangeListener(forwardPropertyChangeListener("selectedValues", "columnTags"));
	}
	
	private PropertyChangeListener forwardPropertyChangeListener(final String widgetProperty,
			final String panelProperty) {
		return new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (widgetProperty.equals(evt.getPropertyName())) {
					changeSupport.firePropertyChange(panelProperty, evt.getOldValue(), evt.getNewValue());
				}
			}
		};
	}

	protected SelectionListener forwardSelectionListener(final String propertyName) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Find a solution for the values
				changeSupport.firePropertyChange(propertyName, null, null);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	public List<String> getPossibleProperties() {
		return Arrays.asList(rowProperty.getItems());
	}
	
	public void setPossibleProperties(Collection<String> possibleProperties) {
		String[] properties = possibleProperties.toArray(new String[possibleProperties.size()]);
		rowProperty.setItems(properties);
		columnProperty.setItems(properties);
	}
	
	public List<String> getPossibleTags() {
		return columnTags.getPossibleValues();
	}
	
	public void setPossibleTags(Collection<String> possibleTags) {
		columnTags.setPossibleValues(possibleTags);
	}
	
	public String getRowProperty() {
		if (rowProperty.getSelectionIndex() == -1)
			return null;
		return rowProperty.getItem(rowProperty.getSelectionIndex());
	}
	
	public void setRowProperty(String string) {
		int index = Arrays.asList(rowProperty.getItems()).indexOf(string);
		rowProperty.select(index);
	}
	
	public String getColumnProperty() {
		if (columnProperty.getSelectionIndex() == -1)
			return null;
		return columnProperty.getItem(columnProperty.getSelectionIndex());
	}
	
	public void setColumnProperty(String string) {
		int index = Arrays.asList(columnProperty.getItems()).indexOf(string);
		columnProperty.select(index);
	}
	
	public List<String> getColumnTags() {
		return columnTags.getSelectedValues();
	}
	
	public void setColumnTags(List<String> selectedValues) {
		columnTags.setSelectedValues(selectedValues);
	}
}
