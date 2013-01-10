package org.csstudio.channel.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A UI to display two lists of strings with button to move one or more selected
 * strings between the two groups.
 * 
 * @author shroffk
 * 
 */
public class StringListSelectionWidget extends Composite {

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private org.eclipse.swt.widgets.List unselected;
	private org.eclipse.swt.widgets.List selected;

	public StringListSelectionWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		unselected = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		unselected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Button selectButton = new Button(composite, SWT.NONE);
		selectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		selectButton.setSize(34, 30);
		selectButton.setText("-->");
		selectButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (unselected.getSelectionCount() != 0) {
					List<String> newSelection = new ArrayList<String>(
							selectedValues);
					newSelection.addAll(Arrays.asList(unselected.getSelection()));
					setSelectedValues(newSelection);
					selected.setSelection(selectedValues.size() - 1);
				}
			}
		});

		Button upButton = new Button(composite, SWT.NONE);
		upButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		upButton.setText("Move Up");
		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					int oldIndex = selected.getSelectionIndex();
					if (oldIndex != 0) {
						List<String> newSelection = new ArrayList<String>(
								selectedValues);
						newSelection.set(oldIndex - 1,
								selectedValues.get(oldIndex));
						newSelection.set(oldIndex,
								selectedValues.get(oldIndex - 1));
						setSelectedValues(newSelection);
						selected.setSelection(oldIndex - 1);
					}
				}
			}
		});

		Button downButton = new Button(composite, SWT.NONE);
		downButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		downButton.setText("Move Down");
		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					int oldIndex = selected.getSelectionIndex();
					if (oldIndex != selectedValues.size() - 1) {
						List<String> newSelection = new ArrayList<String>(
								selectedValues);
						newSelection.set(oldIndex + 1,
								selectedValues.get(oldIndex));
						newSelection.set(oldIndex,
								selectedValues.get(oldIndex + 1));
						setSelectedValues(newSelection);
						selected.setSelection(oldIndex + 1);
					}
				}
			}
		});

		Button unselectButton = new Button(composite, SWT.NONE);
		unselectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		unselectButton.setText("<--");
		unselectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					List<String> newSelection = new ArrayList<String>(
							selectedValues);
					newSelection.removeAll(Arrays.asList(selected
							.getSelection()));
					setSelectedValues(newSelection);
				}
			}
		});

		selected = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		selected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	private List<String> possibleValues = new ArrayList<String>();
	private List<String> selectedValues = new ArrayList<String>();

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(Collection<String> possibleValues) {
		this.possibleValues = new ArrayList<String>(possibleValues);
		Collections.sort(this.possibleValues);
	}

	public List<String> getSelectedValues() {
		return selectedValues;
	}

	public void setSelectedValues(List<String> selectedValues) {
		// Make a copy of the old properties, make sure the new ones are in the
		// list
		List<String> oldSelectedValues = this.selectedValues;
		this.selectedValues = new ArrayList<String>(selectedValues);
		this.selectedValues.retainAll(possibleValues);

		// Change the lists accordingly
		selected.setItems(selectedValues.toArray(new String[selectedValues
				.size()]));
		List<String> unselectedValues = new ArrayList<String>(possibleValues);
		unselectedValues.removeAll(this.selectedValues);
		unselected.setItems(unselectedValues
				.toArray(new String[unselectedValues.size()]));
		changeSupport.firePropertyChange("selectedValues", oldSelectedValues,
				this.selectedValues);
	}

}
