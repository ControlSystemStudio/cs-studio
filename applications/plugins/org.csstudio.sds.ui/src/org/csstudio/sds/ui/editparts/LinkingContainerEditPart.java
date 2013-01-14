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
import java.util.List;

import org.csstudio.sds.internal.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.LinkingContainerModel;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.figures.LinkingContainerFigure;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.ui.progress.IJobRunnable;

/**
 * Controller for the linking container widget. The controller mediates between
 * {@link LinkingContainerModel} and {@link LinkingContainerFigure}.
 *
 * @author Sven Wende
 *
 */
public final class LinkingContainerEditPart extends AbstractContainerEditPart {
	private IProgressMonitor _runningMonitor;

	/**
	 * Constructor.
	 */
	public LinkingContainerEditPart() {
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
		LinkingContainerModel widget = (LinkingContainerModel) getContainerModel();

		LinkingContainerFigure linkingContainerFigure = new LinkingContainerFigure();
		linkingContainerFigure.setAutoFit(widget.isAutoZoom());
		
		return linkingContainerFigure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				loadResource((IPath) newValue, (LinkingContainerFigure) figure);
				return true;
			}

		};

		setPropertyChangeHandler(LinkingContainerModel.PROP_RESOURCE, handler);

		LinkingContainerModel m = (LinkingContainerModel) getContainerModel();
		if(!m.isResourceLoaded()) {
			loadResource(m.getResource(), (LinkingContainerFigure) getFigure());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshChildren() {
		super.refreshChildren();

		// we need to ensure the correct zoom level, when figures are added or removed
		((LinkingContainerFigure) getFigure()).updateZoom();
	}

	private void loadResource(final IPath resource,
			final LinkingContainerFigure figure) {
		if (_runningMonitor != null) {
			_runningMonitor.setCanceled(true);
		}

		_runningMonitor = new NullProgressMonitor();

		ContainerLoadJob job = new ContainerLoadJob(
				(LinkingContainerModel) getContainerModel(), resource, figure);

		job.run(_runningMonitor);

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

	private class ContainerLoadJob implements IJobRunnable {
		private final LinkingContainerModel _container;
		private final IPath _path;
		private final LinkingContainerFigure _figure;

		public ContainerLoadJob(final LinkingContainerModel container, final IPath path,
				final LinkingContainerFigure figure) {
			assert container != null;
			assert path != null;
			assert figure != null;
			_container = container;
			_path = path;
			_figure = figure;
		}

		public IStatus run(final IProgressMonitor progressMonitor) {
			IStatus status = Status.OK_STATUS;

			if ((_path != null) && !_path.isEmpty()) {
				// display a temporary message + cancel button while the display
				// is loading

				if (!progressMonitor.isCanceled()) {
					//showMessage(progressMonitor, "Loading " + _path.toString());

					if (!progressMonitor.isCanceled()) {
						load(progressMonitor);
					} else {
						status = Status.CANCEL_STATUS;
					}
				} else {
					status = Status.CANCEL_STATUS;
				}
			}

			return status;
		}

		protected IStatus clearContainer(final IProgressMonitor progressMonitor) {
			// remove old widgets
			Iterator<AbstractWidgetModel> it = _container.getWidgets()
					.iterator();
			while (it.hasNext()) {
				_container.removeWidget(it.next());
			}
			_container.setResourceLoaded(false);

			updateZoom();

			return Status.OK_STATUS;
		}

		protected void updateZoom() {
			new CheckedUiRunnable() {
				@Override
				protected void doRunInUi() {
					_figure.updateZoom();
				}
			};
		}

		protected void showMessage(final IProgressMonitor progressMonitor,
				final String message) {
			// clear the container
			clearContainer(progressMonitor);

			// add a temporary widget
			final LabelModel loadingMessage = new LabelModel();
			loadingMessage.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#0000C8");
			loadingMessage.setTextValue(message);
			loadingMessage.setLocation(0, 0);
			int w = _container.getWidth();
			loadingMessage.setWidth(w);
			int h = _container.getHeight();
			loadingMessage.setHeight(h);
			_container.addWidget(loadingMessage);
		}

		/**
		 * Initializes the {@link ContainerModel} from the specified path.
		 *
		 * @param path
		 *            The Path to the ContainerModel
		 * @throws Exception
		 */
		protected void load(final IProgressMonitor progressMonitor) {
			InputStream input = getInputStream(_path);

			if (input == null) {
				showMessage(progressMonitor, "Could not load display: "
						+ _path.toPortableString());
			} else {
				final DisplayModel tempModel = new DisplayModel();

				PersistenceUtil.asyncFillModel(tempModel, input,
						new DisplayModelLoadAdapter() {

							@Override
							public void onErrorsOccured(final List<String> errors) {
								showMessage(progressMonitor, "Error occured: "
										+ errors.get(0));
							}

							public void onDisplayModelLoaded() {
								// remove old widgets
								_container.removeWidgets(_container.getWidgets());


								// add new widgets
								List<AbstractWidgetModel> widgets = tempModel.getWidgets();

								tempModel.removeWidgets(widgets);
								_container.addWidgets(widgets);

								// update zoom
								if (!progressMonitor.isCanceled()) {
									updateZoom();
								}

								// use background-color of the loaded display
								_container.setColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND, tempModel.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
								_container.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, tempModel.getColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
								// _container.setAliases(tempModel.getAliases());
								_container
										.setPrimarPv(tempModel.getPrimaryPV());

								_container.setResourceLoaded(true);
							}
						});

			}
		}

		/**
		 * Return the {@link InputStream} of the file that is available on the
		 * specified path.
		 *
		 * @param path
		 *            The {@link IPath} to the file
		 *
		 * @return The corresponding {@link InputStream} or null
		 */
		private InputStream getInputStream(final IPath path) {
			InputStream result = null;

			// try workspace
			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
					path, false);
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

	@Override
	protected boolean determineChildrenSelectability() {
		return false;
	}
}
