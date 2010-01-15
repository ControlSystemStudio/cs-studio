importPackage(Packages.org.csstudio.platform.data);

var positionPV = pvArray[1];
var position = ValueUtil.getDouble(pvArray[1].getValue());
var stop = ValueUtil.getDouble(pvArray[2].getValue());
if(stop != 1){
	positionPV.setValue((position+20)%400);
	widgetController.getWidgetModel().setPropertyValue("x", 283+position);
	widgetController.getWidgetModel().setPropertyValue("no_animation", false);	
}else
	widgetController.getWidgetModel().setPropertyValue("no_animation", true);
	