package org.csstudio.frib.product;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	// private static final String PERSPECTIVE_ID =
	// "org.csstudio.frib.product.perspective";
	private OpenDocumentEventProcessor openDocProcessor;

    public ApplicationWorkbenchAdvisor(
			OpenDocumentEventProcessor openDocProcessor) {
    	this.openDocProcessor = openDocProcessor;
    }

    @Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		// Per default, state is not preserved (RCP book 5.1.1)
		configurer.setSaveAndRestore(true);
		IDE.registerAdapters();
		declareWorkbenchImages();
	}

	public String getInitialWindowPerspectiveId() {
		return CSSPerspective.ID;
	}


    @Override
    public void eventLoopIdle(final Display display)
    {
    	if(openDocProcessor != null)
    		openDocProcessor.catchUp(display);
    	super.eventLoopIdle(display);
    }
	/**
	 * 
	 * register the icons that are registered by the IDE application
	 * Bug report
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=234252
	 * Solution
	 * http://www.eclipsezone.com/eclipse/forums/t84055.html
	 * 
	 * TODO there might be other missing icons (shroffk)
	 * 
	 * Declares all IDE-specific workbench images. This includes both "shared"
	 * images (named in {@link IDE.SharedImages}) and internal images.
	 * 
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareWorkbenchImages() {
		declareWorkbenchImage(IDE.SharedImages.IMG_OBJ_PROJECT,
				"icons/project_open.png", true); //$NON-NLS-1$
		declareWorkbenchImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
				"icons/project_close.png", true); //$NON-NLS-1$
	}

	/**
	 * Declares a workbench image.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the image
	 * @param path
	 *            the path of the image file relative to the product plugin;
	 * @param shared
	 *            <code>true</code> if this is a shared image, and
	 *            <code>false</code> if this is not a shared image
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareWorkbenchImage(String symbolicName, String path,
			boolean shared) {
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, path);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}
}
