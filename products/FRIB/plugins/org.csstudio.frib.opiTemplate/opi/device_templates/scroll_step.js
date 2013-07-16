importPackage(Packages.org.csstudio.opibuilder.scriptUtil);
var step = display.getWidget("Step_Input").getPropertyValue("text");

var scroll = display.getWidget("Scrollbar_1");
scroll.setPropertyValue("step_increment", step);