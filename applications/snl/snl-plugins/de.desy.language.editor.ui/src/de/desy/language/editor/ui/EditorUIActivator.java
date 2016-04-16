package de.desy.language.editor.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EditorUIActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.desy.language.editor.ui";

    // The shared instance
    private static EditorUIActivator plugin;

    /**
     * The constructor
     */
    public EditorUIActivator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static EditorUIActivator getDefault() {
        return plugin;
    }

//    public IEditorInput getDocumentProvider() {
//        if (this.fDocumentProvider == null) {
//            //this.fDocumentProvider = new FileDocumentProvider();//new DocumentProvider();
//        }
//        return this.fDocumentProvider;
//    }

}
