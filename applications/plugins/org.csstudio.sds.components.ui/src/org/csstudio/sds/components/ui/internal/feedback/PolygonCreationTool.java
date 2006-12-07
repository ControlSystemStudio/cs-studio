package org.csstudio.sds.components.ui.internal.feedback;

import java.util.Date;

import org.csstudio.sds.components.internal.model.PolygonElement;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef.tools.TargetingTool;

/**
 * A custom creation tool for polygon elements.
 * 
 * The tool ...
 * 
 * @author Sven Wende
 *
 */

//TODO: swende: Klasse ist momentan noch in experimentellem Zustand!

public class PolygonCreationTool extends TargetingTool {
	/**
	 * Property to be used in {@link AbstractTool#setProperties(java.util.Map)}
	 * for {@link #setFactory(CreationFactory)}.
	 */
	public static final Object PROPERTY_CREATION_FACTORY = "factory"; //$NON-NLS-1$

	private CreationFactory factory;

	private SnapToHelper helper;

	/**
	 * Default constructor. Sets the default and disabled cursors.
	 */
	public PolygonCreationTool() {
		setDefaultCursor(SharedCursors.CURSOR_TREE_ADD);
		setDisabledCursor(Cursors.NO);
		setFactory(new CreationFactory() {

			public Object getNewObject() {
				PolygonElement polygon = new PolygonElement();
				PointList points = getCreateRequest().getPoints();
				
				polygon.setPoints(points);
				
				return polygon;
			}

			public Object getObjectType() {
				return PolygonElement.class;
			}

		});
	}

	/**
	 * Constructs a new CreationTool with the given factory.
	 * 
	 * @param aFactory
	 *            the creation factory
	 */
	public PolygonCreationTool(final CreationFactory aFactory) {
		this();
		setFactory(aFactory);
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#applyProperty(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	protected void applyProperty(final Object key, final Object value) {
		if (PROPERTY_CREATION_FACTORY.equals(key)) {
			if (value instanceof CreationFactory)
				setFactory((CreationFactory) value);
			return;
		}
		super.applyProperty(key, value);
	}

	/**
	 * Creates a {@link CreateRequest} and sets this tool's factory on the
	 * request.
	 * 
	 * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
	 */
	@Override
	protected Request createTargetRequest() {
		PolygonRequest request = new PolygonRequest();
		request.setFactory(getFactory());
		return request;
	}

	/**
	 * @see org.eclipse.gef.Tool#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		helper = null;
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
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
	protected PolygonRequest getCreateRequest() {
		return (PolygonRequest) getTargetRequest();
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
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
		return factory;
	}

	long lastClick = 0;

	/**
	 * The creation tool only works by clicking mouse button 1 (the left mouse
	 * button in a right-handed world). If any other button is pressed, the tool
	 * goes into an invalid state. Otherwise, it goes into the drag state,
	 * updates the request's location and calls
	 * {@link TargetingTool#lockTargetEditPart(EditPart)} with the edit part
	 * that was just clicked on.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonDown(int)
	 */
	@Override
	protected boolean handleButtonDown(final int button) {
		if (button != 1) {
			setState(STATE_INVALID);

			// handleInvalidInput();
			return true;
		}
		setState(STATE_DRAG);

		long now = new Date().getTime();
		if (now - lastClick < 200) {
			System.out.println("DOUBLECLICK");
			if (stateTransition(STATE_DRAG | STATE_DRAG_IN_PROGRESS,
					STATE_TERMINAL)) {
				eraseTargetFeedback();
				unlockTargetEditPart();
				performCreation(button);
			}

			setState(STATE_TERMINAL);
			handleFinished();
		} else {
			getCreateRequest().addPoint(getLocation());
		}
		
		
		lastClick = now;
		// getCreateRequest().setLocation(getLocation());
		// lockTargetEditPart(getTargetEditPart());
		// Snap only when size on drop is employed
		// if (getTargetEditPart() != null)
		// helper = (SnapToHelper) getTargetEditPart().getAdapter(
		// SnapToHelper.class);
		// }
		return true;
	}

