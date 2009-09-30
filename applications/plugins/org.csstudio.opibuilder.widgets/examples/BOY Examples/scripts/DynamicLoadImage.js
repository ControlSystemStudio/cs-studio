
importPackage(Packages.org.csstudio.platform.data);
importPackage(Packages.org.eclipse.core.runtime);



var value = ValueUtil.getDouble(pvArray[0].getValue());



if(value==0)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("/BOY Examples/widgets/DynamicSymbols/Haha.jpg"));
else if(value==1)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("/BOY Examples/widgets/DynamicSymbols/Scared.jpg"));
else if(value==2)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("/BOY Examples/widgets/DynamicSymbols/Shy.jpg"));
else if(value==3)
	widgetController.getWidgetModel().setPropertyValue("image_file", new Path("/BOY Examples/widgets/DynamicSymbols/Thinking.jpg"));
