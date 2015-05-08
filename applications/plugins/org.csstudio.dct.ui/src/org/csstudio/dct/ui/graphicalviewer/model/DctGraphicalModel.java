package org.csstudio.dct.ui.graphicalviewer.model;

import org.csstudio.dct.model.IProject;

/**
 * The root of the graphical dct model.
 *
 * @author Sven Wende
 *
 */
public class DctGraphicalModel extends AbstractContainerNode<IProject> {

    /**
     * Standard constructor.
     *
     * @param project
     *            the dct project that is represented graphically
     */
    public DctGraphicalModel(IProject project) {
        super(project);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetCaption(IProject project) {
        return project.getName();
    }

}
