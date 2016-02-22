package org.csstudio.utility.product;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class PerspectiveLoader {

    public static final String ASCII_ENCODING = "ascii";
    public static final String PERSPECTIVE_SUFFIX = "_e4persp";
    public static final String SELECT_PERSPECTIVE = "Choose a perspective file";
    public static final String XMI_EXTENSION = ".xmi";
    public static final String FILE_PREFIX = "file://";

    @Inject
    private EModelService modelService;

    @Inject
    @Preference(nodePath = "org.eclipse.ui.workbench")
    private IEclipsePreferences preferences;

    public static String perspToString(MPerspective persp) throws IOException {
        Resource resource = new E4XMIResourceFactory().createResource(null);
        resource.getContents().add((EObject) persp);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            resource.save(output, null);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resource.getContents().clear();
        return new String(output.toByteArray(), ASCII_ENCODING);
    }

    public void loadPerspectives() {
        FileDialog chooser = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        chooser.setText(SELECT_PERSPECTIVE);
        chooser.setFilterExtensions(new String[] {"*" + XMI_EXTENSION});
        chooser.open();
        File dirname = new File(chooser.getFilterPath());
        File fullPath = new File(dirname, chooser.getFileName());
        ResourceSet rs = new ResourceSetImpl();
        URI uri = URI.createURI(FILE_PREFIX + fullPath.getPath());
        Resource res = rs.getResource(uri, true);
        EObject obj = res.getContents().get(0);
        if (obj instanceof MPerspective) {
            MPerspective p = (MPerspective) obj;
            MPerspective pclone = (MPerspective) modelService.cloneElement(p,
                    null);
            try {
                String perspAsString = perspToString(pclone);
                // The new perspective import and export mechanism will intercept
                // this preference change and import the perspective for us.
                preferences.put(pclone.getLabel() + PERSPECTIVE_SUFFIX, perspAsString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
