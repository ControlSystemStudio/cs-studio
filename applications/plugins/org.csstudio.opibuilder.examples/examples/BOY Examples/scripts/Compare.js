importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

execute();

function execute(){
if(pvArray[0].getValue() == null || 
	pvArray[1].getValue() == null)
	return;
	
var LEFT = ColorFontUtil.RED;
var RIGHT = ColorFontUtil.BLUE;
var DRAW = ColorFontUtil.GREEN;

	var value0 = PVUtil.getDouble(pvArray[0]);
	var value1 = PVUtil.getDouble(pvArray[1]);
	widgetController.setPropertyValue("text",
		value0 == value1 ? "Draw!" : value0 > value1 ? "Left Win!" : "Right Win!");
	widgetController.setPropertyValue("foreground_color",
		value0 == value1 ? DRAW: value0 > value1 ? LEFT : RIGHT);

}