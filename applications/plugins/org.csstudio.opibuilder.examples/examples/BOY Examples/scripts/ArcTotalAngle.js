importPackage(Packages.org.csstudio.platform.data);

var value = ValueUtil.getDouble(pvArray[0].getValue());
widgetController.getWidgetModel().setPropertyValue("total_angle", value);