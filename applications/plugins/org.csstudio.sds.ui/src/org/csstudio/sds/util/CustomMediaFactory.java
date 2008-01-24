package org.csstudio.sds.util;

import java.util.HashMap;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A factory, which provides convinience methods for the creation of Images and
 * Fonts.
 * 
 * All resources created via this factory get automatically disposed, when the
 * application is stopped.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class CustomMediaFactory {
	/**
	 * The shared instance.
	 */
	private static CustomMediaFactory _instance;

	/**
	 * The color registry.
	 */
	private ColorRegistry _colorRegistry;

	/**
	 * The image registry.
	 */
	private ImageRegistry _imageRegistry;

	/**
	 * The font registry.
	 */
	private FontRegistry _fontRegistry;

	/**
	 * Map that holds the provided image descriptors.
	 */
	private HashMap<ImageDescriptor, Image> _imageCache;

	/**
	 * Private constructor to avoid instantiation.
	 */
	private CustomMediaFactory() {
		_colorRegistry = new ColorRegistry();
		_imageRegistry = new ImageRegistry();
		_fontRegistry = new FontRegistry();

		_imageCache = new HashMap<ImageDescriptor, Image>();

		// dispose all images from the image cache, when the display is disposed
		Display.getCurrent().addListener(SWT.Dispose, new Listener() {
			public void handleEvent(final Event event) {
				for (Image img : _imageCache.values()) {
					img.dispose();
				}
			}
		});

	}

	/**
	 * Return the shared instance of this class.
	 * 
	 * @return The shared instance of this class.
	 */
	public static CustomMediaFactory getInstance() {
		if (_instance == null) {
			_instance = new CustomMediaFactory();
		}

		return _instance;
	}

	/**
	 * Create the <code>Color</code> for the given color information.
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * 
	 * @return The <code>Color</code> for the given color information.
	 */
	public Color getColor(final int r, final int g, final int b) {
		return getColor(new RGB(r, g, b));
	}

	/**
	 * Create the <code>Color</code> for the given <code>RGB</code>.
	 * 
	 * @param rgb
	 *            A <code>RGB</code> object.
	 * @return The <code>Color</code> for the given <code>RGB</code>.
	 */
	public Color getColor(final RGB rgb) {
		assert rgb != null : "rgb!=null"; //$NON-NLS-1$
		Color result = null;

		String key = String.valueOf(rgb.hashCode());

		if (!_colorRegistry.hasValueFor(key)) {
			_colorRegistry.put(key, rgb);
		}

		result = _colorRegistry.get(key);

		return result;
	}

	/**
	 * Create the <code>Font</code> for the given information.
	 * 
	 * @param name
	 *            The font name.
	 * @param height
	 *            The font height.
	 * @param style
	 *            The font style.
	 * @return The <code>Font</code> for the given information.
	 */
	public Font getFont(final String name, final int height, final int style) {
		assert name != null : "name!=null"; //$NON-NLS-1$

		FontData fd = new FontData(name, height, style);

		String key = String.valueOf(fd.hashCode());
		if (!_fontRegistry.hasValueFor(key)) {
			_fontRegistry.put(key, new FontData[] { fd });
		}

		return _fontRegistry.get(key);
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code>.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @return The <code>Font</code> for the given <code>FontData</code>
	 */
	public Font getFont(final FontData[] fontData) {
		FontData f = fontData[0];
		return getFont(f.getName(), f.getHeight(), f.getStyle());
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code> and
	 * the given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @param style
	 *            The style code.
	 * @return The <code>Font</code> for the given <code>FontData</code> and
	 *         the given style code.
	 */
	public Font getFont(final FontData[] fontData, final int style) {
		FontData f = fontData[0];
		Font font = getFont(f.getName(), f.getHeight(), style);
		return font;
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code> and
	 * the given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @return The <code>Font</code> for the given <code>FontData</code> and
	 *         the given style code.
	 */
	public Font getFont(final FontData fontData) {
		Font font = getFont(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		return font;
	}
	
	/**
	 * Return the system's default font.
	 * 
	 * @param style
	 *            additional styles, e.g. SWT.Bold
	 * @return The system's default font.
	 */
	public Font getDefaultFont(final int style) {
		// FIXME Die default Schriftart bzw. Schriftgröße hängt vom Betriebssystem ab 
		return getFont("Arial", 10, style); //$NON-NLS-1$
	}
	

	/**
	 * Load the <code>Image</code> from the given path in the given plugin.
	 * 
	 * @param pluginId
	 *            The id of the plugin that contains the requested image.
	 * @param relativePath
	 *            The resource path of the requested image.
	 * @return The <code>Image</code> from the given path in the given plugin.
	 */
	public Image getImageFromPlugin(final String pluginId,
			final String relativePath) {
		String key = pluginId + "." + relativePath; //$NON-NLS-1$

		// does image exist
		if (_imageRegistry.get(key) == null) {
			ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(
					pluginId, relativePath);
			_imageRegistry.put(key, descr);
		}

		return _imageRegistry.get(key);
	}

	/**
	 * Load the <code>ImageDescriptor</code> from the given path in the given
	 * plugin.
	 * 
	 * @param pluginId
	 *            The id of the plugin that contains the requested image.
	 * @param relativePath
	 *            The resource path of the requested image.
	 * @return The <code>ImageDescriptor</code> from the given path in the
	 *         given plugin.
	 */
	public ImageDescriptor getImageDescriptorFromPlugin(final String pluginId,
			final String relativePath) {
		String key = pluginId + "." + relativePath; //$NON-NLS-1$

		// does image exist
		if (_imageRegistry.get(key) == null) {
			ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(
					pluginId, relativePath);
			_imageRegistry.put(key, descr);
		}

		return _imageRegistry.getDescriptor(key);
	}

	/**
	 * Create an <code>Image</code> from the given
	 * <code>ImageDescriptor</code>.
	 * 
	 * @param imageDescriptor
	 *            The <code>ImageDescriptor</code>
	 * @return The <code>Image</code> that was created from the given
	 *         <code>ImageDescriptor</code>
	 */
	public Image getImageFromImageDescriptorCache(
			final ImageDescriptor imageDescriptor) {
		assert imageDescriptor != null : "imageDescriptor!=null"; //$NON-NLS-1$

		if (!_imageCache.containsKey(imageDescriptor)) {
			_imageCache.put(imageDescriptor, imageDescriptor.createImage());
		}
		return _imageCache.get(imageDescriptor);
	}
}
