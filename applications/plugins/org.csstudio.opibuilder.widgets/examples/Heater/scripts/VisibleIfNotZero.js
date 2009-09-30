importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.data);

// Set the widget's "visible" property
// based on the input PV to this script being 0 or not 0
var value = ValueUtil.getDouble(pvArray[0].getValue());
widgetController.getWidgetModel().setPropertyValue("visible", value != 0);
	