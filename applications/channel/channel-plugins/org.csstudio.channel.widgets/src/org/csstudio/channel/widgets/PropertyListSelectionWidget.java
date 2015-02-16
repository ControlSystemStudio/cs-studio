package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class PropertyListSelectionWidget extends Composite {
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	private org.eclipse.swt.widgets.List unselected;
	private org.eclipse.swt.widgets.List selected;

	public PropertyListSelectionWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		unselected = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		unselected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Button selectButton = new Button(composite, SWT.NONE);
		selectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectButton.setSize(34, 30);
		selectButton.setText("-->");
		selectButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (unselected.getSelectionCount() != 0) {
					List<String> newSelection = new ArrayList<String>(selectedProperties);
					newSelection.addAll(Arrays.asList(unselected.getSelection()));
					setSelectedProperties(newSelection);
					selected.setSelection(selectedProperties.size() - 1);
				}
			}
		});
		
		Button upButton = new Button(composite, SWT.NONE);
		upButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		upButton.setText("Move Up");
		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					int oldIndex = selected.getSelectionIndex();
					if (oldIndex != 0) {
						List<String> newSelection = new ArrayList<String>(selectedProperties);
						newSelection.set(oldIndex - 1, selectedProperties.get(oldIndex));
						newSelection.set(oldIndex, selectedProperties.get(oldIndex - 1));
						setSelectedProperties(newSelection);
						selected.setSelection(oldIndex - 1);
					}
				}
			}
		});
		
		Button downButton = new Button(composite, SWT.NONE);
		downButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		downButton.setText("Move Down");
		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					int oldIndex = selected.getSelectionIndex();
					if (oldIndex != selectedProperties.size() - 1) {
						List<String> newSelection = new ArrayList<String>(selectedProperties);
						newSelection.set(oldIndex + 1, selectedProperties.get(oldIndex));
						newSelection.set(oldIndex, selectedProperties.get(oldIndex + 1));
						setSelectedProperties(newSelection);
						selected.setSelection(oldIndex + 1);
					}
				}
			}
		});
		
		Button unselectButton = new Button(composite, SWT.NONE);
		unselectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		unselectButton.setText("<--");
		unselectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected.getSelectionCount() != 0) {
					List<String> newSelection = new ArrayList<String>(selectedProperties);
					newSelection.removeAll(Arrays.asList(selected.getSelection()));
					setSelectedProperties(newSelection);
				}
			}
		});
		
		selected = new org.eclipse.swt.widgets.List(this, SWT.BORDER);
		selected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
	}
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public Collection<Channel> getChannels() {
		return channels;
	}
	
	private Collection<Channel> channels;
	private List<String> possibleProperties = new ArrayList<String>();
	private List<String> selectedProperties = new ArrayList<String>();
	
	public void setChannels(Collection<Channel> channels) {
		Collection<Channel> oldChannels = this.channels;
		this.channels = channels;
		this.possibleProperties = new ArrayList<String>(ChannelUtil.getPropertyNames(channels));
		Collections.sort(this.possibleProperties);
		changeSupport.firePropertyChange("channels", oldChannels, channels);
	}
	
	public List<String> getSelectedProperties() {
		return selectedProperties;
	}
	
	public void setSelectedProperties(List<String> selectedProperties) {
		// Make a copy of the old properties, make sure the new ones are in the list
		List<String> oldSelectedProperties = this.selectedProperties;
		this.selectedProperties = new ArrayList<String>(selectedProperties);
		this.selectedProperties.retainAll(possibleProperties);
		
		// Change the lists accordingly
		selected.setItems(selectedProperties.toArray(new String[selectedProperties.size()]));
		List<String> unselectedProperties = new ArrayList<String>(possibleProperties);
		unselectedProperties.removeAll(this.selectedProperties);
		unselected.setItems(unselectedProperties.toArray(new String[unselectedProperties.size()]));
		
		changeSupport.firePropertyChange("selectedProperties", oldSelectedProperties, this.selectedProperties);
	}
	
}
