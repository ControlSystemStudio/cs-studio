package org.csstudio.swt.widgets.figures;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;


public class ActionButtonTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ActionButtonFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		List<String> superPropList = new ArrayList<String>();
		for(String p : superProps){
			if(!p.equals("opaque"))
				superPropList.add(p);
		}
		String[] myProps = new String[]{
				"toggleStyle",
				"textAlignment",
				"imagePath",
				"text",
				"runMode"
		};
		
		return concatenateStringArrays(superPropList.toArray(new String[]{}), myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}
	
		
}
