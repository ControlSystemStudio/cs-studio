importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.opibuilder.scriptUtil);



var RED = ColorUtil.RED;
var ORANGE = ColorUtil.getColorFromRGB(255,255,0);
var GREEN = ColorUtil.getColorFromHSB(120.0,1.0,1.0);
var PINK = ColorUtil.PINK;

var severity = PVUtil.getSeverity(pvArray[0]);
var color;

switch(severity){
	case 0:  //OK
		color = GREEN;
		break;
	case 1:  //MAJOR
		color = RED;
		break;
	case 2:  //ORANGE
		color = ORANGE;
		break;
	case -1:  //INVALID
	default:
		color = PINK;
}

widgetController.getWidgetModel().setPropertyValue("foreground_color",color);