package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class BoolSwitchTest extends AbstractBoolControlFigureTest{

	@Override
	public Figure createTestWidget() {
		BoolSwitchFigure boolButton = new BoolSwitchFigure();
		boolButton.setRunMode(true);
		return boolButton;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
