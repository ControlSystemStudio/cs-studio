import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.widgets.figures.MeterFigure;
import org.eclipse.draw2d.Figure;


public class MeterTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new MeterFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		List<String> superPropList = new ArrayList<String>();
		for(String p : superProps){
			if(!p.equals("transparent"))
				superPropList.add(p);
		}
		String[] myProps = new String[]{
				"needleColor"
		};
		
		return concatenateStringArrays(superPropList.toArray(new String[]{}), myProps);
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
