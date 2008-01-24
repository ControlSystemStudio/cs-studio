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
package org.csstudio.sds.model;

/**
 * A connection between two distinct variables.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ConnectionElement extends AbstractWidgetModel {
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "element.label"; //$NON-NLS-1$

	/**
	 * True, if the connection is attached to its endpoints.
	 */
	private boolean _isConnected;

	/**
	 * Line drawing style for this connection.
	 */
	private int _lineStyle = 1;

	/**
	 * Connection's source element.
	 */
	private AbstractWidgetModel _sourceModel;

	/**
	 * Connection's target element.
	 */
	private AbstractWidgetModel _targetModel;

	/**
	 * Create a (solid) connection between two distinct variables.
	 * 
	 * @param source
	 *            a source variable for this connection (non null)
	 * @param target
	 *            a target variable for this connection (non null)
	 */
	public ConnectionElement(final AbstractWidgetModel source,
			final AbstractWidgetModel target) {
		assert source != null : "source!=null"; //$NON-NLS-1$
		assert target != null : "target!=null"; //$NON-NLS-1$

		reconnect(source, target);
	}

	/**
	 * Disconnect this connection from the variables it is attached to.
	 */
	public void disconnect() {
		if (_isConnected) {
			_sourceModel.removeConnection(this);
			_targetModel.removeConnection(this);
			_isConnected = false;
		}
	}

	/**
	 * Returns the line drawing style of this connection.
	 * 
	 * @return an int value (Graphics.LINE_DASH or Graphics.LINE_SOLID)
	 */
	public int getLineStyle() {
		return _lineStyle;
	}

	/**
	 * Inverts the connection.
	 * 
	 */
	public void invert() {
		disconnect();
		AbstractWidgetModel tmp = _sourceModel;
		_sourceModel = _targetModel;
		_targetModel = tmp;
		reconnect();
	}

	/**
	 * Returns the source variable of this connection.
	 * 
	 * @return a non-null Alias instance
	 */
	public AbstractWidgetModel getSourceModel() {
		return _sourceModel;
	}

	/**
	 * Returns the target variable of this connection.
	 * 
	 * @return a non-null Alias instance
	 */
	public AbstractWidgetModel getTargetModel() {
		return _targetModel;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the
	 * variables it was previously attached to.
	 */
	public void reconnect() {
		if (!_isConnected) {
			_sourceModel.addConnection(this);
			_targetModel.addConnection(this);
			_isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target variable. The connection
	 * will disconnect from its current attachments and reconnect to the new
	 * source and target variables.
	 * 
	 * @param newSource
	 *            a new source variable for this connection (non null)
	 * @param newTarget
	 *            a new target variable for this connection (non null)
	 */
	public void reconnect(final AbstractWidgetModel newSource,
			final AbstractWidgetModel newTarget) {
		if ((newSource == null) || (newTarget == null)
				|| (newSource == newTarget)) {
			throw new IllegalArgumentException();
		}
		disconnect();
		_sourceModel = newSource;
		_targetModel = newTarget;
		reconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		try {
			ConnectionElement con = (ConnectionElement) obj;

			if ((con.getSourceModel() == _sourceModel)
					&& (con.getTargetModel() == _targetModel)) {
				result = true;
			}
		} catch (ClassCastException cce) {
			result = false;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return null;
	}

	/**
	 * Return whether this connection element is currently connected.
	 * 
	 * @return True, if this connection element is currently connected.
	 */
	public boolean isConnected() {
		return _isConnected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		return "";
	}

}
