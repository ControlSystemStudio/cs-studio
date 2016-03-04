package org.csstudio.perspectives;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

public class PerspectiveStartup implements IStartup {

    /**
     * Create a perspective saver on startup.
     */
    @Override
    public void earlyStartup() {
        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        context.set(IPerspectiveUtils.class.getCanonicalName(), new PerspectiveUtils());
        context.set(IFileUtils.class.getCanonicalName(), new FileUtils());
        // Initialise a perspective saver.
        ContextInjectionFactory.make(PerspectiveSaver.class, context);
        // Load perspectives at startup.
        PerspectiveLoader loader = ContextInjectionFactory.make(PerspectiveLoader.class, context);
        try {
            Path perspectivesDirectory = getPerspectivesDirectory();
            loader.loadFromDirectory(perspectivesDirectory);
        } catch (IOException | URISyntaxException e) {
            Plugin.getLogger().log(Level.WARNING, Messages.PerspectiveStartup_startupLoadFailed, e);
        }
    }

    private Path getPerspectivesDirectory() throws MalformedURLException, IOException, URISyntaxException {
        IPreferencesService prefs = Platform.getPreferencesService();
        String dirPreference = prefs.getString(PerspectivesPreferencePage.ID,
                PerspectivesPreferencePage.PERSPECTIVE_LOAD_DIRECTORY, Plugin.PERSPECTIVE_LOAD_LOCATION, null);
        IFileUtils fileUtils = new FileUtils();
        return fileUtils.stringPathToPath(dirPreference);
    }

}
