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
package org.csstudio.sds.components.ui.internal.editparts;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import org.csstudio.sds.components.model.LinkingContainerModel;
import org.csstudio.sds.components.ui.internal.figures.LinkingContainerFigure;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.internal.connection.ConnectionServicesManager;
import org.csstudio.sds.internal.connection.ConnectionUtil;
import org.csstudio.sds.internal.connection.SdsException;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.persistence.DisplayModelReader;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.widgets.Display;

/**
 * EditPart controller for the Snippet widget. The controller mediates between
 * {@link LinkingContainerModel} and {@link LinkingContainerFigure}.
 * 
 * @author Sven Wende
 * 
 */
public final class LinkingContainerEditPart extends AbstractContainerEditPart {
	public LinkingContainerEditPart() {
		setChildrenSelectable(false);
	}

	public IFigure getContentPane() {
		return ((LinkingContainerFigure) getFigure()).getContentsPane();
	}

	@Override
	protected IFigure doCreateFigure() {
		return new LinkingContainerFigure();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {
				initContainerFromResource((IPath) newValue);
				return true;
			}

		};

		setPropertyChangeHandler(LinkingContainerModel.PROP_RESOURCE, handler);

		LinkingContainerModel m = (LinkingContainerModel) getContainerModel();
		initContainerFromResource(m.getResource());
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new XYLayoutEditPolicy() {

			@Override
			protected Command createChangeConstraintCommand(EditPart child,
					Object constraint) {
				return null;
			}

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				return null;
			}

			@Override
			protected void showSizeOnDropFeedback(CreateRequest request) {

			}

		});

		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);

	}

	@Override
	public synchronized void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
	}

	protected void initContainerFromResource(IPath path) {
		ContainerModel container = getContainerModel();

		if (path != null && !path.isEmpty()) {

			IFile file = findFile(path);

			if (file != null) {
				DisplayModel tempModel = new DisplayModel();

				try {
					DisplayModelReader.getInstance()
							.readModelFromXml(file.getContents(), tempModel,
									Display.getCurrent());
				} catch (CoreException e) {
					e.printStackTrace();
				}

				// remove old widgets
				Iterator<AbstractWidgetModel> it = container.getWidgets()
						.iterator();
				while (it.hasNext()) {
					container.removeWidget(it.next());
				}

				// add new widgets
				for (AbstractWidgetModel w : tempModel.getWidgets()) {
					container.addWidget(w);
				}
				
				((LinkingContainerFigure) getFigure()).updateZoom();
			}

		}
	}

	private IFile findFile(IPath location) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);

		if (file == null) {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
					.findFilesForLocation(location);

			if (files != null && files.length > 0) {
				file = files[0];
			}
		}

		return file;
	}
}
