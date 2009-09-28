importPackage(Packages.org.eclipse.swt);
importPackage(Packages.org.eclipse.swt.widgets);
importPackage(Packages.org.eclipse.swt.events);
importPackage(Packages.org.eclipse.swt.layout);
importPackage(Packages.org.eclipse.jface.dialogs);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.data);

if (ValueUtil.getDouble(pvArray[0].getValue()) == 100)
{
	MessageDialog.openWarning(null, "Warning", "The temperature you set is too high!");
}
