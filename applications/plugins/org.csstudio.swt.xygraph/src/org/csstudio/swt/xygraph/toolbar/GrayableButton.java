package org.csstudio.swt.xygraph.toolbar;

import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
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
	private static final String GRAY_IMAGE = "GrayableButton.GrayImage";
	
	public GrayableButton(Image image) {
		super(image);
		this.image = image;
		grayImage = XYGraphMediaFactory.getInstance().getRegisteredImage(GRAY_IMAGE + image.toString());
		if( grayImage == null){
			grayImage = new Image(null, image, SWT.IMAGE_GRAY);
			XYGraphMediaFactory.getInstance().registerImage(GRAY_IMAGE + image.toString(), grayImage);
		}
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
