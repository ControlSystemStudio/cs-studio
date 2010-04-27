importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var RED = ColorFontUtil.RED;
var YELLOW = ColorFontUtil.YELLOW;

var value = PVUtil.getDouble(pvArray[0]);

if(value==0)
	widgetController.setPropertyValue("opi_file", "DynamicSymbols/1.opi");
else if(value==1)
	widgetController.setPropertyValue("opi_file", "DynamicSymbols/2.opi");
else if(value==2)
	widgetController.setPropertyValue("opi_file", "DynamicSymbols/3.opi");
else if(value==3)
	widgetController.setPropertyValue("opi_file", "DynamicSymbols/4.opi");
