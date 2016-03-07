/**
 *
 */
package org.csstudio.utility.channel.actions;

import java.util.Collection;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Kunal Shroff
 *
 */
public class AddPropertyDialog extends TitleAreaDialog {

    private Collection<String> propertyNames;

    private String propertyName;
    private String propertyValue;

    // property names
    private Combo combo;
    // property value
    private Text text;

    public AddPropertyDialog(Shell parentShell,
            Collection<String> existingPropertyNames) {
        super(parentShell);
        this.propertyNames = existingPropertyNames;
    }

    /**
     * Creates the dialog's contents
     *
     * @param parent the parent composite
     * @return Control
     */
    @Override
    protected Control createContents(Composite parent) {
      Control contents = super.createContents(parent);

      // Set the title
      setTitle("Add Property");

      // Set the message
      setMessage("Add the following selected property to all the selection channel");

      return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        new Label(composite, SWT.NONE);

        Label propertyNameLabel = new Label(composite, SWT.NONE);
        propertyNameLabel.setText("Property Name:");

        combo = new Combo(composite, SWT.NONE);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        combo.setItems(propertyNames.toArray(new String[propertyNames.size()]));
        combo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                propertyName = combo.getText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        combo.addModifyListener((ModifyEvent e) -> {
            propertyName = ((Combo) e.getSource()).getText();
        });

        Label propertyValueLabel = new Label(composite, SWT.NONE);
        propertyValueLabel.setText("Property Value:");

        text = new Text(composite, SWT.NONE);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        text.addModifyListener((ModifyEvent e) -> {
            propertyValue = text.getText();
        });

        return super.createDialogArea(parent);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

}
