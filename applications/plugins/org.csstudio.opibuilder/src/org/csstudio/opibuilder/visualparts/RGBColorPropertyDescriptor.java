package org.csstudio.opibuilder.visualparts;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;

public class RGBColorPropertyDescriptor extends ColorPropertyDescriptor {

	public RGBColorPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setLabelProvider(new RgbLabelProvider());
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		//CellEditor editor = new ColorCellEditor(parent);
		RGBCellEditor editor = new RGBCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for RGB value, which displays a small colored icon and
	 * the RGB value as String as well.
	 * 
	 * @author swende
	 * 
	 */
	private final class RgbLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Image getImage(final Object element) {
			return CustomMediaFactory
					.getInstance()
					.getImageFromImageDescriptorCache(createIcon((RGB) element));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			RGB rgb = (RGB) element;
			return "(" + rgb.red + ";" + rgb.green + ";" + rgb.blue + ")";
		}

		/**
		 * Creates a small icon using the specified color.
		 * 
		 * @param rgb
		 *            the color
		 * @return an icon
		 */
		private ImageDescriptor createIcon(final RGB rgb) {
			assert rgb != null : "rgb!=null"; //$NON-NLS-1$

			Color color = CustomMediaFactory.getInstance().getColor(rgb);

			// create new graphics context, to draw on
			Image image = new Image(Display.getCurrent(), 16, 16);
			GC gc = new GC(image);

			// draw transparent background
			Color bg = CustomMediaFactory.getInstance().getColor(255, 255, 255);
			gc.setBackground(bg);
			gc.fillRectangle(0, 0, 16, 16);
			// draw icon
			gc.setBackground(color);
			Rectangle r = new Rectangle(1, 4, 14, 9);
			gc.fillRectangle(r);
			gc
					.setBackground(CustomMediaFactory.getInstance().getColor(0,
							0, 0));
			gc.drawRectangle(r);
			gc.dispose();

			// setup tranparency
			ImageDescriptor descr = ImageDescriptor.createFromImage(image);
			int whitePixel = descr.getImageData().palette.getPixel(new RGB(255,
					255, 255));
			descr.getImageData().transparentPixel = whitePixel;

			// dispose the temp image
			image.dispose();

			return descr;
		}
	}
	
}
