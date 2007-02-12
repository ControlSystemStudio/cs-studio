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
package org.csstudio.sds.components.ui.internal.figures;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.ScrollBar;

/**
 * A slider figure.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class SliderFigure extends Panel implements IRefreshableFigure {
	/**
	 * Definition of listeners that react on slider events.
	 * 
	 * @author Sven Wende
	 * @version $Revision$
	 * 
	 */
	public interface ISliderListener {
		/**
		 * React on a slider event.
		 * 
		 * @param newValue
		 *            The new slider value.
		 */
		void sliderValueChanged(int newValue);
	}

	/**
	 * Listeners that react on slider events.
	 */
	private List<ISliderListener> _sliderListeners;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	/**
	 * The scroll bar figure.
	 */
	private ScrollBar _scrollBar;

	/**
	 * Standard constructor.
	 */
	public SliderFigure() {
		_sliderListeners = new ArrayList<ISliderListener>();

		_scrollBar = new ScrollBar();
		_scrollBar.setExtent(0);
		_scrollBar.setMaximum(1000);
		_scrollBar.setMinimum(1);
		_scrollBar.setValue(400);
		_scrollBar.setStepIncrement(1);
		_scrollBar.setOrientation(ScrollBar.VERTICAL);
		// Ellipse thumb = new Ellipse();
		// thumb.setSize(10,20);
		// thumb.setMinimumSize(new Dimension(60, 60));
		// thumb.setBackgroundColor(ColorConstants.red);

		// thumb.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.RIDGED));

		// _scrollBar.setThumb(thumb);
		// setLayoutManager(new ScrollPaneLayout());
		setLayoutManager(new BorderLayout());
		add(_scrollBar, BorderLayout.CENTER);
		_scrollBar.validate();
		validate();

		// add listener
		_scrollBar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE,
				new PropertyChangeListener() {
					public void propertyChange(final PropertyChangeEvent event) {
						int newValue = (Integer) event.getNewValue();
						for (ISliderListener l : _sliderListeners) {
							l.sliderValueChanged(newValue);
						}
					}
				});
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param min
	 *            The minimum value.
	 */
	public void setMin(final int min) {
		_scrollBar.setMinimum(min);

	}

	/**
	 * Set the maximum value.
	 * 
	 * @param max
	 *            The maximum value.
	 */
	public void setMax(final int max) {
		_scrollBar.setMaximum(max);
	}

	/**
	 * Set the increment value.
	 * 
	 * @param increment
	 *            The increment value.
	 */
	public void setIncrement(final int increment) {
		_scrollBar.setStepIncrement(increment);
	}

	/**
	 * Sets the orientation. Choose one of {@link PositionConstants#HORIZONTAL}
	 * or {@link PositionConstants#VERTICAL}.
	 * 
	 * @param horizontal
	 *            true for horizontal and false for vertical layout
	 */
	public void setOrientation(final boolean horizontal) {
		_scrollBar.setOrientation(horizontal ? ScrollBar.HORIZONTAL
				: ScrollBar.VERTICAL);
	}

	/**
	 * Set the current slider value.
	 * 
	 * @param value
	 *            The current slider value.
	 */
	public void setValue(final int value) {
		_scrollBar.setValue(value);
	}

	/**
	 * Add a slider listener.
	 * 
	 * @param listener
	 *            The slider listener to add.
	 */
	public void addSliderListener(final ISliderListener listener) {
		_sliderListeners.add(listener);
	}

	/**
	 * Remove a slider listener.
	 * 
	 * @param listener
	 *            The slider listener that is to be removed.
	 */
	public void removeSliderListener(final ISliderListener listener) {
		_sliderListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

}
