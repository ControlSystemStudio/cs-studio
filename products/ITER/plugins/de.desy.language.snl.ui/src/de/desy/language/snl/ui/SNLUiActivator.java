package de.desy.language.snl.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.desy.language.snl.ui.preferences.SNLPreferenceListener;
import de.desy.language.snl.ui.rules.SNLCodeElementTextAttributeConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class SNLUiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.desy.language.snl.ui";

	// The shared instance
	private static SNLUiActivator plugin;

	/**
	 * The constructor
	 */
	public SNLUiActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		SNLUiActivator.plugin = this;

		this.initializeHighlghtingColors();
		this.getPreferenceStore().addPropertyChangeListener(
				new SNLPreferenceListener(this.getPreferenceStore()));
	}

	private void initializeHighlghtingColors() {
		for (final SNLCodeElementTextAttributeConstants constant : SNLCodeElementTextAttributeConstants
				.values()) {
			final String colorValueAsString = this.getPreferenceStore()
					.getString(constant.asStringId() + ".color");
			if (colorValueAsString.length() > 1) {
				final RGB color = StringConverter.asRGB(colorValueAsString);
				constant.setRGBValue(color);
			} // else: Default is placed in Enum.
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		SNLUiActivator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SNLUiActivator getDefault() {
		return SNLUiActivator.plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				SNLUiActivator.PLUGIN_ID, path);
	}
}
