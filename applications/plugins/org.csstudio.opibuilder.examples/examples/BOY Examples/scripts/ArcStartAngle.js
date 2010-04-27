importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var value = PVUtil.getDouble(pvArray[0]);
widgetController.setPropertyValue("start_angle", value);