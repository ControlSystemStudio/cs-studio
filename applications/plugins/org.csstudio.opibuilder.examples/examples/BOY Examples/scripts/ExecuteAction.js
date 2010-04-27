importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var src = PVUtil.getString(pvArray[0]);
if(src == "Open OPI")
	widgetController.executeAction(0);
else if(src == "Play Sound")
	widgetController.executeAction(1);
	