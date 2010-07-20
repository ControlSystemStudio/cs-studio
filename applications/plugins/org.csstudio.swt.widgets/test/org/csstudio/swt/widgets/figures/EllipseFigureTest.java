package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


/**
 * @author Xihui Chen
 *
 */
public class EllipseFigureTest extends AbstractShapeWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new EllipseFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"antiAlias",
				"horizontalFill",
				"lineColor",
				"fill",
				"transparent"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
