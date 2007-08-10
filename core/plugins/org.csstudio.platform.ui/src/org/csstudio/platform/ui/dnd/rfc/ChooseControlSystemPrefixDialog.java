/**
 * 
 */
package org.csstudio.platform.ui.dnd.rfc;

import org.csstudio.platform.model.rfc.ControlSystemEnum;
import org.csstudio.platform.model.rfc.PvAdressFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

class ChooseControlSystemPrefixDialog extends MessageDialog {
	private ControlSystemEnum _selectedControlSystem;
	private boolean _dontAskAgain;

	public ChooseControlSystemPrefixDialog(Shell parentShell) {
		 
		super(parentShell, "Control System Prefix", null, "Please choose the appropriate control system.",
				MessageDialog.QUESTION, new String[] { "Ok", "Cancel" }, 0);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		TreeViewer tv = new TreeViewer(parent);
		tv.setLabelProvider(new WorkbenchLabelProvider());
		tv.setContentProvider(new BaseWorkbenchContentProvider() {

			@Override
			public Object[] getElements(Object element) {
				return (Object[]) element;
			}

		});
		tv.setInput(ControlSystemEnum.values());

		tv.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event
						.getSelection();
				if (sel != null && sel.getFirstElement() != null) {
					_selectedControlSystem = (ControlSystemEnum) sel
							.getFirstElement();
				}
			}

		});
		
		tv.setSelection(new StructuredSelection(PvAdressFactory.getInstance().getDefaultControlSystem()));

		final Button dontAskAgainButton = new Button(parent, SWT.CHECK);
		dontAskAgainButton.setText("Don´t ask again!");
		dontAskAgainButton.setSelection(!PvAdressFactory.getInstance().askForControlSystem());
		
		dontAskAgainButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				_dontAskAgain = dontAskAgainButton.getSelection();
			}
			
		});
		
		Label l = new Label(parent, SWT.NONE);
		l.setText("Note: You can change the default settting on the | > CSS Core > Control System | preference page.");
		return parent;
	}

	public ControlSystemEnum getSelectedControlSystem() {
		return _selectedControlSystem;
	}

	public boolean dontAskAgain() {
		return _dontAskAgain;
	}

}