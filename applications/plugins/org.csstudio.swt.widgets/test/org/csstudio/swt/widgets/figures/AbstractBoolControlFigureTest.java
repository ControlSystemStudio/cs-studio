package org.csstudio.swt.widgets.figures;


public abstract class AbstractBoolControlFigureTest extends AbstractBoolFigureTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"confirmTip",
				"password",
				"runMode",
				"showConfirmDialog",
				"toggle"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
