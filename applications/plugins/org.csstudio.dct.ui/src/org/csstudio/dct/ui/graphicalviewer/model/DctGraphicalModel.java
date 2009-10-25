package org.csstudio.dct.ui.graphicalviewer.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Project;

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
