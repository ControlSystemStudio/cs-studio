import java.beans.PropertyDescriptor;

import org.csstudio.swt.widgets.figures.ProgressBarFigure;
import org.eclipse.draw2d.Figure;


public class ProgressBarTest extends AbstractMarkedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ProgressBarFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"fillColor",
				"fillBackgroundColor",
				"effect3D",
				"horizontal",
				"indicatorMode",
				"origin",
				"originIgnored"
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
			if(pd.getName().equals("logScale") || pd.getName().equals("indicatorMode"))
				return super.generateTestData(pd, (Integer)seed  +1);
		}
				return super.generateTestData(pd, seed);
	}
		
}
