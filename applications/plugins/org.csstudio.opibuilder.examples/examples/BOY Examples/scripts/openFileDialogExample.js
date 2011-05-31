importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var isInWorkspace = display.getWidget("workspaceDialog").getValue();

var filePath = FileUtil.openFileDialog(isInWorkspace.booleanValue());
if(filePath != null)
	display.getWidget("dialogFilePath").setPropertyValue("text", filePath);
	