package org.csstudio.swt.widgets.figures;


public abstract class AbstractRoundRampedWidgetTest extends AbstractMarkedWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"gradient"				
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
