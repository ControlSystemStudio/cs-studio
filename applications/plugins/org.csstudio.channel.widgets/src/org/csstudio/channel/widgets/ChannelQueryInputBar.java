package org.csstudio.channel.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

public class ChannelQueryInputBar extends AbstractChannelQueryWidget {

	private Combo combo;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ChannelQueryInputBar(Composite parent, int style,
			IDialogSettings dialogSettings, String settingsKey) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		ComboViewer comboViewer = new ComboViewer(this, SWT.NONE);
		combo = comboViewer.getCombo();
		ComboHistoryHelper name_helper = new ComboHistoryHelper(dialogSettings,
				settingsKey, combo, 20, true) {
			@Override
			public void newSelection(final String queryText) {
				setChannelQuery(ChannelQuery.Builder.query(queryText).create());
			}
		};
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					String newValue = "";
					if (event.getNewValue() != null)
						newValue = ((ChannelQuery) event.getNewValue()).getQuery();
					if (newValue.equals(combo.getText())) {
						combo.setText(newValue);
					}
				}
			}
		});
		
		
		name_helper.loadSettings();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
