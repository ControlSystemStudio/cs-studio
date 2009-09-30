importPackage(Packages.org.csstudio.platform.data);

var value = ValueUtil.getDouble(pvArray[0].getValue());
var stop = ValueUtil.getDouble(pvArray[1].getValue());
if(stop != 1){
	widgetController.getWidgetModel().setPropertyValue("x", 283+value);
	widgetController.getWidgetModel().setPropertyValue("no_animation", false);
	
}else
	widgetController.getWidgetModel().setPropertyValue("no_animation", true);
	