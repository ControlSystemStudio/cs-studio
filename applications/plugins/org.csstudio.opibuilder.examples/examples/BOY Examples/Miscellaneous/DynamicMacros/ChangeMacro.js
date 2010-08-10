importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var macroInput = DataUtil.createMacrosInput(true);
macroInput.put("pv", PVUtil.getString(pvArray[0]));
widgetController.setPropertyValue("macros", macroInput);
widgetController.setPropertyValue("opi_file", 
	widgetController.getPropertyValue("opi_file"), true);

