importPackage(Packages.org.eclipse.swt.graphics);
importPackage(Packages.java.lang);
importPackage(Packages.org.csstudio.platform.ui.util);
importPackage(Packages.org.csstudio.platform.data);



var RED = CustomMediaFactory.COLOR_RED;
var ORANGE = CustomMediaFactory.COLOR_ORANGE;
var GREEN = CustomMediaFactory.COLOR_GREEN;

var severity = pvArray[0].getValue().getSeverity();
widgetController.getWidgetModel().setBackgroundColor(
	severity.isOK() ? GREEN: severity.isMajor()? RED : ORANGE);