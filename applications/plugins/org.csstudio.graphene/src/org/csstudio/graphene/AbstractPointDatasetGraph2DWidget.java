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
public abstract class AbstractPointDatasetGraph2DWidget extends BeanComposite
		implements ConfigurableWidget {

    private boolean showAxis = true;

    public AbstractPointDatasetGraph2DWidget(Composite parent, int style) {
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
	System.out.println(control.getMin() + " - " + control.getMax());
	System.out
		.println("Setting"
			+ plotDataRange.getIntegratedRange().getMinimum()
				.doubleValue()
			+ " "
			+ plotDataRange.getIntegratedRange().getMaximum()
				.doubleValue());
	control.setRange(plotDataRange.getIntegratedRange().getMinimum()
		.doubleValue(), plotDataRange.getIntegratedRange().getMaximum()
		.doubleValue());
    }

    public void resetRange(StartEndRangeWidget control) {
	control.setRanges(0, 0, 1, 1);
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
    private String xColumnFormula;
    private String yColumnFormula;
    private String tooltipFormula;

    public String getDataFormula() {
	return this.dataFormula;
    }

    public void setDataFormula(String dataFormula) {
	String oldValue = this.dataFormula;
	this.dataFormula = dataFormula;
	changeSupport.firePropertyChange("dataFormula", oldValue,
		this.dataFormula);
    }

    public String getXColumnFormula() {
	return this.xColumnFormula;
    }

    public void setXColumnFormula(String xColumnFormula) {
	String oldValue = this.xColumnFormula;
	this.xColumnFormula = xColumnFormula;
	changeSupport.firePropertyChange("xColumnFormula", oldValue,
		this.xColumnFormula);
    }

    public String getYColumnFormula() {
	return this.yColumnFormula;
    }

    public void setYColumnFormula(String yColumnFormula) {
	String oldValue = this.yColumnFormula;
	this.yColumnFormula = yColumnFormula;
	changeSupport.firePropertyChange("yColumnFormula", oldValue,
		this.yColumnFormula);
    }

    public String getTooltipFormula() {
	return this.tooltipFormula;
    }

    public void setTooltipFormula(String tooltipFormula) {
	String oldValue = this.tooltipFormula;
	this.tooltipFormula = tooltipFormula;
	changeSupport.firePropertyChange("tooltipFormula", oldValue,
		this.tooltipFormula);
    }

}
