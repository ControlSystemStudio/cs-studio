package org.csstudio.swt.xygraph.toolbar;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**A button with gray image when disabled.
 * @author Xihui Chen
 *
 */
public class GrayableButton extends Button {
	
	Image image;
	Image grayImage;
	
	public GrayableButton(Image image) {
		super(image);
		this.image = image;
		grayImage = new Image(null, image, SWT.IMAGE_GRAY);
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if(value)			
			setContents(new ImageFigure(image));
		else
			setContents(new ImageFigure(grayImage));
	}	
	
	
}
