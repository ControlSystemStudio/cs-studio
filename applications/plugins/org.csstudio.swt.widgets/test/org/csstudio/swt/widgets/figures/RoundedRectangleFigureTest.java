package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


/**
 * @author Xihui Chen
 *
 */
public class RoundedRectangleFigureTest extends OPIRectangleFigureTest{

	@Override
	public Figure createTestWidget() {
		return new RoundedRectangleFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"cornerWidth",
				"cornerHeight"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
