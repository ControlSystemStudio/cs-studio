/**
 *
 */
package org.csstudio.utility.channel.actions;


import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * @author Kunal Shroff
 *
 */
public class CreatePropertyDialog  extends TitleAreaDialog{

    private String propertyName;
    private String propertyOwner;
    private Text textPropertyName;
    private Text textPropertyOwner;


    /**
     * Create a dialog with the an initial property name <tt>propertyName</tt>
     *
     * @param parentShell
     * @param tagName
     */
    public CreatePropertyDialog(Shell parentShell, String propertyName) {
            super(parentShell);
            this.propertyName = propertyName;
    }

    /**
     * Creates the dialog's contents
     *
     * @param parent
     *            the parent composite
     * @return Control
     */
    protected Control createContents(Composite parent) {
            Control contents = super.createContents(parent);

            // Set the title
            setTitle("Create a New Property");

            // Set the message
            setMessage("The selected Property is not present in the channelfinder service.\n Create a new Property with the following credentials.");

            return contents;
    }

    /**
     * Creates the gray area
     *
     * @param parent
     *            the parent composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            composite.setFont(parent.getFont());
            composite.setLayout(new GridLayout(2, false));

            Label lblPropertyName = new Label(composite, SWT.NONE);
            lblPropertyName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            lblPropertyName.setText("Property Name: ");

            textPropertyName = new Text(composite, SWT.BORDER);
            textPropertyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            textPropertyName.setText(propertyName);

            Label lblPropertyOwner = new Label(composite, SWT.NONE);
            lblPropertyOwner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            lblPropertyOwner.setText("Property Owner: ");

            textPropertyOwner = new Text(composite, SWT.BORDER);
            textPropertyOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            return composite;
    }

    protected void okPressed() {
            propertyName = textPropertyName.getText();
            propertyOwner = textPropertyOwner.getText();
            super.okPressed();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyOwner() {
        return propertyOwner;
    }

}
