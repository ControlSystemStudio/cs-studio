package org.csstudio.swt.widgets.figures;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileSource;
import org.csstudio.swt.widgets.figures.TextInputFigure.SelectorType;
import org.eclipse.draw2d.Figure;


/**
 * @author Xihui Chen
 *
 */
public class TextInputFigureTest extends TextFigureTest{

	@Override
	public Figure createTestWidget() {
		TextInputFigure figure = new TextInputFigure();
		figure.setSelectorType(SelectorType.DATETIME);
		figure.setFileSource(FileSource.LOCAL);
		figure.setFileReturnPart(FileReturnPart.NAME_ONLY);
		return figure;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"dateTimeFormat",
				"selectorType",
				"fileSource",
				"fileReturnPart",
				"startPath"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
}
