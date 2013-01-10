package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.Channel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;

public class ChannelTreeByPropertyConfigurationPanel extends AbstractConfigurationComposite {

	private PropertyListSelectionWidget listWidget;
	private Button showChannelNames;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ChannelTreeByPropertyConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		listWidget = new PropertyListSelectionWidget(this, SWT.NONE);
		FormData fd_listWidget = new FormData();
		fd_listWidget.right = new FormAttachment(100);
		fd_listWidget.top = new FormAttachment(0);
		fd_listWidget.left = new FormAttachment(0);
		listWidget.setLayoutData(fd_listWidget);
		listWidget.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (Arrays.asList("channels", "selectedProperties").contains(event.getPropertyName())) {
					changeSupport.firePropertyChange(new PropertyChangeEvent(this, event.getPropertyName(),
							event.getOldValue(), event.getNewValue()));
				}
			}
		});
		
		showChannelNames = new Button(this, SWT.CHECK);
		fd_listWidget.bottom = new FormAttachment(showChannelNames, -6);
		FormData fd_showChannelNames = new FormData();
		fd_showChannelNames.bottom = new FormAttachment(100, -10);
		fd_showChannelNames.left = new FormAttachment(0, 10);
		showChannelNames.setLayoutData(fd_showChannelNames);
		showChannelNames.setText("Show channel names");
		showChannelNames.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("showChannelNames", !showChannelNames.getSelection(), showChannelNames.getSelection());
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("showChannelNames", !showChannelNames.getSelection(), showChannelNames.getSelection());
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public Collection<Channel> getChannels() {
		return listWidget.getChannels();
	}

	public void setChannels(Collection<Channel> channels) {
		listWidget.setChannels(channels);
	}
	
	public List<String> getSelectedProperties() {
		return listWidget.getSelectedProperties();
	}

	public void setSelectedProperties(List<String> selectedProperties) {
		listWidget.setSelectedProperties(selectedProperties);
	}
	
	public boolean isShowChannelNames() {
		return showChannelNames.getSelection();
	}
	
	public void setShowChannelNames(boolean flag) {
		showChannelNames.setSelection(flag);
	}
	
}
