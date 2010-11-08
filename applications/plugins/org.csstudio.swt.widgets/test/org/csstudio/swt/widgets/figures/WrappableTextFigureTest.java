package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class WrappableTextFigureTest extends TextFigureTest{

	@Override
	public Figure createTestWidget() {
		return new WrappableTextFigure();
	}	
	

	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
