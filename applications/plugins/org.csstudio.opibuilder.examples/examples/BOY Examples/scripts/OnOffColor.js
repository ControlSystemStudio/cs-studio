importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var RED = ColorFontUtil.RED;
var GREEN = ColorFontUtil.GREEN;

var value = PVUtil.getDouble(pvArray[0]);
widgetController.setPropertyValue("background_color", value > 0? GREEN : RED);