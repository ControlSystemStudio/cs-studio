importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);

var RED = CustomMediaFactory.COLOR_RED;
var GREEN = CustomMediaFactory.COLOR_GREEN;
var value = ValueUtil.getDouble(pvArray[0].getValue());
var width = 5*value;
var oldY = widgetController.getWidgetModel().getLocation().y;
var oldHeight = widgetController.getWidgetModel().getSize().height;

widgetController.getWidgetModel().setLocation(value*20,  550 - width/2);
widgetController.getWidgetModel().setSize(width,width);	
		