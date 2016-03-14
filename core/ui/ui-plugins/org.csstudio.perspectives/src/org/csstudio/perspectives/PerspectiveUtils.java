package org.csstudio.perspectives;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

@SuppressWarnings("restriction")  // This class uses internal e4 API.
public class PerspectiveUtils implements IPerspectiveUtils {

    /**
     * Save a perspective object to a .xmi file.
     * @param persp Perspective object to save
     * @param file Filepath of file to save into
     * @throws IOException
     */
    @Override
    public void savePerspective(MPerspective persp, URI fileUri) throws IOException {
        Resource resource = new E4XMIResourceFactory().createResource(fileUri);
        resource.getContents().add((EObject) persp);
        resource.save(Collections.EMPTY_MAP);
    }

    @Override
    public String perspectiveToString(MPerspective persp) throws IOException {
        if (persp == null) {
            throw new IllegalArgumentException("Perspective may not be null");
        }
        Resource resource = new E4XMIResourceFactory().createResource(null);
        resource.getContents().add((EObject) persp);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            resource.save(output, null);
            return new String(output.toByteArray(), Plugin.ASCII_ENCODING);
        }
    }
}
