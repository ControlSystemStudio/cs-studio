package org.csstudio.perspectives;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Class handling saving perspectives to file.
 */
@SuppressWarnings("restriction")  // This class uses internal e4 API.
public class PerspectiveSaver implements EventHandler {

    public static final String PERSPECTIVE_PREFIX  = "perspective_";

    @Inject
    private IEventBroker broker;

    @Inject
    private EModelService modelService;

    @Inject
    private IPreferencesService prefs;

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    @Inject
    @Named(E4Workbench.INSTANCE_LOCATION)
    private Location instanceLocation;

    @Inject
    private IPerspectiveUtils perspectiveUtils;

    @Inject
    private IFileUtils fileUtils;

    /**
     * Subscribe to the e4 event broker listening for perspective save events.
     */
    @PostConstruct
    public void init() {
        // Subscribe to perspective save events.
        broker.subscribe(UIEvents.UILifeCycle.PERSPECTIVE_SAVED, this);
    }

    /**
     * When a perspective is saved by a user, save it also to a file in .xmi format.
     * Only do this if the relevant preference setting is present.
     *
     * Importantly, it is necessary to save any persisted state from an MPart into
     * the corresponding MPlaceholder in the perspective.  This allows OPIView to
     * get access to the persisted state when loading.
     *
     * Finally, it is necessary to re-import the perspective once the new state has been
     * added.  This ensures that the registered perspective has this information available
     * for the specific case of Window | New Window when the perspective has just been
     * saved.
     *
     * @param Event the event object supplied by the event broker
     */
    @Override
    public void handleEvent(Event event) {
        Object o = event.getProperty(UIEvents.EventTags.ELEMENT);
        if (o instanceof MPerspective) {
            try {
                Path saveDir = getSaveDirectory();
                if (saveDir != null) {
                    savePerspectiveToDirectory((MPerspective) o, saveDir);
                }
            } catch (IOException e) {
                Plugin.getLogger().log(Level.WARNING, Messages.PerspectiveSaver_saveFailed, e);
            }
        }
    }

    private void savePerspectiveToDirectory(MPerspective p, Path saveDir) throws IOException {
        List<MPlaceholder> phs = modelService.findElements(p, null, MPlaceholder.class, null);
        // Copy persisted state from part to placeholder.
        for (MPlaceholder ph : phs) {
            ph.getPersistedState().putAll(ph.getRef().getPersistedState());
        }
        // If you do not clone the MPerspective, an IllegalArgumentException
        // is thrown by the framework when importing.
        MPerspective clone = (MPerspective) modelService.cloneElement(p, null);
        URI uri = constructUri(saveDir, clone.getLabel());
        perspectiveUtils.savePerspective(clone, uri);
        // The new perspective import and export mechanism will intercept
        // this preference change and import the perspective for us.
        // I'm not sure why we need to import explicitly even though the
        // perspective has been saved.
        String perspAsString = perspectiveUtils.perspectiveToString(clone);
        preferences.put(clone.getLabel() + Plugin.PERSPECTIVE_SUFFIX, perspAsString);
        Plugin.getLogger().config("Saved perspective to " + uri);
    }

    URI constructUri(Path dataArea, String perspectiveName) {
        if (dataArea == null || perspectiveName == null) {
            throw new NullPointerException("Arguments to constructUri may not be null.");
        }
        URI uri = fileUtils.pathToEmfUri(dataArea);
        uri = uri.appendSegment(PERSPECTIVE_PREFIX + perspectiveName);
        uri = uri.appendFileExtension(Plugin.XMI_EXTENSION);
        return uri;
    }

    Path getSaveDirectory() throws IOException {
        String saveDirPreference = prefs.getString(PerspectivesPreferencePage.ID,
                PerspectivesPreferencePage.PERSPECTIVE_SAVE_DIRECTORY, null, null);
        Path saveDir = null;
        if (saveDirPreference != null) {
            saveDir = fileUtils.stringPathToPath(saveDirPreference);
        }
        return saveDir;
    }

}
