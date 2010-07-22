package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class ThumbWheelTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ThumbWheelFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"decimalDigits",
				"integerDigits",
				"internalBorderColor",
				"internalBorderThickness",
				"wheelFont",
				"test"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
			
}
