
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);



var RED = CustomMediaFactory.COLOR_RED;
var YELLOW = CustomMediaFactory.COLOR_YELLOW;


var value = ValueUtil.getDouble(pvArray[0].getValue());



if(value==0)
	widgetController.getWidgetModel().setOPIFilePath("DynamicSymbols/1.opi");
else if(value==1)
	widgetController.getWidgetModel().setOPIFilePath("DynamicSymbols/2.opi");
else if(value==2)
	widgetController.getWidgetModel().setOPIFilePath("DynamicSymbols/3.opi");	
else if(value==3)
	widgetController.getWidgetModel().setOPIFilePath("DynamicSymbols/4.opi");	