importPackage(Packages.org.eclipse.jface.dialogs);
importPackage(Packages.org.csstudio.platform.data);

if(ValueUtil.getDouble(pvArray[0].getValue()) == 100){
		var op = MessageDialog.openWarning(
			null, "Warning", "You reach the value of 100!");
}
	

