package org.csstudio.askap.sb;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.askap.sb"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	public static Image RED_LED_IMAGE = null;
	public static Image GREEN_LED_IMAGE = null;
	public static Image GREY_LED_IMAGE = null;
	public static Image WAIT_IMAGE = null;
	public static Image RUN_IMAGE = null;

	
	/**
	 * The constructor
	 */
	public Activator() {
		RED_LED_IMAGE = getImage("icons/red_round_button.png");        	
		GREEN_LED_IMAGE = getImage("icons/green_round_button.png");
		GREY_LED_IMAGE = getImage("icons/grey_round_button.png");

		WAIT_IMAGE = getImage("icons/time-machine-icon.png");
		RUN_IMAGE = getImage("icons/Animals-Running-Rabbit-icon.png");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	
	// getImageDescriptor("icons/wavesample.gif")
    public Image getImage(final String path)
    {
        Image image = getImageRegistry().get(path);
        if (image == null)
        {
        	ImageDescriptor des = getImageDescriptor(path);
        	
        	if (des == null)
        		return null;
        	
            image = des.createImage();
            getImageRegistry().put(path, image);
        }
        return image;
    }

	public ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	

}
