importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);



var LEFT = CustomMediaFactory.COLOR_RED;
var RIGHT = CustomMediaFactory.COLOR_BLUE;
var DRAW = CustomMediaFactory.COLOR_GREEN;

var value0 = ValueUtil.getDouble(pvArray[0].getValue());
var value1 = ValueUtil.getDouble(pvArray[1].getValue());
widgetController.getWidgetModel().setPropertyValue("text",
	value0 == value1 ? "Draw!" : value0 > value1 ? "Left Win!" : "Right Win!");
widgetController.getWidgetModel().setPropertyValue("foreground_color",
	value0 == value1 ? DRAW: value0 > value1 ? LEFT : RIGHT);