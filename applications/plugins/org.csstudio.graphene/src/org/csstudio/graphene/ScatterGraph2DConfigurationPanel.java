/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.AbstractConfigurationPanel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author shroffk
 *
 */
public class ScatterGraph2DConfigurationPanel extends AbstractConfigurationPanel {
	private Text textYPV;
	private Text textXPV;
	private Combo combo;
	private Button btnShowAxis;

    public ScatterGraph2DConfigurationPanel(Composite parent, int style) {
	super(parent, style);
	setLayout(new GridLayout(2, false));
	
	Label lblYPV = new Label(this, SWT.NONE);
	lblYPV.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblYPV.setText("Y PV:");
	
	textYPV = new Text(this, SWT.BORDER);
	textYPV.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	textYPV.addListener(SWT.DefaultSelection, new Listener() {

	    @Override
	    public void handleEvent(Event event) {
		changeSupport.firePropertyChange("yPv", null, textYPV.getText());

	    }
	});
	
	
	Label lblXPV = new Label(this, SWT.NONE);
	lblXPV.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblXPV.setText("X PV:");
	
	textXPV = new Text(this, SWT.BORDER);
	textXPV.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	textXPV.addListener(SWT.DefaultSelection, new Listener() {

	    @Override
	    public void handleEvent(Event event) {
		changeSupport.firePropertyChange("xPv", null, textXPV.getText());
	    }
	});
	
	Label lblInterpolation = new Label(this, SWT.NONE);
	lblInterpolation.setText("Interpolation Scheme:");
	
	combo = new Combo(this, SWT.NONE);
	combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	new Label(this, SWT.NONE);
	
	btnShowAxis = new Button(this, SWT.CHECK);
	btnShowAxis.setText("Show Axis Scroll");
	btnShowAxis.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		changeSupport.firePropertyChange("showAxisScroll", null,
			getShowAxis());
	    }
	});
    }
    
    public String getXPv() {
	return textXPV.getText();
    }

    public void setXPv(String xPv) {
	if (xPv != null) {
	    this.textXPV.setText(xPv);
	    changeSupport.firePropertyChange("xPv", null, textXPV.getText());
	}
    }

    public String getYPv() {
	return textYPV.getText();
    }

    public void setYPv(String yPv) {
	if (yPv != null) {
	    this.textYPV.setText(yPv);
	    changeSupport.firePropertyChange("yPv", null, textYPV.getText());
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
