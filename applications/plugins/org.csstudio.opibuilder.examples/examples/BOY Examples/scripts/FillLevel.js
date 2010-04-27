importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var value = PVUtil.getDouble(pvArray[0]);
widgetController.setPropertyValue("fill_level",value);