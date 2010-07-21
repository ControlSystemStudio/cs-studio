package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class CheckBoxTest extends AbstractLabelWidgetTest{

	@Override
	public Figure createTestWidget() {
		CheckBoxFigure boolButton = new CheckBoxFigure();
		boolButton.setRunMode(true);
		return boolButton;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"runMode",
				"value",
				"bit",
				"boolValue"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
