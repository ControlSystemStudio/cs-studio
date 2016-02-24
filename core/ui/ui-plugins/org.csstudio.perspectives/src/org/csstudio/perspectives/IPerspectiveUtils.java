package org.csstudio.perspectives;

import java.io.IOException;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.emf.common.util.URI;

public interface IPerspectiveUtils {

    public static final String ASCII_ENCODING = "ascii";
    public static final String XMI_EXTENSION = ".xmi";
    public static final String FILE_PREFIX = "file://";
    public static final String PERSPECTIVE_SUFFIX = "_e4persp";

    public void savePerspective(MPerspective persp, URI file) throws IOException;

    public String perspToString(MPerspective persp) throws IOException;

}
