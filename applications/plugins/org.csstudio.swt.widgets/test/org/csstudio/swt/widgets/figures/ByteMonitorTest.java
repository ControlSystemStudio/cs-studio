package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class ByteMonitorTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ByteMonitorFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"numBits",
				"offColor",
				"onColor",
				"startBit",
				"value",
				"effect3D",
				"horizontal",
				"reverseBits",
				"squareLED"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
