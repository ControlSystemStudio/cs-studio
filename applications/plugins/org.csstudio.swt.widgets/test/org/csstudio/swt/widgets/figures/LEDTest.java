package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class LEDTest extends AbstractBoolFigureTest{

	@Override
	public Figure createTestWidget() {
		return new LEDFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D",
				"squareLED"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
