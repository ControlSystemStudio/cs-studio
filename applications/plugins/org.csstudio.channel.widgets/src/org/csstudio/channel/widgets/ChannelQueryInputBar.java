package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.util.ToolTipHelp;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class ChannelQueryInputBar extends AbstractChannelQueryWidget 
	implements ISelectionProvider {

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
		ToolTipHelp tooltip = new ToolTipHelp(combo);
		tooltip.setText(
				"Space seperated search criterias, patterns may include * and ? wildcards\n" +
				"channelNamePattern\n" +
				"propertyName=valuePattern1,valuePattern2\n" +
				"Tags=tagNamePattern\n" +
				"Each criteria is logically ANDed, || seperated values are logically ORed");

		ComboHistoryHelper name_helper = new ComboHistoryHelper(dialogSettings,
				settingsKey, combo, 20, true) {
			@Override
			public void newSelection(final String queryText) {
				setChannelQuery(null);
				setChannelQuery(ChannelQuery.query(queryText).build());
			}
		};
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					String newValue = "";
					if (event.getNewValue() != null)
						newValue = ((ChannelQuery) event.getNewValue()).getQuery();
					if (!newValue.equals(combo.getText())) {
						combo.setText(newValue);
					}
				}
			}
		});
		
		selectionProvider = new AbstractSelectionProviderWrapper(comboViewer, this) {
			
			@Override
			protected ISelection transform(IStructuredSelection selection) {
				if (getChannelQuery() != null)
					return new StructuredSelection(getChannelQuery());
				else
					return new StructuredSelection();
			}
		};
		
		combo.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// If focus is lost and the text is different,
				// change the query
				String comboText = combo.getText();
				String queryText = "";
				if (getChannelQuery() != null) {
					queryText = getChannelQuery().getQuery();
				}
				if (!comboText.equals(queryText)) {
					setChannelQuery(ChannelQuery.query(comboText).build());
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
			}
		});
		
		name_helper.loadSettings();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		combo.setMenu(menu);
	}
	
	private AbstractSelectionProviderWrapper selectionProvider;

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}
	
}
