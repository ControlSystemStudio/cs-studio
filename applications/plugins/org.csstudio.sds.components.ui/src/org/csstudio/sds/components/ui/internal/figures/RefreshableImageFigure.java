package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
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
	 * The {@link IPath} to the image.
	 */
	private IPath _path = new Path("");
	/**
	 * The image itself.
	 */
	private Image _image=null;
	/**
	 * The width of the image.
	 */
	private int _imgWidth=0;
	/**
	 * The height of the image.
	 */
	private int _imgHeight=0;
	
	/**
	 * The amount of pixels, which are cropped from the top.
	 */
	private int _topCrop=0;
	/**
	 * The amount of pixels, which are cropped from the bottom.
	 */
	private int _bottomCrop=0;
	/**
	 * The amount of pixels, which are cropped from the left.
	 */
	private int _leftCrop=0;
	/**
	 * The amount of pixels, which are cropped from the right.
	 */
	private int _rightCrop=0;
	/**
	 * The stretch state for the image.
	 */
	private boolean _stretch=true;
	
	/**
	 * We want to have local coordinates here.
	 * @return True if here should used local coordinates
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	
	/**
	 * Fills the image. Nothing to do here.
	 * @param gfx The {@link Graphics} to use
	 */
	protected void fillShape(final Graphics gfx) {}
	
	/**
	 * Draws the outline of the image. Nothing to do here.
	 * @param gfx The {@link Graphics} to use
	 */
	protected void outlineShape(final Graphics gfx) {}
	
	/**
	 * The main drawing routine.
	 * @param gfx The {@link Graphics} to use
	 */
	public void paintFigure(final Graphics gfx) {
		Rectangle bound=getBounds().getCopy();
		bound.crop(this.getInsets());
		Image temp;
		
		try {
			if (_image==null && !_path.isEmpty()) {
				String currentPath = _path.toString();
				IPath fullPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
				try {
					temp=new Image(Display.getDefault(),currentPath);
				} catch (Exception e) {
					currentPath  = fullPath.toString()+_path.toString();
					temp=new Image(Display.getDefault(),currentPath);
				}
				if (_stretch) {
					_image=new Image(Display.getDefault(),
							temp.getImageData().scaledTo(bound.width+_leftCrop+_rightCrop,
									bound.height+_topCrop+_bottomCrop));
				} else {
					_image=new Image(Display.getDefault(),temp.getImageData());
				}
				temp.dispose();
				temp=null;
				_imgWidth=_image.getBounds().width;
				_imgHeight=_image.getBounds().height;
			}
		} catch (Exception e) {
			if (_image!=null) {
				_image.dispose();
			}
			_image=null;
			if (!_path.isEmpty()) {
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
				System.out.println("RefreshableImageFigure.paintFigure() ERROR loading image\n"+_path);
				TextPainter.drawText(gfx,"ERROR loading image\n"+_path,bound.width/2,bound.height/2,TextPainter.CENTER);
				f.dispose();
			}
		}
		if (_image!=null) {
			gfx.drawImage(_image,  
					_leftCrop,_topCrop,
					_imgWidth-_leftCrop-_rightCrop,_imgHeight-_topCrop-_bottomCrop,
					bound.x,bound.y,
					_imgWidth-_leftCrop-_rightCrop,_imgHeight-_topCrop-_bottomCrop);
		}
	}
	
	/**
	 * Resizes the image.
	 */
	public void resizeImage() {
		if (_stretch) {
			if (_image!=null) {
				_image.dispose();
			}
			_image=null;
		}
	}
	
	/**
	 * Sets the path to the image.
	 * @param newval The path to the image
	 */
	public void setFilePath(final IPath newval) {
		_path=newval;
		if (_image!=null) {
			_image.dispose();
		}
		_image=null;
	}
	
	/**
	 * Returns the path to the image.
	 * @return The path to the image
	 */
	public IPath getFilePath() {
		return _path;
	}
	
	/**
	 * Sets the amount of pixels, which are cropped from the top. 
	 * @param newval The amount of pixels
	 */
	public void setTopCrop(final int newval) {
		_topCrop=newval;
		resizeImage();
	}
	
	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * @return The amount of pixels
	 */
	public int getTopCrop() {
		return _topCrop;
	}
	
	/**
	 * Sets the amount of pixels, which are cropped from the bottom. 
	 * @param newval The amount of pixels
	 */
	public void setBottomCrop(final int newval) {
		_bottomCrop=newval;
		resizeImage();
	}
	
	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * @return The amount of pixels
	 */
	public int getBottomCrop() {
		return _bottomCrop;
	}
	
	/**
	 * Sets the amount of pixels, which are cropped from the left. 
	 * @param newval The amount of pixels
	 */
	public void setLeftCrop(final int newval) {
		_leftCrop=newval;
		resizeImage();
	}
	
	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * @return The amount of pixels
	 */
	public int getLeftCrop() {
		return _leftCrop;
	}
	
	/**
	 * Sets the amount of pixels, which are cropped from the right. 
	 * @param newval The amount of pixels
	 */
	public void setRightCrop(final int newval) {
		_rightCrop=newval;
		resizeImage();
	}
	
	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * @return The amount of pixels
	 */
	public int getRightCrop() {
		return _rightCrop;
	}
	
	/**
	 * Sets the stretch state for the image.
	 * @param newval The new state (true, if it should be stretched, false otherwise)
	 */
	public void setStretch(final boolean newval) {
		_stretch=newval;
		if (_image!=null) {
			_image.dispose();
		}
		_image=null;
	}
	
	/**
	 * Returns the stretch state for the image.
	 * @return True, if it should be stretched, false otherwise
	 */
	public boolean getStretch() {
		return _stretch;
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
