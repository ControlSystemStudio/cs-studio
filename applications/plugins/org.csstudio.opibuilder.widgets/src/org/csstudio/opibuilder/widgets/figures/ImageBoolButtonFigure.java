package org.csstudio.opibuilder.widgets.figures;

import java.io.InputStream;

import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImageBoolButtonFigure extends AbstractBoolControlFigure {

	/**
	 * The image itself.
	 */
	private Image onImage, offImage;
	
	private boolean strech;

	private Cursor cursor;
	
	public ImageBoolButtonFigure() {
		cursor = Cursors.HAND;
		addMouseListener(buttonPresser);
		add(boolLabel);
	}
	
	@Override
	protected void layout() {	
		Rectangle clientArea = getClientArea().getCopy();	
		if(boolLabel.isVisible()){			
			Dimension labelSize = boolLabel.getPreferredSize();				
			boolLabel.setBounds(new Rectangle(clientArea.x + clientArea.width/2 - labelSize.width/2,
					clientArea.y + clientArea.height/2 - labelSize.height/2,
					labelSize.width, labelSize.height));
		}
		super.layout();
	}

	
	private Image loadImageFromIPath(IPath path){
		if(path == null || path.isEmpty())
			return null;
		try {
			InputStream input = ResourceUtil.pathToInputStream(path);
			return new Image(Display.getDefault(), input);
		} catch (Exception e) {
			String message = NLS.bind("Failed to load image {0}\n{1}", path, e);
			ConsoleService.getInstance().writeError(message);
		} 
		return null;
		
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {		
		Rectangle clientArea = getClientArea();
		Image temp;
		if(boolValue)
			temp = onImage;
		else 
			temp = offImage;
		if(temp !=null)
			if(strech)
				graphics.drawImage(temp, new Rectangle(temp.getBounds()), clientArea);
			else
				graphics.drawImage(temp, clientArea.getLocation());
		if(!isEnabled()) {
			graphics.setAlpha(DISABLED_ALPHA);
			graphics.setBackgroundColor(DISABLE_COLOR);
			graphics.fillRectangle(bounds);
		}	
		super.paintClientArea(graphics);
	}
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if(runMode){
			if(value){
				if(cursor == null || cursor.isDisposed())
					cursor = Cursors.HAND;		
			}else {				
				cursor = null;
			}	
		}	
		setCursor(runMode? cursor : null);
	}
	
	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);
		setCursor(runMode? cursor : null);
	}
	
	public void setOnImagePath(IPath onImagePath) {
		if(onImage != null){
			onImage.dispose();
			onImage = null;
		}
		onImage = loadImageFromIPath(onImagePath);
		revalidate();
	}
	
	public void setOffImagePath(IPath offImagePath) {
		if(offImage != null){
			offImage.dispose();
			offImage = null;
		}
		offImage = loadImageFromIPath(offImagePath);
		revalidate();
	}
	
	public void setStretch(boolean strech) {
		this.strech = strech;
	}
	
	@Override
	public void setValue(double value) {
		super.setValue(value);
		revalidate();
	}

	public Dimension getAutoSizedDimension() {		
		Image temp = boolValue ? onImage : offImage;
		
		if(temp != null)
			return new Dimension(temp.getBounds().width + getInsets().left + getInsets().right,
					temp.getBounds().height + getInsets().bottom + getInsets().top);
		return null;
	
	}

	public void dispose() {
		if(onImage !=null){
			onImage.dispose();
			onImage = null;
		}
		
		if(offImage !=null){
			offImage.dispose();
			offImage = null;
		}
			
	}
	
	
	
}
