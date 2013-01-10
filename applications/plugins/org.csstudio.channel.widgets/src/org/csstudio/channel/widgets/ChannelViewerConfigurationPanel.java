package org.csstudio.channel.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ChannelViewerConfigurationPanel extends
		AbstractConfigurationComposite {

	private StringListSelectionWidget propertyListWidget;
	private StringListSelectionWidget tagListWidget;

	public ChannelViewerConfigurationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		TabItem tbtmProperties = new TabItem(tabFolder, SWT.NONE);
		tbtmProperties.setText("Properties");
		propertyListWidget = new StringListSelectionWidget(tabFolder,
				SWT.NONE);
		tbtmProperties.setControl(propertyListWidget);
		propertyListWidget
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if (Arrays.asList("selectedValues")
								.contains(event.getPropertyName())) {
							changeSupport
									.firePropertyChange(new PropertyChangeEvent(
											this, event.getPropertyName(),
											event.getOldValue(), event
													.getNewValue()));
						}
					}
				});

		TabItem tbtmTags = new TabItem(tabFolder, SWT.NONE);
		tbtmTags.setText("Tags");
		tagListWidget = new StringListSelectionWidget(tabFolder, SWT.NONE);
		tbtmTags.setControl(tagListWidget);
		tagListWidget
		.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (Arrays.asList("selectedValues")
						.contains(event.getPropertyName())) {
					changeSupport
							.firePropertyChange(new PropertyChangeEvent(
									this, event.getPropertyName(),
									event.getOldValue(), event
											.getNewValue()));
				}
			}
		});

	}

	public void setSelectedProperties(List<String> properties) {
		propertyListWidget.setSelectedValues(properties);
	}

	public List<String> getSelectedProperties() {		
		return propertyListWidget.getSelectedValues();
	}
	

	public void setPossibleProperties(List<String> list) {
		propertyListWidget.setPossibleValues(list);
	}
	
	public List<String> getPossibleProperties() {
		return propertyListWidget.getPossibleValues();
	}
	
	public void setSelectedTags(List<String> tags) {
		tagListWidget.setSelectedValues(tags);
	}

	public List<String> getSelectedTags() {		
		return tagListWidget.getSelectedValues();
	}
	
	public void setPossibleTags(List<String> tags) {
		tagListWidget.setPossibleValues(tags);
	}

	public List<String> getPossibleTags() {		
		return tagListWidget.getPossibleValues();
	}

	public Object isShowChannelNames() {
		return null;
	}

}
