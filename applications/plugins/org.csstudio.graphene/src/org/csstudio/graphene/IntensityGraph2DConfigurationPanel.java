package org.csstudio.graphene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.epics.graphene.NumberColorMap;
import org.epics.graphene.NumberColorMaps;
import org.eclipse.swt.widgets.Button;

public class IntensityGraph2DConfigurationPanel extends
        AbstractGraph2DConfigurationPanel {
    private Combo colorMapCombo;
    private Button btnDrawLegend;

    public IntensityGraph2DConfigurationPanel(Composite parent,
            int style) {
        super(parent, style);

        Label lblColorMap = new Label(this, SWT.NONE);
        lblColorMap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblColorMap.setText("Color Map");

        colorMapCombo = new Combo(this, SWT.NONE);
        colorMapCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        List<String> colorMapNames = new ArrayList<String>(NumberColorMaps.getRegisteredColorSchemes().keySet());
        Collections.sort(colorMapNames);
        colorMapCombo.setItems(colorMapNames.toArray(new String[colorMapNames.size()]));
        forwardComboEvents(colorMapCombo, "colorMap");

        btnDrawLegend = new Button(this, SWT.CHECK);
        btnDrawLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnDrawLegend.setText("Draw Legend");
        forwardCheckBoxEvents(btnDrawLegend, "drawLegend");
    }

    public NumberColorMap getColorMap() {
        return NumberColorMaps.getRegisteredColorSchemes().get(comboSelectedValue(colorMapCombo));
    }

    public void setColorMap(NumberColorMap colorMap) {
        String key = null;
        for (Map.Entry<String, NumberColorMap> entry : NumberColorMaps.getRegisteredColorSchemes().entrySet()) {
            if (entry.getValue().equals(colorMap)) {
                key = entry.getKey();
            }
        }

        if (key == null) {
            colorMapCombo.select(-1);
            return;
        }

        int index = Arrays.asList(colorMapCombo.getItems()).indexOf(key);
        colorMapCombo.select(index);
    }

    public boolean isDrawLegend() {
        return btnDrawLegend.getSelection();
    }

    public void setDrawLegend(boolean drawLegend) {
        btnDrawLegend.setSelection(drawLegend);
    }
}
