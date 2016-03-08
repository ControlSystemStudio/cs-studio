package org.csstudio.unitconversion.ui;

import static gov.bnl.unitconversion.ConversionAlgorithm.ConversionAlogrithmBuilder.conversionAlgorithm;
import gov.bnl.unitconversion.ConversionAlgorithm;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author shroffk
 *
 */

public class ConversionAlgorithmTableTest {
    private static ConversionAlgorithmTable conversionAlgorithmTable;

    public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(1, false));

    Button btnNewButton = new Button(shell, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        Map<String, ConversionAlgorithm> testData = new HashMap<String, ConversionAlgorithm>();
        testData.put(
            "i2b",
            conversionAlgorithm(
                1,
                "2.717329e-13*input**4 -4.50853e-10*input**3 + 2.156812e-07*input**2 + 0.001495718*input + 0.0014639")
                .build());
        testData.put(
            "i2b",
            conversionAlgorithm(
                1,
                "1.239146e-12*input**4 -2.242334e-09*input**3 + 1.117486e-06*input**2 + 0.007377142*input + 0.007218819")
                .build());
        testData.put(
            "i2b",
            conversionAlgorithm(
                1,
                "-7.736754e-11*input**4 + 1.078356e-07*input**3 -4.27955e-05*input**2 + 0.061426*input + 0.031784")
                .build());
        testData.put(
            "b2i",
            conversionAlgorithm(
                1,
                "-33.289411*input**4 + 84.116293*input**3 -61.320653*input**2 + 668.452373*input -0.969042")
                .build());
        conversionAlgorithmTable.setConversionAlgorithms(testData);
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        false, 1, 1));
    btnNewButton.setText("Add Test Data");

    conversionAlgorithmTable = new ConversionAlgorithmTable(shell, SWT.NONE);
    conversionAlgorithmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
        true, true, 1, 1));
    shell.setSize(400, 300);
    shell.open();
    while (!shell.isDisposed())
        if (!display.readAndDispatch())
        display.sleep();
    display.dispose();
    }
}
