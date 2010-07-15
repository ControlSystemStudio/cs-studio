package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.csstudio.swt.widgets.figures.GaugeFigure;
import org.eclipse.draw2d.Figure;


public class GaugeTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new GaugeFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D",
				"needleColor"
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
			if(pd.getName().equals("logScale"))
				return super.generateTestData(pd, (Integer)seed  +1);
		}
				return super.generateTestData(pd, seed);
	}
		
}
