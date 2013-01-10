/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.commands.ConnectionDeleteCommand;
import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.editpolicies.ManhattanBendpointEditPolicy;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionFilter;

/**
 * Editpart for connections between widgets.
 * 
 * @author Xihui Chen
 * 
 */
public class WidgetConnectionEditPart extends AbstractConnectionEditPart {

	private ExecutionMode executionMode;

	private RotatableDecoration targetDecoration, sourceDecoration;

	/**
	 * The factor to calculate x from arrow length
	 */
	private static double X_FACTOR = 1 / Math.sqrt(1 + Math.pow(
			Math.tan(ConnectionModel.ARROW_ANGLE), 2));
	private static double Y_FACTOR = Math.tan(ConnectionModel.ARROW_ANGLE)
			* X_FACTOR;

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getWidgetModel().getProperty(ConnectionModel.PROP_LINE_COLOR)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							getConnectionFigure().setForegroundColor(
									((OPIColor) evt.getNewValue())
											.getSWTColor());
						}
					});

			getWidgetModel().getProperty(ConnectionModel.PROP_LINE_STYLE)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							getConnectionFigure().setLineStyle(
									getWidgetModel().getLineStyle());
						}
					});
			getWidgetModel().getProperty(ConnectionModel.PROP_LINE_WIDTH)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							getConnectionFigure().setLineWidth(
									getWidgetModel().getLineWidth());
						}
					});
			getWidgetModel().getProperty(ConnectionModel.PROP_ROUTER)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							updateRouter(getConnectionFigure());
						}
					});			
			
			getWidgetModel().getProperty(ConnectionModel.PROP_POINTS)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(final PropertyChangeEvent evt) {
							Runnable runnable = new Runnable() {
								
								@Override
								public void run() {
									if(((PointList)evt.getOldValue()).size() != 
											((PointList)evt.getNewValue()).size())
										updateRouter(getConnectionFigure());
									else
										refreshBendpoints(getConnectionFigure());
								}
							};
							//It should update at the same rate as other widget at run time
							if(getExecutionMode() == ExecutionMode.RUN_MODE){
								Display display = getViewer().getControl().getDisplay();
								WidgetIgnorableUITask task = new WidgetIgnorableUITask(
										getWidgetModel().getProperty(ConnectionModel.PROP_POINTS),
										runnable, display);
									
								GUIRefreshThread.getInstance(
										getExecutionMode() == ExecutionMode.RUN_MODE)
										.addIgnorableTask(task);
							}else
								runnable.run();
							
							
							
						}
					});

			getWidgetModel().getProperty(ConnectionModel.PROP_ARROW_LENGTH)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							updateArrowLength(getConnectionFigure());
						}
					});

			getWidgetModel().getProperty(ConnectionModel.PROP_ARROW_TYPE)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							updateDecoration(getConnectionFigure());
							updateArrowLength(getConnectionFigure());
						}
					});

			getWidgetModel().getProperty(ConnectionModel.PROP_FILL_ARROW)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							updateDecoration(getConnectionFigure());
							updateArrowLength(getConnectionFigure());
						}
					});
			getWidgetModel().getProperty(ConnectionModel.PROP_ANTIALIAS)
					.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							getConnectionFigure().setAntialias(
									getWidgetModel().isAntiAlias()? SWT.ON : SWT.OFF);
							for(Object obj : getConnectionFigure().getChildren()){
								if(obj instanceof Shape)
									((Shape)obj).setAntialias(
											getWidgetModel().isAntiAlias()? SWT.ON : SWT.OFF);
							}
						}
					});

		}
	}

	@Override
	protected void createEditPolicies() {
		if (getExecutionMode() == ExecutionMode.EDIT_MODE) {
			// Selection handle edit policy.
			// Makes the connection show a feedback, when selected by the user.
			installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
					new ConnectionEndpointEditPolicy() {
						private ConnectionRouter originalRouter = null;
						private Object originalConstraint = null;

						@Override
						protected void showConnectionMoveFeedback(
								ReconnectRequest request) {							
							if (getConnection().getConnectionRouter() instanceof FixedPointsConnectionRouter) {
								originalRouter = getConnection()
										.getConnectionRouter();
								originalConstraint = originalRouter.getConstraint(getConnection());
								getConnection().setConnectionRouter(
										new ManhattanConnectionRouter());
							}
							super.showConnectionMoveFeedback(request);
						}

						@Override
						protected void eraseConnectionMoveFeedback(
								ReconnectRequest request) {
							if(originalRouter != null){
								originalRouter.setConstraint(getConnection(), originalConstraint);
								getConnection().setConnectionRouter(originalRouter);
							}
							super.eraseConnectionMoveFeedback(request);
						}
					});
			// Allows the removal of the connection model element
			installEditPolicy(EditPolicy.CONNECTION_ROLE,
					new ConnectionEditPolicy() {
						protected Command getDeleteCommand(GroupRequest request) {
							return new ConnectionDeleteCommand(getWidgetModel());
						}
					});			
		}
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super
				.createFigure();
		connection.setLineStyle(getWidgetModel().getLineStyle());
		connection.setLineWidth(getWidgetModel().getLineWidth());
		connection.setForegroundColor(getWidgetModel().getLineColor()
				.getSWTColor());
		updateDecoration(connection);
		updateArrowLength(connection);
		updateRouter(connection);
		connection.setAntialias(getWidgetModel().isAntiAlias() ? SWT.ON
				: SWT.OFF);		
		return connection;
	}

	private void updateDecoration(PolylineConnection connection) {
		switch (getWidgetModel().getArrowType()) {
		case None:
			// if(targetDecoration != null)
			// connection.remove(targetDecoration);
			targetDecoration = null;
			// if(sourceDecoration != null)
			// connection.remove(sourceDecoration);
			sourceDecoration = null;
			break;
		case From:
			// if(targetDecoration != null)
			// connection.remove(targetDecoration);
			targetDecoration = null;
			if (getWidgetModel().isFillArrow())
				sourceDecoration = new PolygonDecoration();
			else
				sourceDecoration = new PolylineDecoration();
			break;
		case To:
			// if(sourceDecoration != null)
			// connection.remove(sourceDecoration);
			sourceDecoration = null;
			if (getWidgetModel().isFillArrow())
				targetDecoration = new PolygonDecoration();
			else
				targetDecoration = new PolylineDecoration();
			break;
		case Both:
			if (getWidgetModel().isFillArrow()) {
				sourceDecoration = new PolygonDecoration();
				targetDecoration = new PolygonDecoration();
			} else {
				sourceDecoration = new PolylineDecoration();
				targetDecoration = new PolylineDecoration();
			}
			break;
		default:
			break;
		}
		if(targetDecoration != null)
			((Shape)targetDecoration).setAntialias(
					getWidgetModel().isAntiAlias()? SWT.ON : SWT.OFF);
		if(sourceDecoration != null)
			((Shape)sourceDecoration).setAntialias(
					getWidgetModel().isAntiAlias()? SWT.ON : SWT.OFF);
		connection.setTargetDecoration(targetDecoration);
		connection.setSourceDecoration(sourceDecoration);
	}

	private void updateArrowLength(PolylineConnection connection) {
		int l = getWidgetModel().getArrowLength();
		if (sourceDecoration != null) {
			if (sourceDecoration instanceof PolygonDecoration)
				((PolygonDecoration) sourceDecoration).setScale(X_FACTOR * l,
						Y_FACTOR * l);
			else
				((PolylineDecoration) sourceDecoration).setScale(X_FACTOR * l,
						Y_FACTOR * l);
			sourceDecoration.repaint();
		}
		if (targetDecoration != null) {
			if (targetDecoration instanceof PolygonDecoration)
				((PolygonDecoration) targetDecoration).setScale(X_FACTOR * l,
						Y_FACTOR * l);
			else
				((PolylineDecoration) targetDecoration).setScale(X_FACTOR * l,
						Y_FACTOR * l);
			targetDecoration.repaint();
		}
		connection.revalidate();
	}

	private void updateRouter(PolylineConnection connection) {
		ConnectionRouter router = ConnectionRouter.NULL;
		switch (getWidgetModel().getRouterType()) {
		case MANHATTAN:		
			//Allow move bendpoint
			if(getExecutionMode()==ExecutionMode.EDIT_MODE)
				installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, 
					new ManhattanBendpointEditPolicy());
			//no points, use manhattan
			if(getWidgetModel().getPoints().size() == 0){
				router = new ManhattanConnectionRouter();
				break;
			}	
			//has points, use points for routing
			router = new FixedPointsConnectionRouter();			
			connection.setConnectionRouter(router);
			refreshBendpoints(connection);
			return;				
		case STRAIGHT_LINE:
			//no bendpoint
			if(getExecutionMode()==ExecutionMode.EDIT_MODE)
				installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, 
					null);
			router = ConnectionRouter.NULL;
		default:
			break;
		}
		connection.setConnectionRouter(router);
	}
	
	/**
	 * Updates the bendpoints, based on the model.
	 * @param connection 
	 */
	protected void refreshBendpoints(PolylineConnection connection) {
		//Only work for manhattan router
		if (!(connection.getConnectionRouter() 
				instanceof FixedPointsConnectionRouter))
			return;
		PointList points = getWidgetModel().getPoints().getCopy();
		if(points.size() ==0){
			points = connection.getPoints().getCopy();
			points.removePoint(0);
			points.removePoint(points.size()-1);
			getWidgetModel().setPoints(points);
		}
		connection.setRoutingConstraint(points);
		
	}

	public ConnectionModel getWidgetModel() {
		return (ConnectionModel) getModel();
	}

	public PolylineConnection getConnectionFigure() {
		return (PolylineConnection) getFigure();
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	/**
	 * Set execution mode, this should be set before widget is activated.
	 * 
	 * @param executionMode
	 *            the executionMode to set
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		getWidgetModel().setExecutionMode(executionMode);
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (key == IActionFilter.class)
			return new IActionFilter() {
				public boolean testAttribute(Object target, String name,
						String value) {
					if (name.equals("executionMode") && //$NON-NLS-1$
							value.equals("EDIT_MODE") && //$NON-NLS-1$
							getExecutionMode() == ExecutionMode.EDIT_MODE)
						return true;
					if (name.equals("executionMode") && //$NON-NLS-1$
							value.equals("RUN_MODE") && //$NON-NLS-1$
							getExecutionMode() == ExecutionMode.RUN_MODE)
						return true;					
					return false;
				}

			};
		return super.getAdapter(key);
	}

	public void setPropertyValue(String propID, Object value){
		getWidgetModel().setPropertyValue(propID, value);
	}
	
}
