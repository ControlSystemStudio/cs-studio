package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class SpinnerTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new SpinnerFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"formatType",
				"max",
				"min",
				"pageIncrement",
				"precision",
				"stepIncrement",
				"value"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
