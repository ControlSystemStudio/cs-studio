importPackage(Packages.org.eclipse.jface.dialogs);
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.eclipse.swt.widgets);
importPackage(Packages.org.eclipse.ui);
if(ValueUtil.getDouble(pvArray[0].getValue()) == 100){
//	var r = new Runnable(){
//		public void run(){
		var op = MessageDialog.openWarning(
			null, "Warning", "The temperature you set is too high!");
//		};
//	PlatformUI.getWorkbench().getDisplay().asyncExec(r);
}
	

