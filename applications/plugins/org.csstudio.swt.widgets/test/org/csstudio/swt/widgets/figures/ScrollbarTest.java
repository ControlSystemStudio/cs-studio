package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class ScrollbarTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ScrollbarFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"extent",
				"maximum",
				"minimum",
				"pageIncrement",
				"stepIncrement",
				"value",
				"horizontal",
				"showValueTip"
				
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
			
}
