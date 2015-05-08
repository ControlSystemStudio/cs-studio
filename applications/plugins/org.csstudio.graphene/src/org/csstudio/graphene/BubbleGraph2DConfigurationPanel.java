package org.csstudio.graphene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class BubbleGraph2DConfigurationPanel extends
        AbstractPointDatasetGraph2DConfigurationPanel {
    private Text textSizeColumnFormula;
    private Text textColorColumnFormula;
    private Button btnHighlightFocusValue;

    public BubbleGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);

        Label lblSizeColumnFormula = new Label(this, SWT.NONE);
        lblSizeColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblSizeColumnFormula.setText("Size Column Formula:");

        textSizeColumnFormula = new Text(this, SWT.BORDER);
        textSizeColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        forwardTextEvents(textSizeColumnFormula, "sizeColumnFormula");

        Label lblColorColumnFormula = new Label(this, SWT.NONE);
        lblColorColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblColorColumnFormula.setText("Color Column Formula:");

        textColorColumnFormula = new Text(this, SWT.BORDER);
        textColorColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        forwardTextEvents(textColorColumnFormula, "colorColumnFormula");

        btnHighlightFocusValue = new Button(this, SWT.CHECK);
        btnHighlightFocusValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnHighlightFocusValue.setText("Highlight Focus Value");
        forwardCheckBoxEvents(btnHighlightFocusValue, "highlightFocusValue");
    }

    public String getSizeColumnFormula() {
        return textSizeColumnFormula.getText();
    }

    public void setSizeColumnFormula(String sizeColumnFormula) {
        if (sizeColumnFormula != null) {
            this.textSizeColumnFormula.setText(sizeColumnFormula);
            changeSupport.firePropertyChange("sizeColumnFormula", null,
                    textSizeColumnFormula.getText());
        }
    }

    public String getColorColumnFormula() {
        return textColorColumnFormula.getText();
    }

    public void setColorColumnFormula(String colorColumnFormula) {
        if (colorColumnFormula != null) {
            this.textColorColumnFormula.setText(colorColumnFormula);
            changeSupport.firePropertyChange("colorColumnFormula", null,
                    textColorColumnFormula.getText());
        }
    }

    public boolean isHighlightFocusValue() {
        return this.btnHighlightFocusValue.getSelection();
    }

    public void setHighlightFocusValue(boolean highlightFocusValue) {
        this.btnHighlightFocusValue.setSelection(highlightFocusValue);
        changeSupport.firePropertyChange("highlightFocusValue", null, isHighlightFocusValue());
    }

}
