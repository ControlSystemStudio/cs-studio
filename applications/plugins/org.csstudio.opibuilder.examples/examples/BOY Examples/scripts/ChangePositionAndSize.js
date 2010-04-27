importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var RED = ColorFontUtil.RED;
var GREEN = ColorFontUtil.GREEN;

var value = PVUtil.getDouble(pvArray[0]);


var width = 5*value;
var oldY = widgetController.getPropertyValue("y");
var oldHeight = widgetController.getPropertyValue("height");

widgetController.setPropertyValue("x", value*20);
widgetController.setPropertyValue("y",  500 - width/2);
widgetController.setPropertyValue("width",width);
widgetController.setPropertyValue("height",width);	
		