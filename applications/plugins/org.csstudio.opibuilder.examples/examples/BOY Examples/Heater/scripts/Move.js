importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);

// Simple animation, meant to be used with an input PV
// like sim://sine(-10,10,10,0.1)
// that runs -10...10 in 10 steps, updating every 0.1 seconds
var sine = ValueUtil.getDouble(pvArray[0].getValue());

// Update widget position
var x = 440 + sine;
var y = 390 + 0.2*sine;
var widget = widgetController.getWidgetModel();
widget.setPropertyValue("x", x);
widget.setPropertyValue("y", y);

// Update widget color
if (sine > 0.0)
	widget.setPropertyValue("foreground_color", CustomMediaFactory.COLOR_CYAN);
else
	widget.setPropertyValue("foreground_color", CustomMediaFactory.COLOR_BLUE);
	