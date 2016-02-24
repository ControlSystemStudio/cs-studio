package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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

@SuppressWarnings("restriction")  // This class uses internal e4 API.
public class PerspectiveSaver implements EventHandler {

    private static final Logger logger = Logger.getLogger(PerspectiveSaver.class.getCanonicalName());

    public static final String PERSPECTIVE_PREFIX  = "perspective_";

    @Inject
    private IEventBroker broker;

    @Inject
    private EModelService modelService;

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    @Inject
    @Named(E4Workbench.INSTANCE_LOCATION)
    private Location instanceLocation;

    @Inject
    private IPerspectiveUtils perspectiveUtils;

    private File dataDirectory;

    /**
     * Create required resources and subscribe to the e4 event broker listening for
     * perspective save events.
     */
    @PostConstruct
    public void init() {
        try {
            logger.config("Initialising perspective saver.");
            URL dataUri = instanceLocation.getDataArea(Plugin.ID);
            dataDirectory = new File(dataUri.getFile());
            Files.createDirectories(dataDirectory.toPath());
            // Subscribe to perspective save events.
            broker.subscribe(UIEvents.UILifeCycle.PERSPECTIVE_SAVED, this);
        } catch (IOException e) {
            logger.log(Level.WARNING, Messages.PerspectiveSaver_saveFailed, e);
        }
    }


    /**
     * When a perspective is saved by a user, save it also to a file in .xmi format.
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
                MPerspective p = (MPerspective) o;

                List<MPlaceholder> phs = modelService.findElements(p, null, MPlaceholder.class, null);
                // Copy persisted state from part to placeholder.
                for (MPlaceholder ph : phs) {
                    ph.getPersistedState().putAll(ph.getRef().getPersistedState());
                }
                MPerspective clone = (MPerspective) modelService.cloneElement(p, null);
                URI uri = constructUri(clone.getLabel());
                perspectiveUtils.savePerspective(clone, uri);
                // The new perspective import and export mechanism will intercept
                // this preference change and import the perspective for us.
                // I'm not sure why we need to import explicitly even though the 
                // perspective has been saved.
                String perspAsString = perspectiveUtils.perspToString(clone);
                preferences.put(clone.getLabel() + Plugin.PERSPECTIVE_SUFFIX, perspAsString);
                logger.config("Saved perspective to " + uri);
            } catch (IOException e) {
                logger.log(Level.WARNING, Messages.PerspectiveSaver_saveFailed, e);
            }
        }
    }

    private URI constructUri(String perspectiveName) {
        StringBuilder sb = new StringBuilder(Plugin.FILE_PREFIX);
        sb.append("/");
        sb.append(PERSPECTIVE_PREFIX);
        sb.append(perspectiveName);
        sb.append(Plugin.XMI_EXTENSION);
        return URI.createURI(sb.toString());
    }

}
