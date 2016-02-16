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

    @Inject
    private IEventBroker broker;

    @Inject
    private EModelService modelService;

    private URL url;

    private File resourceDirectory;

    public PerspectiveSaver() {

        try {
            url = Platform.getInstanceLocation()
                    .getDataArea("org.csstudio.startup");
            System.out.println("The url: " + url);
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
        System.out.println("The file is " + file);
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
        Object o = event.getProperty("ChangedElement");
        if (o instanceof MPerspective) {
            try {
                MPerspective p = (MPerspective) o;
                MPerspective perspClone = (MPerspective) modelService
                        .cloneElement(p, null);
                List<MPlaceholder> phs = modelService.findElements(p, null,
                        MPlaceholder.class, null);
                for (MPlaceholder ph : phs) {
                    System.out.println("placeholder " + ph);
                    ph.getPersistedState()
                            .putAll(ph.getRef().getPersistedState());
                    ph.getPersistedState().put("Hello",  "World");
                }
                String name = p.getLabel();
                savePerspective(perspClone, url.getFile() + "/perspective_"  + name + ".xmi");
                System.out.println("Saved perspective " + name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
