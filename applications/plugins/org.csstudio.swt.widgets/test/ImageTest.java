import java.beans.PropertyDescriptor;

import org.csstudio.swt.widgets.figures.ImageFigure;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Figure;


public class ImageTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		ImageFigure imageFigure = new ImageFigure();
		imageFigure.setFilePath(new Path("C:\\Users\\5hz\\Pictures\\DancingPeaks.gif"));
		return imageFigure;
	}
	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {
		
		if(seed != null && seed instanceof Integer){
			if(pd.getName().equals("animationDisabled"))
				return super.generateTestData(pd, ((Integer)seed+1));
		}
		return super.generateTestData(pd, seed);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"filePath",
				"topCrop",
				"bottomCrop",
				"leftCrop",
				"rightCrop",
				"stretch",
				"animationDisabled"
				
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
