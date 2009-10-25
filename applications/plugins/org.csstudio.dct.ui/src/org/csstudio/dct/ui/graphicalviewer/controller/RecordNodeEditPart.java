package org.csstudio.dct.ui.graphicalviewer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.UiExecutionService;
import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.dct.ui.graphicalviewer.view.ConnectionAnchor;
import org.csstudio.dct.ui.graphicalviewer.view.RecordFigure;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.RGB;
import org.epics.css.dal.Timestamp;

/**
 * Controller for {@link RecordNode}.
 * 
 * @author Sven Wende
 * 
 */
public class RecordNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener, IProcessVariableValueListener {

	private ConnectionAnchor anchorRight, anchorLeft, anchorTop, anchorBottom;

	private ChopboxAnchor anchorCenter;

	private List<ConnectionAnchor> outgoingAnchors, incomingAnchors;

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();

		getCastedModel().addPropertyChangeListener(this);

		IRecord record = getCastedModel().getElement();

		if (!record.isAbstract()) {
			try {
				String name = AliasResolutionUtil.getEpicsNameFromHierarchy(record);
				String resolvedName = ResolutionUtil.resolve(name, record);
				IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(resolvedName);
				ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService().register(this, pv, ValueType.DOUBLE);
			} catch (AliasResolutionException e) {
				CentralLogger.getInstance().error(this, e.getMessage());
			}
		}

	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void deactivate() {
		getCastedModel().removePropertyChangeListener(this);

		List l = getTargetConnections();
		for (int i = 0; i < l.size(); i++) {
			((EditPart) l.get(i)).deactivate();
		}
		super.deactivate();

	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		RecordFigure figure = new RecordFigure(getCastedModel().getCaption());

		outgoingAnchors = new ArrayList<ConnectionAnchor>();

		for (int i = 0; i < getCastedModel().getSourceConnections().size(); i++) {
			ConnectionAnchor a = new ConnectionAnchor(figure);
			a.offsetH = 70;
			a.offsetV = i * 5 + 3;
			outgoingAnchors.add(a);
		}

		incomingAnchors = new ArrayList<ConnectionAnchor>();

		for (int i = 0; i < getCastedModel().getTargetConnections().size(); i++) {
			ConnectionAnchor a = new ConnectionAnchor(figure);
			a.offsetH = 0;
			a.offsetV = i * 5 + 3;
			incomingAnchors.add(a);
		}

		anchorCenter = new ChopboxAnchor(figure);

		anchorLeft = new ConnectionAnchor(figure);

		anchorRight = new ConnectionAnchor(figure);
		anchorTop = new ConnectionAnchor(figure);
		anchorBottom = new ConnectionAnchor(figure);

		anchorLeft.topDown = false;
		anchorRight.topDown = false;
		anchorTop.topDown = false;
		anchorBottom.topDown = false;

		createOrUpdateAnchorsLocations();

		return figure;
	}

	/**
	 *{@inheritDoc}
	 */
	public AbstractConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectionEditpart) {
		Connection connection = (Connection) connectionEditpart.getModel();
		int index = getCastedModel().getSourceConnections().indexOf(connection);
		return anchorCenter;
	}

	/**
	 *{@inheritDoc}
	 */
	public AbstractConnectionAnchor getSourceConnectionAnchor(Request request) {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	public AbstractConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectionEditpart) {
		Connection connection = (Connection) connectionEditpart.getModel();
		int index = getCastedModel().getTargetConnections().indexOf(connection);
		return anchorCenter;
	}

	/**
	 *{@inheritDoc}
	 */
	public AbstractConnectionAnchor getTargetConnectionAnchor(Request request) {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected List getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected List getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	/**
	 *{@inheritDoc}
	 */
	public void connectionStateChanged(final ConnectionState connectionState) {
		final RecordFigure figure = (RecordFigure) getFigure();
		UiExecutionService.getInstance().queue(new Runnable() {
			public void run() {
				RGB rgb;

				switch (connectionState) {
				case CONNECTED:
					rgb = new RGB(0, 255, 0);
					break;
				case CONNECTION_LOST:
					rgb = new RGB(255, 0, 0);
					break;
				case DISCONNECTED:
					rgb = new RGB(255, 255, 0);
					break;
				case CONNECTION_FAILED:
					rgb = new RGB(255, 0, 255);
					break;
				default:
					rgb = new RGB(255, 255, 255);
					break;
				}

				figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(rgb));
				figure.setValue(connectionState.toString());
			}
		});

	}

	/**
	 *{@inheritDoc}
	 */
	public void errorOccured(final String error) {
		final RecordFigure figure = (RecordFigure) getFigure();
		if (error != null) {
			UiExecutionService.getInstance().queue(new Runnable() {
				public void run() {
					figure.setValue(error);
					figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(255, 0, 0));
				}
			});
		}

	}

	/**
	 *{@inheritDoc}
	 */
	public void valueChanged(final Object value, Timestamp timestamp) {
		final RecordFigure figure = (RecordFigure) getFigure();
		if (value != null) {
			UiExecutionService.getInstance().queue(new Runnable() {
				public void run() {
					figure.setValue(value.toString());
					figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(0, 255, 0));
				}
			});
		}
	}

	private RecordNode getCastedModel() {
		return (RecordNode) getModel();
	}

	private void createOrUpdateAnchorsLocations() {
		Dimension size = new Dimension(70, 30);
		
		anchorLeft.offsetH = 0;
		anchorLeft.offsetV = size.height / 2;

		anchorRight.offsetH = size.width;
		anchorRight.offsetV = size.height / 2;

		anchorTop.offsetH = size.width/2;
		anchorTop.offsetV = 0;

		anchorBottom.offsetH = size.width/2;
		anchorBottom.offsetV = size.height;
	}

}
