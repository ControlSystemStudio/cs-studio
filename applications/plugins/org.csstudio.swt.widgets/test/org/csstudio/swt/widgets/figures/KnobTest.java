package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.csstudio.swt.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.KnobFigure;
import org.eclipse.draw2d.Figure;


public class KnobTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		KnobFigure knob = new KnobFigure();
		knob.addManualValueChangeListener(new IManualValueChangeListener() {
			
			public void manualValueChanged(double newValue) {
				System.out.println("Knob Dragged: " + newValue);
			}
		});
		return knob;
	}

	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {	
		if(seed !=null && seed instanceof Integer){
			if(pd.getName().equals("increment"))
				return (Integer)seed%10;
			if(pd.getName().equals("logScale"))
				return super.generateTestData(pd, (Integer)seed  +1);
		}
				return super.generateTestData(pd, seed);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D",
				"thumbColor",
				"increment"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
}
