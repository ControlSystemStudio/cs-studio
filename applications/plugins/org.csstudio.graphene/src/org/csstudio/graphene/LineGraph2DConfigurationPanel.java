package org.csstudio.graphene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class LineGraph2DConfigurationPanel extends
        AbstractPointDatasetGraph2DConfigurationPanel {
    private Button btnHighlightFocusData;

    public LineGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);

        btnHighlightFocusData = new Button(this, SWT.CHECK);
        btnHighlightFocusData.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnHighlightFocusData.setText("Highlight Focus Data");
        forwardCheckBoxEvents(btnHighlightFocusData, "highlightFocusData");
    }

    public boolean getHighlightFocusData() {
        return getCheckBoxValue(btnHighlightFocusData);
    }

    public void setHighlightFocusData (boolean highlightFocusData) {
        setCheckBoxValue(btnHighlightFocusData, highlightFocusData);
    }
    }
