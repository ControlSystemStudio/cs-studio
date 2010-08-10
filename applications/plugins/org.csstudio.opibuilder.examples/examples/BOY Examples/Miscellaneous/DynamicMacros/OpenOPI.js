importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var flagName = "firstRun";
//Avoid running this script if the script is triggered during opi startup.
if(widgetController.getExternalObject(flagName) == null){
	widgetController.setExternalObject(flagName, false);	
}else{

	var macroInput = DataUtil.createMacrosInput(true);
	macroInput.put("pv", PVUtil.getString(pvArray[0]));		
	ScriptUtil.openOPI(widgetController, "embeddedOPI.opi", false, macroInput);

}