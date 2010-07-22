package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class GroupingContainerTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new GroupingContainerFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"showScrollBar"
				
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
			
}
