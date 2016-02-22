package org.csstudio.utility.product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class PerspectiveSaver implements EventHandler {

    public static final String PERSPECTIVE_PREFIX  = "perspective_";

    @Inject
    private IEventBroker broker;

    @Inject
    private EModelService modelService;

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    private URL url;

    private File resourceDirectory;

    public PerspectiveSaver() {

        try {
            url = Platform.getInstanceLocation().getDataArea("org.csstudio.startup");
            resourceDirectory = new File(url.getFile());
            Files.createDirectories(resourceDirectory.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() {
        // Subscribe to perspective save events.
        broker.subscribe(UIEvents.UILifeCycle.PERSPECTIVE_SAVED, this);
    }

    private void savePerspective(MPerspective persp, String file) throws IOException {
        URI uri = URI.createURI("file://" + file);
        Resource resource = new E4XMIResourceFactory().createResource(uri);
        resource.getContents().add((EObject) persp);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            resource.save(Collections.EMPTY_MAP);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resource.getContents().clear();
    }

    /**
     * When a perspective is saved by a user, save it also to a file.
     */
    @Override
    public void handleEvent(Event event) {
        Object o = event.getProperty(UIEvents.EventTags.ELEMENT);
        if (o instanceof MPerspective) {
            try {
                MPerspective p = (MPerspective) o;

                List<MPlaceholder> phs = modelService.findElements(p, null, MPlaceholder.class, null);
                for (MPlaceholder ph : phs) {
                    ph.getPersistedState()
                            .putAll(ph.getRef().getPersistedState());
                }
                MPerspective clone = (MPerspective) modelService.cloneElement(p, null);
                savePerspective(clone, url.getFile() + "/" + PERSPECTIVE_PREFIX  + p.getLabel() + PerspectiveLoader.XMI_EXTENSION);
                // The new perspective import and export mechanism will intercept
                // this preference change and import the perspective for us.
                // I'm not sure why we need to import explicitly even though the 
                // perspective has been saved.
                String perspAsString = PerspectiveLoader.perspToString(clone);
                preferences.put(clone.getLabel() + PerspectiveLoader.PERSPECTIVE_SUFFIX, perspAsString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
