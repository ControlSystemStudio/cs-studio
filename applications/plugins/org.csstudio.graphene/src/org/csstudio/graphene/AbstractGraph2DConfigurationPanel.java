/**
 *
 */
package org.csstudio.graphene;

import org.csstudio.utility.pvmanager.widgets.AbstractConfigurationPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 *
 */
public class AbstractGraph2DConfigurationPanel extends
        AbstractConfigurationPanel {

    private Text textDataFormula;
    private Button btnResizableAxis;

    public AbstractGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));

        Label lblDataFormula = new Label(this, SWT.NONE);
        lblDataFormula.setText("Data Formula:");

        textDataFormula = new Text(this, SWT.BORDER);
        textDataFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false, 1, 1));
        forwardTextEvents(textDataFormula, "dataFormula");

    }

    public String getDataFormula() {
        return textDataFormula.getText();
    }

    public void setDataFormula(String dataFormula) {
        if (dataFormula != null) {
            this.textDataFormula.setText(dataFormula);
            changeSupport.firePropertyChange("dataFormula", null,
                    textDataFormula.getText());
        }
    }

    public boolean isResizableAxis() {
        return this.btnResizableAxis.getSelection();
    }

    public void setShowAxis(boolean showAxis) {
        this.btnResizableAxis.setSelection(showAxis);
        changeSupport.firePropertyChange("resizableAxis", null, isResizableAxis());
    }

}
