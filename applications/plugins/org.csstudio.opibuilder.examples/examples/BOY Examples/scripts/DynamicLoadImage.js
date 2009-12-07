
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.eclipse.core.runtime);



var value = ValueUtil.getDouble(pvArray[0].getValue());



if(value==0)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("DynamicSymbols/Haha.jpg"));
else if(value==1)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("DynamicSymbols/Scared.jpg"));
else if(value==2)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("DynamicSymbols/Shy.jpg"));
else if(value==3)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("DynamicSymbols/Thinking.jpg"));
