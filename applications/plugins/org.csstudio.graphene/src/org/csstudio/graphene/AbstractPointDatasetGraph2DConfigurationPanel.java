/**
 *
 */
package org.csstudio.graphene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 *
 */
public class AbstractPointDatasetGraph2DConfigurationPanel extends
        AbstractGraph2DConfigurationPanel {

    private Text textXColumnFormula;
    private Text textYColumnFormula;

    public AbstractPointDatasetGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);

        Label lblXColumnFormula = new Label(this, SWT.NONE);
        lblXColumnFormula.setText("X Column Formula:");

        textXColumnFormula = new Text(this, SWT.BORDER);
        textXColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        forwardTextEvents(textXColumnFormula, "xColumnFormula");

        Label lblYColumnFormula = new Label(this, SWT.NONE);
        lblYColumnFormula.setText("Y Column Formula:");

        textYColumnFormula = new Text(this, SWT.BORDER);
        textYColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 1, 1));
        forwardTextEvents(textYColumnFormula, "yColumnFormula");

    }

    public String getXColumnFormula() {
        return textXColumnFormula.getText();
    }

    public void setXColumnFormula(String xColumnFormula) {
        if (xColumnFormula != null) {
            this.textXColumnFormula.setText(xColumnFormula);
            changeSupport.firePropertyChange("xColumnFormula", null,
                    textXColumnFormula.getText());
        }
    }

    public String getYColumnFormula() {
        return textYColumnFormula.getText();
    }

    public void setYColumnFormula(String yColumnFormula) {
        if (yColumnFormula != null) {
            this.textYColumnFormula.setText(yColumnFormula);
            changeSupport.firePropertyChange("yColumnFormula", null,
                    textYColumnFormula.getText());
        }
    }

}
