importPackage(Packages.org.csstudio.platform.data);

var src = ValueUtil.getString(pvArray[0].getValue());
if(src == "Open OPI")
	widgetController.executeAction(0);
else if(src == "Play Sound")
	widgetController.executeAction(1);
	