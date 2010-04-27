importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var RED = ColorFontUtil.RED;
var YELLOW = ColorFontUtil.YELLOW;

var value = PVUtil.getDouble(pvArray[0]);

widgetController.setPropertyValue("background_color", value > 0? RED : YELLOW);