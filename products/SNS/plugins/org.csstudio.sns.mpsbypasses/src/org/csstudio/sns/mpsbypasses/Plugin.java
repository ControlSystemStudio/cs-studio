package org.csstudio.sns.mpsbypasses;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.sns.mpsbypasses";

	/** @param path Image path within plugin
	 *  @return {@link ImageDescriptor}
	 */
	public static ImageDescriptor getImageDescription(final String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}
}
