importPackage(Packages.org.csstudio.opibuilder.scriptUtil);
importPackage(Packages.org.csstudio.opibuilder.datadefinition);

var value = PVUtil.getString(pvArray[0]);
var colorMap;
if(value =="Cool")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.Cool, true, true);
else if(value == "Hot")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.Hot, true, true);
else if(value == "JET")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.JET, true, true);
else if(value == "GrayScale")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.GrayScale, true, true);
else if(value == "ColorSpectrum")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.ColorSpectrum, true, true);
else if(value == "Shaded")
	colorMap = new ColorMap(ColorMap.PredefinedColorMap.Shaded, true, true);	
//set colormap of IntensityGraph	
widgetController.getChild("IntensityGraph").setPropertyValue(
		"color_map", colorMap);
	

