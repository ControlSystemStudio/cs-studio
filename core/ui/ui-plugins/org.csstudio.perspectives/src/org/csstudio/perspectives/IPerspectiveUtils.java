package org.csstudio.perspectives;

import java.io.IOException;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.emf.common.util.URI;

public interface IPerspectiveUtils {

    /**
     * Save a perspective object to a .xmi file.
     * @param persp Perspective object to save
     * @param file Filepath of file to save into
     * @throws IOException
     */
    public void savePerspective(MPerspective persp, URI file) throws IOException;

    /**
     * Convert an MPerspective object into an XMI XML string.
     * @param persp Perspective to convert.
     * @return XML string
     * @throws IOException
     * @throws IllegalArgumentException if perspective is null
     */
    public String perspectiveToString(MPerspective persp) throws IOException;

}
