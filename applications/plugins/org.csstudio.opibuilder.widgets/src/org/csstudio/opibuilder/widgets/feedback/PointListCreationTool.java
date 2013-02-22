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
package org.csstudio.opibuilder.widgets.feedback;

import java.util.ArrayList;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.TargetingTool;

/**
 * A custom creation tool for PointList dependend widgets. The tool produces a
 * point list, by interpreting each left click as location for a new point.
 * 
 * @author Sven Wende, Xihui Chen
 * 
 */

public final class PointListCreationTool extends TargetingTool {
	/**
	 * Property to be used in AbstractTool#setProperties(java.util.Map) for
	 * {@link #setFactory(CreationFactory)}.
	 */
	public static final Object PROPERTY_CREATION_FACTORY = "factory"; //$NON-NLS-1$


	/**
	 * The creation factory.
	 */
	private CreationFactory _factory;

	/**
	 * The point list, which is manipulated by this tool.
	 */
	private PointList _points = new PointList();
	
	/**
	 * List of common EditPart.
	 *
	 * This maintains the list of common EditParts where all
	 * the points belong to. The first element of this
	 * list is the descendant EditPart where all points belong
	 * to. The second element is the parent of the first, and
	 * the third element is the parent of the second, and so
	 * on. The last element of this list is the root EditPart. 
	 */
	private ArrayList<EditPart> commonEditParts = null;

	/**
	 * A SnapToHelper.
	 */
	private SnapToHelper _snap2Helper;

