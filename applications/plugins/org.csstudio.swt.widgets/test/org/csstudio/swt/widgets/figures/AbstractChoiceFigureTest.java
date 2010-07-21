package org.csstudio.swt.widgets.figures;

import java.beans.PropertyDescriptor;
import java.util.Arrays;


/**
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceFigureTest extends AbstractWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"selectedColor",
				"state",
				"states",
				"horizontal"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {
		if(pd.getName().equals("states"))
			return Arrays.asList("choice 1", "choice 2", "Chioce 3");
		return super.generateTestData(pd, seed);
	}
	
}
