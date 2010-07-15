package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.csstudio.swt.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.ScaledSliderFigure;
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

	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {	
		if(seed !=null && seed instanceof Integer){			
			if(pd.getName().equals("logScale") )
				return super.generateTestData(pd, (Integer)seed  +1);
		}
				return super.generateTestData(pd, seed);
	}
		
}
