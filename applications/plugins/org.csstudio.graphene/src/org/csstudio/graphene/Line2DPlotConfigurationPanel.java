/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.AbstractConfigurationPanel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

/**
 * @author shroffk
 * 
 */
public class Line2DPlotConfigurationPanel extends AbstractConfigurationPanel {

    private Text textXPv;
    private Text textYPv;
    private Combo comboInterpolationScheme;
    private Button btnShowAxis;

    public Line2DPlotConfigurationPanel(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(2, false));

	Label lblInterpolationScheme = new Label(this, SWT.NONE);
	lblInterpolationScheme.setLayoutData(new GridData(SWT.RIGHT,
		SWT.CENTER, false, false, 1, 1));
	lblInterpolationScheme.setText("Interpolation scheme:");

	comboInterpolationScheme = new Combo(this, SWT.NONE);
	comboInterpolationScheme.setLayoutData(new GridData(SWT.FILL,
		SWT.CENTER, true, false, 1, 1));

	Label lblYPv = new Label(this, SWT.NONE);
	lblYPv.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));
	lblYPv.setText("Y PV:");

	textYPv = new Text(this, SWT.BORDER);
	textYPv.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));

	Label lblXPv = new Label(this, SWT.NONE);
	lblXPv.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));
	lblXPv.setText("X PV(optional):");

	textXPv = new Text(this, SWT.BORDER);
	textXPv.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	new Label(this, SWT.NONE);

	btnShowAxis = new Button(this, SWT.CHECK);
	btnShowAxis.setText("Show Axis Scroll");
    }

    public String getXPv() {
	return textXPv.getText();
    }

    public void setXPv(String xPv) {
	if (xPv != null)
	    this.textXPv.setText(xPv);
    }

    public String getYPv() {
	return textYPv.getText();
    }

    public void setYPv(String yPv) {
	if (yPv != null)
	    this.textYPv.setText(yPv);
    }

    public boolean getShowAxis() {
	return false;
    }

    public void setShowAxis(boolean showAxis) {
	this.btnShowAxis.setSelection(showAxis);
    }
}
