package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class RadioBoxTest extends AbstractChoiceFigureTest{

	@Override
	public Figure createTestWidget() {
		return new RadioBoxFigure();
	}
	
	
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
