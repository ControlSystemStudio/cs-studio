importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);

var sine = ValueUtil.getDouble(pvArray[0].getValue());

var x = 440 + sine;
var y = 390 + sine;
widgetController.getWidgetModel().setPropertyValue("x", x);
// widgetController.getWidgetModel().setPropertyValue("y", y);

if (sine > 0.0)
	widgetController.getWidgetModel().setPropertyValue("foreground_color", CustomMediaFactory.COLOR_RED);
else
	widgetController.getWidgetModel().setPropertyValue("foreground_color", CustomMediaFactory.COLOR_BLUE);
	