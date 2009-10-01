importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);


var value = ValueUtil.getDouble(pvArray[0].getValue());
widgetController.getWidgetModel().setFillLevel(value);