package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class GaugeTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new GaugeFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D",
				"needleColor"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
