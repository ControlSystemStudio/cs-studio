importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);

var pid_out = ValueUtil.getDouble(pvArray[0].getValue());

var text;
var color;
if (pid_out > 100)
{
    text = "Heating up ...";
    color = CustomMediaFactory.COLOR_RED;
}
else if (pid_out > 0)
{
    text = "Keeping warm ...";
    color = CustomMediaFactory.COLOR_YELLOW;
}
else if (pid_out < 0)
{
    text = "Cooling down ...";
    color = CustomMediaFactory.COLOR_LIGHT_BLUE;
}
else
{
    text = "Temperature is just right";
    color = CustomMediaFactory.COLOR_GREEN;
}

widgetController.getWidgetModel().setPropertyValue("text", text);
widgetController.getWidgetModel().setPropertyValue("background_color", color);
