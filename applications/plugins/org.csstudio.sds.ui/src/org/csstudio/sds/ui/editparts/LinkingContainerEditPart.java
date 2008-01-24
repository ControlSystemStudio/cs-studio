/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.editparts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.csstudio.sds.model.LinkingContainerModel;
import org.csstudio.sds.ui.figures.LinkingContainerFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * EditPart controller for the Snippet widget. The controller mediates between
 * {@link LinkingContainerModel} and {@link LinkingContainerFigure}.
 * 
 * @author Sven Wende
 * 
 */
public final class LinkingContainerEditPart extends AbstractContainerEditPart {

	/**
	 * Constructor.
	 */
	public LinkingContainerEditPart() {
		setChildrenSelectable(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public IFigure getContentPane() {
		return ((LinkingContainerFigure) getFigure()).getContentsPane();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		LinkingContainerFigure linkingContainerFigure = new LinkingContainerFigure();
		return linkingContainerFigure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				initContainerFromResource((IPath) newValue);
				return true;
			}

		};

		setPropertyChangeHandler(LinkingContainerModel.PROP_RESOURCE, handler);

		LinkingContainerModel m = (LinkingContainerModel) getContainerModel();
		initContainerFromResource(m.getResource());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {

			@Override
			protected Command createChangeConstraintCommand(
					final EditPart child, final Object constraint) {
				return null;
			}

			@Override
			protected Command getCreateCommand(final CreateRequest request) {
				return null;
			}

			@Override
			protected void showSizeOnDropFeedback(final CreateRequest request) {

			}

		});

		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);

	}

	/**
	 * Initializes the {@link ContainerModel} from the specified path.
	 * 
	 * @param path
	 *            The Path to the ContainerModel
	 */
	protected void initContainerFromResource(final IPath path) {
		final ContainerModel container = getContainerModel();

		if (path != null && !path.isEmpty()) {

			// IFile file = findFile(path);
			InputStream input = getInputStream(path);
			// if (file != null) {
			if (input != null) {
				final DisplayModel tempModel = new DisplayModel();

				PersistenceUtil.asyncFillModel(tempModel, input,
						new DisplayModelLoadAdapter() {
							public void onDisplayModelLoaded() {
								// remove old widgets
								Iterator<AbstractWidgetModel> it = container
										.getWidgets().iterator();
								while (it.hasNext()) {
									container.removeWidget(it.next());
								}

								// add new widgets
								it = tempModel.getWidgets().iterator();
								while (it.hasNext()) {
									AbstractWidgetModel w = it.next();
									tempModel.removeWidget(w);
									container.addWidget(w);
								}

								// update zoom
								new CheckedUiRunnable() {
									@Override
									protected void doRunInUi() {
										((LinkingContainerFigure) getFigure())
												.updateZoom();
									}
								};

							}
						});

			}

		}
	}
	
	/**
	 * Return the {@link InputStream} from the given path.
	 * 
	 * @param path
	 *            The {@link IPath} to the file
	 * @return The corresponding {@link InputStream}
	 */
	private InputStream getInputStream(final IPath path) {
		InputStream result = null;
		
		// try workspace
		IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path,
				false);
		if (r instanceof IFile) {
			try {
				result = ((IFile) r).getContents();
			} catch (CoreException e) {
				result = null;
			}
		}

		if (result == null) {
			// try from local file system
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}

		}

		return result;
	}
}
