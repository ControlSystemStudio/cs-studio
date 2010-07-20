package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class ArcTest extends AbstractShapeWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ArcFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"antiAlias",
				"startAngle",
				"totalAngle",
				"fill"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
