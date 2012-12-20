package org.csstudio.graphene;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.channel.widgets.AbstractSelectionProviderWrapper;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class ProcessVariableInputBar extends Composite implements ISelectionProvider {

	
	private ProcessVariable processVariable;
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public ProcessVariable getProcessVariable() {
		return processVariable;
	}
	
	public void setProcessVariable(ProcessVariable processVariable) {
		ProcessVariable oldValue = this.processVariable;
		this.processVariable = processVariable;
		changeSupport.firePropertyChange("processVariable", oldValue, processVariable);
	}
	
	private Combo combo;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ProcessVariableInputBar(Composite parent, int style,
			IDialogSettings dialogSettings, String settingsKey) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		ComboViewer comboViewer = new ComboViewer(this, SWT.NONE);
		combo = comboViewer.getCombo();

		ComboHistoryHelper name_helper = new ComboHistoryHelper(dialogSettings,
				settingsKey, combo, 20, true) {
			@Override
			public void newSelection(final String name) {
				setProcessVariable(new ProcessVariable(name));
			}
		};
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("processVariable".equals(event.getPropertyName())) {
					String newValue = "";
					if (event.getNewValue() != null)
						newValue = ((ProcessVariable) event.getNewValue()).getName();
					if (!newValue.equals(combo.getText())) {
						combo.setText(newValue);
					}
				}
			}
		});
		
		selectionProvider = new AbstractSelectionProviderWrapper(comboViewer, this) {
			
			@Override
			protected ISelection transform(IStructuredSelection selection) {
				if (getProcessVariable() != null)
					return new StructuredSelection(getProcessVariable());
				else
					return new StructuredSelection();
			}
		};
		
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
