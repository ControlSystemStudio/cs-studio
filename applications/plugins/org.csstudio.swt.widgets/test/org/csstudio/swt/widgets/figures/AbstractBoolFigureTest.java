package org.csstudio.swt.widgets.figures;


public abstract class AbstractBoolFigureTest extends AbstractWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"bit",
				"booleanValue",
				"offColor",
				"offLabel",
				"onColor",
				"onLabel",
				"value",
				"showBooleanLabel"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
