importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var RED = ColorFontUtil.RED;
var ORANGE = ColorFontUtil.getColorFromRGB(255,255,0);
var GREEN = ColorFontUtil.getColorFromHSB(120.0,1.0,1.0);
var PINK = ColorFontUtil.PINK;

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