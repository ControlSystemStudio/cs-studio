importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);

var value = ValueUtil.getDouble(pvArray[0].getValue());
var x = Math.round(390 + value);
var angle = value;
if (angle < 0)
	angle = 360 + angle;

var widget = widgetController.getWidgetModel();
widget.setPropertyValue("rotation_angle", angle);
// widget.setPropertyValue("x", x);

	