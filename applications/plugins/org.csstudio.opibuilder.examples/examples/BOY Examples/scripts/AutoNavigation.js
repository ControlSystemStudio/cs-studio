importPackage(Packages.org.csstudio.opibuilder.scriptUtil);

var value = PVUtil.getLong(pvArray[0]);

var mainContainer = widgetController.getChild("Main Container");

var opiArray=["1_1_Rectangle_Ellipse.opi","1_5_Polyline_Polygon.opi","2_3_Gauge_Meter.opi", "2_5_5_XY_Graph.opi"];

//recover border style for all other widgets 
for(var i=0; i<4; i++){	
	if(i != value)
		widgetController.getChild("Sub Container " + i).setPropertyValue("border_style", 2);
}

//highlight this container by setting its border to line style
widgetController.getChild("Sub Container " + value).setPropertyValue("border_style", 1);

//load OPI to main container
mainContainer.setPropertyValue("opi_file", opiArray[value]);
