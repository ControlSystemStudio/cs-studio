package org.csstudio.graphene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.MultiAxisLineGraph2DRenderer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class MultiAxisLineGraph2DConfigurationPanel extends
        AbstractPointDatasetGraph2DConfigurationPanel {
    private Combo comboInterpolation;
    private Button btnSeparateAreas;

    public MultiAxisLineGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);

        Label lblInterpolation = new Label(this, SWT.NONE);
        lblInterpolation.setText("Interpolation:");

        comboInterpolation = new Combo(this, SWT.NONE);
        comboInterpolation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboInterpolation.setItems(ComboDataUtil.toStringArray(MultiAxisLineGraph2DRenderer.supportedInterpolationScheme));
        forwardComboEvents(comboInterpolation, "interpolation");

        btnSeparateAreas = new Button(this, SWT.CHECK);
        btnSeparateAreas.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnSeparateAreas.setText("Separate Areas");
        forwardCheckBoxEvents(btnSeparateAreas, "separateAreas");
    }

    public InterpolationScheme getInterpolation() {
        return InterpolationScheme.valueOf(comboInterpolation.getText());
    }

    public void setInterpolation(InterpolationScheme interpolation) {
        comboInterpolation.setText(interpolation.toString());
    }

    public boolean isSeparateAreas() {
        return getCheckBoxValue(btnSeparateAreas);
    }

    public void setSeparateAreas(boolean separateAreas) {
        setCheckBoxValue(btnSeparateAreas, separateAreas);
    }
}
