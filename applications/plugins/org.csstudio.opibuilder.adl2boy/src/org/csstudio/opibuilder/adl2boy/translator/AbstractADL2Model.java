package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.eclipse.swt.graphics.RGB;

public abstract class AbstractADL2Model {
	AbstractWidgetModel widgetModel;
	RGB colorMap[] = new RGB[0];
	
	public AbstractADL2Model(final ADLWidget adlWidget, RGB colorMap[], AbstractContainerModel parentWidget) {
		this.colorMap = colorMap;
	}

	/**
	 * 
	 * @return
	 */
	abstract public AbstractWidgetModel getWidgetModel() ;

	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	protected void setADLObjectProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLObject()){
			ADLObject adlObj=adlWidget.getAdlObject();
			widgetModel.setX(adlObj.getX());
			widgetModel.setY(adlObj.getY());
			widgetModel.setHeight(adlObj.getHeight());
			widgetModel.setWidth(adlObj.getWidth());
		}
		
	}

	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	protected void setADLBasicAttributeProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel, boolean colorForeground){
		if (adlWidget.hasADLBasicAttribute()){
			ADLBasicAttribute basAttr = adlWidget.getAdlBasicAttribute();
			System.out.println("Trying to load color " + basAttr.getClr() );
			if (basAttr.isColorDefined()) {
				if (colorForeground) {
					widgetModel.setForegroundColor(colorMap[basAttr.getClr()]);
				}
				else {
					widgetModel.setBackgroundColor(colorMap[basAttr.getClr()]);
				}
			}
			else {
				if (colorForeground) {
					widgetModel.setForegroundColor(widgetModel.getParent().getForegroundColor());
				}
				else {
					widgetModel.setBackgroundColor(widgetModel.getParent().getBackgroundColor());
				}
				
			}
		}
	}
	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	protected void setADLControlProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLControl()){
			ADLControl control = adlWidget.getAdlControl();
			if (control.isForeColorDefined() ){
				widgetModel.setForegroundColor(colorMap[control.getForegroundColor()]);
			}
			else { 
				widgetModel.setForegroundColor(widgetModel.getParent().getForegroundColor());
			}
			if (control.isBackColorDefined() ){
				widgetModel.setBackgroundColor(colorMap[control.getBackgroundColor()]);
			}
			else { 
				widgetModel.setBackgroundColor(widgetModel.getParent().getBackgroundColor());
			}
			
			String channel = control.getChan();
			if ((channel != null) && (!(channel.equals(""))) ){
				widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, channel);
			}
		}
	}
	/** set the properties contained in the ADL basic properties section in the 
	 * created widgetModel
	 * @param adlWidget
	 * @param widgetModel
	 */
	protected void setADLMonitorProps(ADLAbstractWidget adlWidget, AbstractWidgetModel widgetModel){
		if (adlWidget.hasADLMonitor()){
			ADLMonitor monitor = adlWidget.getAdlMonitor();
			if (monitor.isForeColorDefined() ){
				widgetModel.setForegroundColor(colorMap[monitor.getForegroundColor()]);
			}
			else { 
				widgetModel.setForegroundColor(widgetModel.getParent().getForegroundColor());
			}
			if (monitor.isBackColorDefined() ){
				widgetModel.setBackgroundColor(colorMap[monitor.getBackgroundColor()]);
			}
			else { 
				widgetModel.setBackgroundColor(widgetModel.getParent().getBackgroundColor());
			}
			String channel = monitor.getChan();
			if ((channel != null) && (!(channel.equals(""))) ){
				widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, channel);
			}
		}
	}
}
