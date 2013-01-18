/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.ui.util;

import java.util.HashMap;

import org.eclipse.core.runtime.Plugin;
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
	private HashMap<String, Image> _imageCache;

	/**
	 * Private constructor to avoid instantiation.
	 */
	private CustomMediaFactory() {
		Display display = Display.getDefault();
		_colorRegistry = new ColorRegistry(display);
		_imageRegistry = new ImageRegistry(display);
		_fontRegistry = new FontRegistry(display);

		_imageCache = new HashMap<String, Image>();

		// dispose all images from the image cache, when the display is disposed
		display.addListener(SWT.Dispose, new Listener() {
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
	public static synchronized CustomMediaFactory getInstance() {
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
	 * Create the <code>Font</code> for the given <code>FontData</code> and the
	 * given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @param style
	 *            The style code.
	 * @return The <code>Font</code> for the given <code>FontData</code> and the
	 *         given style code.
	 */
	public Font getFont(final FontData[] fontData, final int style) {
		FontData f = fontData[0];
		Font font = getFont(f.getName(), f.getHeight(), style);
		return font;
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code> and the
	 * given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @return The <code>Font</code> for the given <code>FontData</code> and the
	 *         given style code.
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
		// FIXME Die default Schriftart bzw. Schriftgröße hängt vom
		// Betriebssystem ab
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
	public Image getImageFromPlugin(final String pluginId, final String relativePath) {
		String key = pluginId + "." + relativePath; //$NON-NLS-1$

		// does image exist
		if (_imageRegistry.get(key) == null) {
			ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, relativePath);
			_imageRegistry.put(key, descr);
		}

		return _imageRegistry.get(key);
	}

	/**
	 * Load the <code>Image</code> from the given path in the given plugin.
	 * Usually, this is the image found via the the given plug-in relative path.
	 * But this implementation also supports a hack for testing: If no plugin is
	 * running, because for example this is an SWT-only test, the path is used
	 * as is, i.e. relative to the current directory.
	 * 
	 * @param plugin
	 *            The plugin that contains the requested image.
	 * @param pluginId
	 *            The id of the plugin.
	 * @param relativePath
	 *            The image's relative path to the root of the plugin.
	 * @return The <code>Image</code> from the given path in the given plugin.
	 */
	public Image getImageFromPlugin(final Plugin plugin, final String pluginId, final String relativePath) {
		String key = pluginId + "." + relativePath; //$NON-NLS-1$	
		// does image exist
		if (_imageRegistry.get(key) == null) {
			if (plugin != null) {
				ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, relativePath);
				_imageRegistry.put(key, descr);
			} else {
				final Display display = Display.getCurrent();
				final Image img = new Image(display, relativePath);
				_imageRegistry.put(key, ImageDescriptor.createFromImage(img));
			}
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
	 * @return The <code>ImageDescriptor</code> from the given path in the given
	 *         plugin.
	 */
	public ImageDescriptor getImageDescriptorFromPlugin(final String pluginId, final String relativePath) {
		String key = pluginId + "." + relativePath; //$NON-NLS-1$

		// does image exist
		if (_imageRegistry.get(key) == null) {
			ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, relativePath);
			_imageRegistry.put(key, descr);
		}

		return _imageRegistry.getDescriptor(key);
	}

	/** the color for light blue */
	final static public RGB COLOR_LIGHT_BLUE = new RGB(153, 186, 243);

	/** the color for blue */
	final static public RGB COLOR_BLUE = new RGB(0, 0, 255);

	/** the color for white */
	final static public RGB COLOR_WHITE = new RGB(255, 255, 255);

	/** the color for gray */
	final static public RGB COLOR_GRAY = new RGB(200, 200, 200);

	/** the color for dark gray */
	final static public RGB COLOR_DARK_GRAY = new RGB(150, 150, 150);

	/** the color for black */
	final static public RGB COLOR_BLACK = new RGB(0, 0, 0);

	/** the color for red */
	final static public RGB COLOR_RED = new RGB(255, 0, 0);

	/** the color for green */
	final static public RGB COLOR_GREEN = new RGB(0, 255, 0);

	/** the color for yellow */
	final static public RGB COLOR_YELLOW = new RGB(255, 255, 0);

	/** the color for pink */
	final static public RGB COLOR_PINK = new RGB(255, 0, 255);

	/** the color for cyan */
	final static public RGB COLOR_CYAN = new RGB(0, 255, 255);

	/** the color for orange */
	final static public RGB COLOR_ORANGE = new RGB(255, 128, 0);

	/** the color for orange */
	final static public RGB COLOR_PURPLE = new RGB(128, 0, 255);

	/** the font for Arial in height of 9 */
	final static public FontData FONT_ARIAL = new FontData("Arial", 9, SWT.NONE);

	/** the font for Tahoma in height of 9 */
	final static public FontData FONT_TAHOMA = new FontData("Tahoma", 9, SWT.NONE);
}
