/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.BeanComposite;
import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.widgets.StartEndRangeWidget;
import org.eclipse.swt.widgets.Composite;
import org.epics.pvmanager.graphene.GraphDataRange;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractGraph2DWidget extends BeanComposite implements ConfigurableWidget {

    private boolean showAxis = true;
    private String pvName;
    private String xPvName;

    public AbstractGraph2DWidget(Composite parent, int style) {
	super(parent, style);
    }

    abstract void reconnect();

    /**
     * A helper function to set all the appropriate
     * 
     * @param control
     */
    public void setRange(StartEndRangeWidget control,
	    GraphDataRange plotDataRange) {
	control.setRange(plotDataRange.getIntegratedRange().getMinimum()
		.doubleValue(), plotDataRange.getIntegratedRange().getMaximum()
		.doubleValue());
    }

    public void resetRange(StartEndRangeWidget control) {
	control.setRanges(0, 0, 1, 1);
    }

    public String getXpvName() {
	return xPvName;
    }

    public void setXPvName(String xPvName) {
	String oldValue = this.xPvName;
	this.xPvName = xPvName;
	changeSupport.firePropertyChange("xProcessVariable", oldValue,
		this.xPvName);
    }

    public String getPvName() {
	return this.pvName;
    }

    public void setPvName(String pvName) {
	String oldValue = this.pvName;
	this.pvName = pvName;
	changeSupport.firePropertyChange("processVariable", oldValue,
		this.pvName);
    }
    
    public boolean isShowAxis() {
	return showAxis;
    }

    public void setShowAxis(boolean showAxis) {
	boolean oldValue = this.showAxis;
	this.showAxis = showAxis;
	changeSupport.firePropertyChange("showAxis", oldValue, this.showAxis);
    }

	private String dataFormula;
    public String getDataFormula() {
    	return this.dataFormula;
    }

    public void setDataFormula(String dataFormula) {
		String oldValue = this.dataFormula;
		this.dataFormula = dataFormula;
		changeSupport.firePropertyChange("dataFormula", oldValue,
			this.dataFormula);
    }

}
