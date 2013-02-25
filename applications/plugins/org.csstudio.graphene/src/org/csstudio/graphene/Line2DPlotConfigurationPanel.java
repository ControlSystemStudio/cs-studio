/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.AbstractConfigurationPanel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
	textYPv.addListener(SWT.DefaultSelection, new Listener() {

	    @Override
	    public void handleEvent(Event event) {
		changeSupport.firePropertyChange("yPv", null, textYPv.getText());

	    }
	});

	Label lblXPv = new Label(this, SWT.NONE);
	lblXPv.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
		1, 1));
	lblXPv.setText("X PV(optional):");

	textXPv = new Text(this, SWT.BORDER);
	textXPv.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	textXPv.addListener(SWT.DefaultSelection, new Listener() {

	    @Override
	    public void handleEvent(Event event) {
		changeSupport.firePropertyChange("xPv", null, textXPv.getText());
	    }
	});

	btnShowAxis = new Button(this, SWT.CHECK);
	btnShowAxis.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		changeSupport.firePropertyChange("showAxisScroll", null,
			getShowAxis());
	    }
	});
	btnShowAxis.setText("Show Axis Scroll");
    }

    public String getXPv() {
	return textXPv.getText();
    }

    public void setXPv(String xPv) {
	if (xPv != null) {
	    this.textXPv.setText(xPv);
	    changeSupport.firePropertyChange("xPv", null, textXPv.getText());
	}
    }

    public String getYPv() {
	return textYPv.getText();
    }

    public void setYPv(String yPv) {
	if (yPv != null) {
	    this.textYPv.setText(yPv);
	    changeSupport.firePropertyChange("yPv", null, textYPv.getText());
	}
    }

    public boolean getShowAxis() {
	return this.btnShowAxis.getSelection();
    }

    public void setShowAxis(boolean showAxis) {
	this.btnShowAxis.setSelection(showAxis);
	changeSupport.firePropertyChange("showAxisScroll", null, getShowAxis());
    }
}
