importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);



var RED = CustomMediaFactory.COLOR_RED;
var GREEN = CustomMediaFactory.COLOR_GREEN;


var value = ValueUtil.getDouble(pvArray[0].getValue());
widgetController.getWidgetModel().setBackgroundColor(value > 0? GREEN : RED);