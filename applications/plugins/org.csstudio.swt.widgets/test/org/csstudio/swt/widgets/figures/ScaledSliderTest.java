package org.csstudio.swt.widgets.figures;
import org.csstudio.swt.datadefinition.IManualValueChangeListener;
import org.eclipse.draw2d.Figure;


public class ScaledSliderTest extends AbstractMarkedWidgetTest{

	@Override
	public Figure createTestWidget() {
		ScaledSliderFigure slider = new ScaledSliderFigure();
		slider.addManualValueChangeListener(new IManualValueChangeListener() {
			
			public void manualValueChanged(double newValue) {
				System.out.println("slider Dragged: " + newValue);
			}
		});
		return slider;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"fillColor",
				"fillBackgroundColor",
				"effect3D",
				"horizontal",
				"thumbColor",
				"stepIncrement",
				"pageIncrement"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

	
		
}