	/**
	 * Default constructor.
	 */
	public PointListCreationTool() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyProperty(final Object key, final Object value) {
		if (PROPERTY_CREATION_FACTORY.equals(key)) {
			if (value instanceof CreationFactory) {
				setFactory((CreationFactory) value);
			}
			return;
		}
		super.applyProperty(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Request createTargetRequest() {
		_points = new PointList();
		CreateRequest request = new CreateRequest();
		request.setFactory(getFactory());
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		_snap2Helper = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getCommandName() {
		return REQ_CREATE;
	}

	/**
	 * Cast the target request to a CreateRequest and returns it.
	 * 
	 * @return the target request as a CreateRequest
	 * @see TargetingTool#getTargetRequest()
	 */
	protected CreateRequest getCreateRequest() {

		return (CreateRequest) getTargetRequest();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDebugName() {
		return "Creation Tool";//$NON-NLS-1$
	}

	/**
	 * Returns the creation factory used to create the new EditParts.
	 * 
	 * @return the creation factory
	 */
	protected CreationFactory getFactory() {
		return _factory;
	}

	@Override
	protected boolean handleDoubleClick(int button) {
		// only react on left clicks
		if (button != 1) {
			setState(STATE_INVALID);
			return true;
		}
		// remove the last point, which was just created for previewing the
		// next axis
		_points.removePoint(_points.size() - 1);

		// perform creation of the material
		if (stateTransition(STATE_DRAG | STATE_DRAG_IN_PROGRESS,
				STATE_TERMINAL)) {
			eraseTargetFeedback();
			unlockTargetEditPart();
			performCreation(button);
		}
		// terminate
		setState(STATE_TERMINAL);
		handleFinished();
		updateTargetRequest();
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleButtonDown(final int button) {
		if (getTargetEditPart() != null) {
			_snap2Helper = (SnapToHelper) getTargetEditPart().getAdapter(
					SnapToHelper.class);
		}

		// only react on left clicks
		if (button != 1) {
			setState(STATE_INVALID);
			return true;
		}

		// the tool is in progress mode, until a doubleclick occurs
		setState(STATE_DRAG_IN_PROGRESS);

			// handle clicks
		
			Point p = getSnapedLocation();
			if (_points.size() == 0) {
				// add a new point
				_points.addPoint(p);
			} else {
				// override the last point, which was the "preview" point before
				_points.setPoint(p, _points.size() - 1);

			}
			// add an additional point, which is just for previewing the next
			// axis in the graphical feedback
			_points.addPoint(p);
			
			if (commonEditParts == null) {
				// This is the first point. Register all ancestors to the list.
				commonEditParts = new ArrayList<EditPart>();
				EditPart ep = getTargetEditPart();
				while (ep != null) {
					commonEditParts.add(ep);
					ep = ep.getParent();
				}
			} else {
				// Remove all EditParts which the added point does not belong to.
				int index = commonEditParts.indexOf(getTargetEditPart());
				if (index == -1) {
					commonEditParts.clear();
				} else {
					for (int i=0; i<index; i++) {
						commonEditParts.remove(i);
					}
				}
			}

		updateTargetRequest();
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleButtonUp(final int button) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleDragInProgress() {
		if (isInState(STATE_DRAG_IN_PROGRESS)) {
			updateTargetRequest();
			setCurrentCommand(getCommand());
			showTargetFeedback();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleDragStarted() {
		return stateTransition(STATE_DRAG, STATE_DRAG_IN_PROGRESS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleFocusLost() {
		if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) {
			eraseTargetFeedback();
			setState(STATE_INVALID);
			handleFinished();
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleHover() {
		if (isInState(STATE_INITIAL)) {
			updateAutoexposeHelper();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleMove() {
		if (getState() != STATE_TERMINAL && getState() != STATE_INVALID) {
			if (_points.size() > 0) {
				// snap
				PrecisionPoint location = getSnapedLocation();

				// update the last point in the list to update the graphical
				// feedback
				_points.setPoint(location, _points.size() - 1);
			}

			updateTargetRequest();
			updateTargetUnderMouse();
			setCurrentCommand(getCommand());
			showTargetFeedback();
			return true;
		}
		return false;
	}

	/**
	 * Gets the "snapped" location based on the current location of the mouse.
	 * 
	 * @return the point of the snapped location
	 */
	private PrecisionPoint getSnapedLocation() {
		CreateRequest req = getCreateRequest();
		PrecisionPoint location = new PrecisionPoint(getLocation().x,
				getLocation().y);

		if (_snap2Helper != null) {
			_snap2Helper.snapPoint(req, PositionConstants.NORTH_WEST,
					new PrecisionPoint(getLocation().x, getLocation().y),
					location);
		}
		return location;
	}

	/**
	 * Executes the current command and selects the newly created object. The
	 * button that was released to cause this creation is passed in, but since
	 * {@link #handleButtonDown(int)} goes into the invalid state if the button
	 * pressed is not button 1, this will always be button 1.
	 * 
	 * @param button
	 *            the button that was pressed
	 */
	protected void performCreation(final int button) {
		EditPartViewer viewer = getCurrentViewer();
		executeCurrentCommand();
		selectAddedObject(viewer);
	}

	/**
	 * Add the newly created object to the viewer's selected objects.
	 * 
	 * @param viewer
	 *            the EditPartViewer
	 */
	private void selectAddedObject(final EditPartViewer viewer) {
		final Object model = getCreateRequest().getNewObject();
		if (model == null || viewer == null) {
			return;
		}
		Object editpart = viewer.getEditPartRegistry().get(model);
		if (editpart instanceof EditPart) {
			// Force the new object to get positioned in the viewer.
			viewer.flush();
			if(((EditPart) editpart).isSelectable())
				viewer.select((EditPart) editpart);
		}
	}

	/**
	 * Sets the creation factory used to create the new edit parts.
	 * 
	 * @param factory
	 *            the factory
	 */
	public void setFactory(final CreationFactory factory) {
		_factory = factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void updateTargetRequest() {
		CreateRequest req = getCreateRequest();

		if (isInState(STATE_DRAG_IN_PROGRESS)) {
			// use the rectangle, which is defined by the point lists as new
			// bounds
			Rectangle bounds = _points.getBounds();
			req.setSize(bounds.getSize());
			req.setLocation(bounds.getLocation());
			req.getExtendedData().put(AbstractPolyFeedbackFactory.PROP_POINTS,
					_points);
			// req.getExtendedData().clear();
			if (!getCurrentInput().isAltKeyDown() && _snap2Helper != null) {
				PrecisionRectangle baseRect = new PrecisionRectangle(bounds);
				PrecisionRectangle result = baseRect.getPreciseCopy();
				_snap2Helper.snapRectangle(req, PositionConstants.NSEW,
						baseRect, result);
				req.setLocation(result.getLocation());
				req.setSize(result.getSize());
			}
		} else {
			req.setSize(null);
			req.setLocation(getLocation());
		}
	}
	
	@Override
	protected EditPartViewer.Conditional getTargetingConditional() {
		return new EditPartViewer.Conditional() {
			public boolean evaluate(EditPart editpart) {
				EditPart targetEditPart = editpart.getTargetEditPart(getTargetRequest());
				if (targetEditPart == null)
					return false;
				
				// If there is no point, the EditPart under the mouse is the target. 
				if (commonEditParts == null)
					return true;

				// If the EditPart under the mouse is not listed in the list of EditPart,
				// it cannot be the target.
				return commonEditParts.contains(targetEditPart);
			}
		};
	}
}
