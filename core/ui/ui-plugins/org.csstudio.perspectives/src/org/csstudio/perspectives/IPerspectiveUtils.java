package org.csstudio.perspectives;

import java.io.IOException;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.emf.common.util.URI;

public interface IPerspectiveUtils {

    public void savePerspective(MPerspective persp, URI file) throws IOException;

    public String perspToString(MPerspective persp) throws IOException;

}
