package org.csstudio.swt.widgets.figures;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Figure;


public class ImageBoolButtonTest extends AbstractBoolControlFigureTest{

	@Override
	public Figure createTestWidget() {
		ImageBoolButtonFigure boolButton = new ImageBoolButtonFigure();
		boolButton.setRunMode(true);
		boolButton.setOffImagePath(new Path("C:\\Users\\5hz\\Pictures\\reset_switch_off.gif"));
		boolButton.setOnImagePath(new Path("C:\\Users\\5hz\\Pictures\\reset_switch_on.gif"));
		return boolButton;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"offImagePath",
				"onImagePath",
				"stretch"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

		
}
