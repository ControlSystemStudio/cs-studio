/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.csstudio.logbook.util.LogEntrySearchUtil;
import org.csstudio.ui.util.DelayedNotificator;
import org.csstudio.ui.util.widgets.MultipleSelectionCombo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * @author shroffk
 * 
 */
public class LogViewConfigurationDialog extends Dialog {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @return the expandable
     */
    public boolean isExpandable() {
        return expandable;
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    // Model
    private boolean expandable;
    private int order;

    public LogViewConfigurationDialog(Shell parentShell, boolean expandable, int order) {
	super(parentShell);
	this.expandable = expandable;
	this.order = order;
	setBlockOnOpen(false);
	setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
    }

    @Override
    public Control createDialogArea(Composite parent) {
	getShell().setText("Configuration Options");
	Composite container = (Composite) super.createDialogArea(parent);
	container.setLayout(new GridLayout(2, false));
	
	final Button btnConfirm = new Button(container, SWT.CHECK);
	btnConfirm.setText("Expanded");
	btnConfirm.setSelection(expandable);
	btnConfirm.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

	btnConfirm.addSelectionListener(new SelectionAdapter()
	{
	    @Override
	    public void widgetSelected(SelectionEvent e)
	    {
	        expandable = btnConfirm.getSelection();
	    }
	});
	
	Label label = new Label(container, SWT.NONE);
	label.setText("Edited logEntry order:");
	
	final Combo combo = new Combo (container, SWT.READ_ONLY);
	combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	combo.setItems (new String [] {"Oldest", "Newest"});
	combo.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e)
	    {
	        switch (combo.getText()) {
		case "Oldest":
		    order = SWT.UP;
		    break;
		case "Newest":
		    order = SWT.DOWN;
		    break;
		default:
		    order = SWT.DOWN;
		    break;
		}
	    }
	});
	
	return container;

    }

}
