package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * An image figure.
 * 
 * @author jbercic
 * 
 */
public final class RefreshableImageFigure extends Shape implements IAdaptable {
	
	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	/**
	 * filename of the image and the image itself
	 * width and height of the image
	 */
	private String filename="";
	private Image image=null;
	private int img_width=0,img_height=0;
	
	/**
	 * how much to crop the image, stretch it?
	 */
	private int top_crop=0,bottom_crop=0,left_crop=0,right_crop=0;
	private boolean stretch=true;
	
	/**
	 * Border properties.
	 */
	private int border_width;
//	private RGB border_color = new RGB(0,0,0);
	
	/**
	 * We want to have local coordinates here.
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	
	/**
	 * Fills the image. Nothing to do here.
	 */
	protected void fillShape(Graphics gfx) {}
	
	/**
	 * Draws the outline of the image. Nothing to do here.
	 */
	protected void outlineShape(Graphics gfx) {}
	
	/**
	 * The main drawing routine.
	 */
	public void paintFigure(Graphics gfx) {
		Rectangle bound=getBounds();
		Rectangle border_bound=bound.getCropped(new Insets(border_width));
		Image temp;
		
		try {
			if (image==null && filename!="") {
				temp=new Image(Display.getDefault(),filename);
				if (stretch==true) {
					image=new Image(Display.getDefault(),
							temp.getImageData().scaledTo(border_bound.width+left_crop+right_crop,
									border_bound.height+top_crop+bottom_crop));
				} else {
					image=new Image(Display.getDefault(),temp.getImageData());
				}
				temp.dispose();
				temp=null;
				img_width=image.getBounds().width;
				img_height=image.getBounds().height;
			}
		}
		catch (Exception e) {
			if (image!=null) {
				image.dispose();
			}
			image=null;
			if (filename!="") {
				Font f=gfx.getFont();
				FontData fd=f.getFontData()[0];
				
				if (bound.width>=20*30) {
					fd.setHeight(30);
				} else {
					if (bound.width/20+1<7) {
						fd.setHeight(7);
					} else {
						fd.setHeight(bound.width/20+1);
					}
				}
				f=new Font(Display.getDefault(),fd);
				gfx.setFont(f);
				gfx.setBackgroundColor(getBackgroundColor());
				gfx.setForegroundColor(getForegroundColor());
				gfx.fillRectangle(bound);
				gfx.translate(bound.getLocation());
				TextPainter.drawText(gfx,"ERROR loading image\n"+filename,bound.width/2,bound.height/2,TextPainter.CENTER);
				f.dispose();
			}
		}
		if (image!=null) {
			gfx.drawImage(image,
					left_crop,top_crop,
					img_width-left_crop-right_crop,img_height-top_crop-bottom_crop,
					border_width,border_width,
					img_width-left_crop-right_crop,img_height-top_crop-bottom_crop);
		}
	}
	
	public void resizeImage() {
		if (stretch==true) {
			if (image!=null) {
				image.dispose();
			}
			image=null;
		}
	}
	
	public void setBorderWidth(final int newval) {
		border_width=newval;
		resizeImage();
	}
	public int getBorderWidth() {
		return border_width;
	}
	
	public void setFilename(final String newval) {
		filename=newval;
		if (image!=null) {
			image.dispose();
		}
		image=null;
	}
	public String getFilename() {
		return filename;
	}
	
	public void setTopCrop(final int newval) {
		top_crop=newval;
		resizeImage();
	}
	public int getTopCrop() {
		return top_crop;
	}
	
	public void setBottomCrop(final int newval) {
		bottom_crop=newval;
		resizeImage();
	}
	public int getBottomCrop() {
		return bottom_crop;
	}
	
	public void setLeftCrop(final int newval) {
		left_crop=newval;
		resizeImage();
	}
	public int getLeftCrop() {
		return left_crop;
	}
	
	public void setRightCrop(final int newval) {
		right_crop=newval;
		resizeImage();
	}
	public int getRightCrop() {
		return right_crop;
	}
	
	public void setStretch(final boolean newval) {
		stretch=newval;
		if (image!=null) {
			image.dispose();
		}
		image=null;
	}
	public boolean getStretch() {
		return stretch;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
}
