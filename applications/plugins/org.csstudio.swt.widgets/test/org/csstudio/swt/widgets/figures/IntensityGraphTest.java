package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.csstudio.swt.widgets.datadefinition.ColorMap;
import org.csstudio.swt.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.draw2d.Figure;


public class IntensityGraphTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new IntensityGraphFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"colorMap",
				"cropBottom",
				"cropLeft",
				"cropRight",
				"cropTop",
				"dataArray",
				"dataHeight",
				"dataWidth",
				"max",
				"min",
				"runMode",
				"showRamp"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {
		if(pd.getName().equals("dataArray") && seed != null && seed instanceof Integer){
			double[] simuData = new double[65536];
			for(int i=0; i<256; i++){
				for(int j=0; j<256; j++){
					int x = j-128;
					int y = i-128;		
					int p = (int) Math.sqrt(x*x + y*y);
					simuData[i*256 + j] = Math.sin(p*2*Math.PI/256 + (Integer)seed);		
				}
			}
			return simuData;
		}else if(pd.getName().equals("dataWidth") || pd.getName().equals("dataHeight"))
			return 256;
		else if(pd.getName().equals("max"))
			return 1;
		else if(pd.getName().equals("min"))
			return -1;
		else if(pd.getName().equals("colorMap") && seed != null && seed instanceof Integer)
			return new ColorMap(PredefinedColorMap.values()[(Integer)seed % 6 + 1], true, true);
		return super.generateTestData(pd, seed);
	}
			
}
