importPackage(Packages.java.lang);
importPackage(Packages.java.lang.reflect);
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.org.csstudio.opibuilder.datadefinition);

var value = ValueUtil.getString(pvArray[0].getValue());
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
	
widgetController.getChild("WaveGraph").setPropertyValue(
		"color_map", colorMap);
	

