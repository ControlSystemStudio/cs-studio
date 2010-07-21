package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class ChoiceButtonTest extends AbstractChoiceFigureTest{

	@Override
	public Figure createTestWidget() {
		return new ChoiceButtonFigure();
	}
	
	
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
