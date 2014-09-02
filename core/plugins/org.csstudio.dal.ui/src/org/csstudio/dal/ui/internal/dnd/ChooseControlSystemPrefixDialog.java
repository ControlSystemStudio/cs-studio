/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 /**
 * 
 */
package org.csstudio.dal.ui.internal.dnd;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
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

public class ChooseControlSystemPrefixDialog extends MessageDialog {
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
		tv.setInput(ControlSystemEnum.valuesShown());

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
		
		tv.setSelection(new StructuredSelection(ProcessVariableAdressFactory.getInstance().getDefaultControlSystem()));

		final Button dontAskAgainButton = new Button(parent, SWT.CHECK);
		dontAskAgainButton.setText("Don´t ask again!");
		dontAskAgainButton.setSelection(!ProcessVariableAdressFactory.getInstance().askForControlSystem());
		
		dontAskAgainButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				_dontAskAgain = dontAskAgainButton.getSelection();
			}
			
		});
		
		Label l = new Label(parent, SWT.NONE);
		l.setText("Note: You can change the default setting on the | > CSS Core > Control System | preference page.");
		return parent;
	}

	public ControlSystemEnum getSelectedControlSystem() {
		return _selectedControlSystem;
	}

	public boolean dontAskAgain() {
		return _dontAskAgain;
	}

}
