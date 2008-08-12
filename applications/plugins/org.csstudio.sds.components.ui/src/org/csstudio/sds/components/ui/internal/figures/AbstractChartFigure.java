/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Figure;

/**
 * Base class for widgets that implement a chart (waveform, strip chart).
 * 
 * @author Joerg Rathlev
 * @author based on waveform by Kai Meyer and Sven Wende
 */
public abstract class AbstractChartFigure extends Figure implements IAdaptable {

	/**
	 * Constant value which represents that a scale or grid lines should be
	 * shown for the x-axis.
	 */
	private static final int SHOW_X_AXIS = 1;

	/**
	 * Constant value which represents that a scale or grid lines should be
	 * shown for the y-axis.
	 */
	private static final int SHOW_Y_AXIS = 2;

	/**
	 * Constant value which represents that a scale or grid lines should be
	 * shown for both axes.
	 */
	private static final int SHOW_BOTH = 3;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * Whether this figure has a transparent background.
	 */
	private boolean _transparent;

	/**
	 * The axes for which grid lines are drawn.
	 * @see #SHOW_X_AXIS
	 * @see #SHOW_Y_AXIS
	 * @see #SHOW_BOTH
	 */
	private int _showGridLines = 0;

	/**
	 * Which axes are shown.
	 * @see #SHOW_X_AXIS
	 * @see #SHOW_Y_AXIS
	 * @see #SHOW_BOTH
	 */
	private int _showAxes = 0;

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
	
	/**
	 * Sets the transparent state of the background.
	 * 
	 * @param transparent
	 *            the transparent state.
	 */
	public final void setTransparent(final boolean transparent) {
		_transparent = transparent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean isOpaque() {
		return !_transparent;
	}

	/**
	 * Sets which axes should be displayed.
	 * 
	 * @param axes
	 *            a value representing which axes should be displayed.
	 * @see #SHOW_X_AXIS
	 * @see #SHOW_Y_AXIS
	 * @see #SHOW_BOTH
	 */
	public final void setShowScale(final int axes) {
		_showAxes = axes;
		refreshConstraints();
	}

	/**
	 * Sets the axes for which grid lines should be displayed.
	 * 
	 * @param axes
	 *            a value representing for which axes grid lines should be
	 *            displayed.
	 * @see #SHOW_X_AXIS
	 * @see #SHOW_Y_AXIS
	 * @see #SHOW_BOTH
	 */
	public final void setShowGridLines(final int axes) {
		_showGridLines = axes;
		refreshConstraints();
	}

	/**
	 * Checks whether the x-axis is displayed.
	 * 
	 * @return <code>true</code> if the x-axis is displayed,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean showXAxis() {
		return (_showAxes == SHOW_X_AXIS || _showAxes == SHOW_BOTH);
	}

	/**
	 * Checks whether the y-axis is displayed.
	 * 
	 * @return <code>true</code> if the y-axis is displayed,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean showYAxis() {
		return (_showAxes == SHOW_Y_AXIS || _showAxes == SHOW_BOTH);
	}

	/**
	 * Checks whether gridlines are displayed for the x-axis.
	 * 
	 * @return <code>true</code> if gridlines are displayed,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean showXAxisGrid() {
		return (_showGridLines == SHOW_X_AXIS || _showGridLines == SHOW_BOTH);
	}

	/**
	 * Checks whether gridlines are displayed for the y-axis.
	 * 
	 * @return <code>true</code> if gridlines are displayed,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean showYAxisGrid() {
		return (_showGridLines == SHOW_Y_AXIS || _showGridLines == SHOW_BOTH);
	}
	
	/**
	 * Performs the layout of the subfigures of this figure.
	 */
	private final void refreshConstraints() {
		// TODO
	}

}
