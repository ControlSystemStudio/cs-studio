package org.csstudio.swt.widgets.figures;


public abstract class AbstractLabelWidgetTest extends AbstractWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"text"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
