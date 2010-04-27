importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var positionPV = pvArray[1];
var position = PVUtil.getDouble(pvArray[1]);
var stop = PVUtil.getDouble(pvArray[2]);
if(stop != 1){
	positionPV.setValue((position+20)%400);
	widgetController.setPropertyValue("x", 283+position);
	widgetController.setPropertyValue("no_animation", false);	
}else
	widgetController.setPropertyValue("no_animation", true);
	