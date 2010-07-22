package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class TabTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new TabFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"activeTabIndex"				
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
			
}
