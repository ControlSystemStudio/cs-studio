importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);



var RED = CustomMediaFactory.COLOR_RED;
var YELLOW = CustomMediaFactory.COLOR_YELLOW;


var value = ValueUtil.getDouble(pvArray[0].getValue());



if(value==0)
	widgetController.getWidgetModel().setOPIFilePath("/OPIBuilder/test2.opi");
else if(value==1)
	widgetController.getWidgetModel().setOPIFilePath("/OPIBuilder/testScript.opi");
else if(value==2)
	widgetController.getWidgetModel().setOPIFilePath("/OPIBuilder/widgets.opi");	
else if(value==3)
	widgetController.getWidgetModel().setOPIFilePath("/OPIBuilder/performanceTest.opi");	