	/**
	 * If the tool is currently in a drag or drag-in-progress state, it goes
	 * into the terminal state, performs some cleanup (erasing feedback,
	 * unlocking target edit part), and then calls {@link #performCreation(int)}.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
	 */
	@Override
	protected boolean handleButtonUp(final int button) {
		// add point (HACK)
		getCreateRequest().addPoint(getLocation());

//		if (stateTransition(STATE_DRAG | STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
//			eraseTargetFeedback();
//			unlockTargetEditPart();
//			performCreation(button);
//		}
//
//		setState(STATE_TERMINAL);
//		handleFinished();
		return true;
	}

	/**
	 * Updates the request, sets the current command, and asks to show feedback.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleDragInProgress()
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
	 * @see org.eclipse.gef.tools.AbstractTool#handleDragStarted()
	 */
	@Override
	protected boolean handleDragStarted() {
		return stateTransition(STATE_DRAG, STATE_DRAG_IN_PROGRESS);
	}

	/**
	 * If the user is in the middle of creating a new edit part, the tool erases
	 * feedback and goes into the invalid state when focus is lost.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleFocusLost()
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

	@Override
	protected void showTargetFeedback() {
		// TODO Auto-generated method stub
		super.showTargetFeedback();
	}

	/**
	 * @see org.eclipse.gef.tools.TargetingTool#handleHover()
	 */
	@Override
	protected boolean handleHover() {
		if (isInState(STATE_INITIAL))
			updateAutoexposeHelper();
		return true;
	}

	/**
	 * Updates the request and mouse target, gets the current command and asks
	 * to show feedback.
	 * 
	 * @see org.eclipse.gef.tools.AbstractTool#handleMove()
	 */
	@Override
	protected boolean handleMove() {
		// System.out.println("Location" + getLocation());
		// // add point (HACK)
		PointList points = getCreateRequest().getPoints();
		if (points.size() > 0) {
			points.setPoint(getLocation(), points.size() - 1);
		}

		updateTargetRequest();
		updateTargetUnderMouse();
		setCurrentCommand(getCommand());
		showTargetFeedback();

		return true;
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

	@Override
	protected void setCurrentCommand(final Command c) {
		// TODO Auto-generated method stub
		super.setCurrentCommand(c);
	}

	/*
	 * Add the newly created object to the viewer's selected objects.
	 */
	private void selectAddedObject(final EditPartViewer viewer) {
		final Object model = getCreateRequest().getNewObject();
		if (model == null || viewer == null)
			return;
		Object editpart = viewer.getEditPartRegistry().get(model);
		if (editpart instanceof EditPart) {
			// Force the new object to get positioned in the viewer.
			viewer.flush();
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
		this.factory = factory;
	}

	/**
	 * Sets the location (and size if the user is performing size-on-drop) of
	 * the request.
	 * 
	 * @see org.eclipse.gef.tools.TargetingTool#updateTargetRequest()
	 */
	@Override
	protected void updateTargetRequest() {
		PolygonRequest req = getCreateRequest();
		if (isInState(STATE_DRAG_IN_PROGRESS)) {
//			Point loq = getStartLocation();
//			Rectangle bounds = new Rectangle(loq, loq);
//			bounds.union(loq.getTranslated(getDragMoveDelta()));
			Rectangle bounds = req.getPoints().getBounds();
			req.setSize(bounds.getSize());
			req.setLocation(bounds.getLocation());
			req.getExtendedData().clear();
			if (!getCurrentInput().isAltKeyDown() && helper != null) {
				PrecisionRectangle baseRect = new PrecisionRectangle(bounds);
				PrecisionRectangle result = baseRect.getPreciseCopy();
				helper.snapRectangle(req, PositionConstants.NSEW, baseRect,
						result);
				req.setLocation(result.getLocation());
				req.setSize(result.getSize());
			}
		} else {
			req.setSize(null);
			req.setLocation(getLocation());
		}
	}

	public class PolygonRequest extends CreateRequest {
		private PointList _points = new PointList();

		public void addPoint(final Point p) {
			_points.addPoint(p);
		}

		public PointList getPoints() {
			return _points;
		}
	}
}
