importPackage(Packages.org.eclipse.jface.dialogs);
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.java.lang);

var flagName = "popped";

if(widgetController.getExternalObject(flagName) == null){
	widgetController.setExternalObject(flagName, false);	
}

var b = widgetController.getExternalObject(flagName);

if(ValueUtil.getDouble(pvArray[0].getValue()) > 80){		
		if( b == false){
			MessageDialog.openWarning(
				null, "Warning", "The temperature you set is too high!");
			widgetController.setExternalObject(flagName, true);
		}
		
}else if (b == true){
	widgetController.setExternalObject(flagName, false);
}
	

