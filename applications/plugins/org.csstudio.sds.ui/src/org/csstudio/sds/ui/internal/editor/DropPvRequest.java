package org.csstudio.sds.ui.internal.editor;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.DropRequest;

/**
 * A Request representing a drop of a PV.
 * 
 * @author Sven Wende, Kai Meyer
 */
public final class DropPvRequest extends org.eclipse.gef.Request implements
		DropRequest {
	/**
	 * The identifier of the type.
	 */
	public static final String REQ_DROP_PV = "REQ_DROP_PV";
	/**
	 * The location of the drop.
	 */
	private Point _location;
	/**
	 * The dropped ProcessVariable-Name.
	 */
	private IProcessVariableAddress _pv;

	/**
	 * Constructor.
	 */
	public DropPvRequest() {
		setType(REQ_DROP_PV);
	}

	/**
	 * Sets the location of the drop.
	 * 
	 * @param location
	 *            the location of the drop
	 */
	public void setLocation(final Point location) {
		_location = location;
	}

	/**
	 * Returns the location of the drop.
	 * @return Point
	 * 			The location of the drop.
	 */
	public Point getLocation() {
		return _location;
	}

	/**
	 * Sets the name of the ProcessVariable.
	 * @param pv
	 * 			The name of the ProcessVariable
	 */
	public void setProcessVariableAddress(final IProcessVariableAddress pv) {
		_pv = pv;
	}

	/**
	 * Returns the Name of the ProcessVariable.
	 * @return String
	 * 			The name of the PV.
	 */
	public IProcessVariableAddress getProcessVariableAddress() {
		return _pv;
	}

}
