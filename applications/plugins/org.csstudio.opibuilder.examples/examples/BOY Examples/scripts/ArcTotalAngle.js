importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var value = PVUtil.getDouble(pvArray[0]);
widgetController.setPropertyValue("total_angle", value);