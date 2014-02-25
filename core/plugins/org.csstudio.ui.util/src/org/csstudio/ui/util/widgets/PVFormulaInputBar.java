package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

/**
 * A widget to enter a pv formula.
 * <p>
 * It allows the user to enter a pv formula in a text field, with a drop down
 * with the last 20 entries.
 * <p>
 * TODO once it's clear what class to use for PVFormula, the selection will
 * change from String to that.
 * 
 * @author carcassi
 *
 */
public class PVFormulaInputBar extends AbstractPVFormulaWidget {
	// implements ISelectionProvider {

	// ITER: changed text to text to integrate auto-complete
	private Text text;
	private Label lblPvFormula;

	/**
	 * Create the composite.
	 * 
	 * @param parent the parent
	 * @param style the SWT style
	 * @param dialogSettings where to store the text history
	 * @param settingsKey the key to use for storing the text history
	 */
	public PVFormulaInputBar(Composite parent, int style,
			IDialogSettings dialogSettings, String settingsKey) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		lblPvFormula = new Label(this, SWT.NONE);
		lblPvFormula.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPvFormula.setText("PV Formula:");

		text = new Text(this, SWT.BORDER);
		text.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				setPVFormula(text.getText());
			}
		});
		new AutoCompleteWidget(text, AutoCompleteTypes.Formula);
		// ComboViewer comboViewer = new ComboViewer(this, SWT.NONE);
		// text = comboViewer.getCombo();
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// ComboHistoryHelper name_helper = new
		// ComboHistoryHelper(dialogSettings, settingsKey, text, 20, true) {
		// @Override
		// public void newSelection(final String queryText) {
		// setPVFormula(queryText);
		// }
		// };

		// selectionProvider = new AbstractSelectionProviderWrapper(comboViewer,
		// this) {
		//
		// @Override
		// protected ISelection transform(IStructuredSelection selection) {
		// if (getChannelQuery() != null)
		// return new StructuredSelection(getChannelQuery());
		// else
		// return new StructuredSelection();
		// }
		// };

		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// If focus is lost and the text is different,
				// change the query
				String comboText = text.getText();
				String queryText = "";
				if (getPVFormula() != null) {
					queryText = getPVFormula();
				}
				if (!comboText.equals(queryText)) {
					setPVFormula(queryText);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
			}
		});

		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("pvFormula".equals(event.getPropertyName())) {
					String newValue = "";
					if (event.getNewValue() != null)
						newValue = (String) event.getNewValue();
					if (!newValue.equals(text.getText())) {
						text.setText(newValue);
					}
				}
			}
		});

		// name_helper.loadSettings();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		text.setMenu(menu);
	}
	
	public String getLabelText() {
		return lblPvFormula.getText();
	}
	
	public void setLabelText(String text) {
		lblPvFormula.setText(text);
	}

	// private AbstractSelectionProviderWrapper selectionProvider;
	//
	// @Override
	// public void addSelectionChangedListener(final ISelectionChangedListener
	// listener) {
	// selectionProvider.addSelectionChangedListener(listener);
	// }
	//
	// @Override
	// public ISelection getSelection() {
	// return selectionProvider.getSelection();
	// }
	//
	// @Override
	// public void removeSelectionChangedListener(
	// ISelectionChangedListener listener) {
	// selectionProvider.removeSelectionChangedListener(listener);
	// }
	//
	// @Override
	// public void setSelection(ISelection selection) {
	// selectionProvider.setSelection(selection);
	// }

}
