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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * A slider figure.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class SimpleSliderFigure extends Panel implements
		IRefreshableFigure {
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
	 * A label, which display the current value.
	 */
	private Label _valueLabel;

	/**
	 * Lower border of the displayed value.
	 */
	private int _min = 0;

	/**
	 * Upper border of the displayed value.
	 */
	private int _max = 100;

	/**
	 * The current value.
	 */
	private int _currentValue = 30;

	/**
	 * The current manual value.
	 */
	private int _manualValue = 30;
	
	/**
	 * The precision of the scrollbar.
	 */
	private int _scrollbarPrecision = 100;
	
	/**
	 * The potenz for the precision.
	 */
	private int _decimalPlaces = 2;
	
	/**
	 * The minimum wide for the slider.	 
	 */
	private int _sliderWide = 5;

	/**
	 * Flag which is used to disable slider events. When the current value is
	 * set on the scrollbar, eventing must be turned off.
	 */
	private boolean _populateEvents = true;

	/**
	 * Standard constructor.
	 */
	public SimpleSliderFigure() {
		_populateEvents = false;

		_sliderListeners = new ArrayList<ISliderListener>();

		BorderLayout layout = new BorderLayout();
		layout.setVerticalSpacing(2);
		layout.setHorizontalSpacing(2);

		setLayoutManager(layout);

		_valueLabel = new Label();
		add(_valueLabel, BorderLayout.TOP);

		_scrollBar = createScrollbarFigure();
		add(_scrollBar, BorderLayout.CENTER);

		_populateEvents = true;
	}

	/**
	 * Creates the scrollbar.
	 * 
	 * @return the scrollbar figure
	 */
	private ScrollBar createScrollbarFigure() {
		ScrollBar bar = new ScrollBar();
		bar.setExtent(5);
		bar.setMaximum(1000);
		bar.setMinimum(1);
		bar.setValue(400);
		bar.setStepIncrement(1);
		bar.setOrientation(Orientable.VERTICAL);
		bar.setBackgroundColor(ColorConstants.blue);
		Ellipse thumb = new Ellipse();
		thumb.setSize(new Dimension(40, 40));
		thumb.setFont(CustomMediaFactory.getInstance().getDefaultFont(true));
		thumb.setBackgroundColor(ColorConstants.red);

		thumb.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.RIDGED));
		bar.validate();

		// add listener
		bar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE,
				new PropertyChangeListener() {
					public void propertyChange(final PropertyChangeEvent event) {
						fireManualValueChange((Integer) event.getNewValue());
					}
				});

		return bar;
	}

	/**
	 * Inform all slider listeners, that the manual value has changed.
	 * 
	 * @param newManualValue
	 *            the new manual value
	 */
	private void fireManualValueChange(final int newManualValue) {
		int tmp = newManualValue;

		if (tmp < _min) {
			tmp = _min;
		}
		if (tmp > _max) {
			tmp = _max;
		}

		if (_populateEvents) {
			for (ISliderListener l : _sliderListeners) {
				l.sliderValueChanged(this.getDoubleFor(tmp));
			}
		}
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param min
	 *            The minimum value.
	 */
	public void setMin(final double min) {
		_min = (int)(min*_scrollbarPrecision);
		_scrollBar.setMinimum(_min);
	}

	/**
	 * Set the maximum value.
	 * 
	 * @param max
	 *            The maximum value.
	 */
	public void setMax(final double max) {		
		_max = (int)(max*_scrollbarPrecision);
		//_scrollBar.setMaximum(_max);
		this.setScrollbarMax(_max);
		
	}
	
	/**
	 * Sets the real max value for the scrollbar based on the given max value.
	 * @param max
	 * 				The base for the real max value
	 */
	private void setScrollbarMax(final int max) {
		_scrollBar.setMaximum(max+_sliderWide);
	}
	
	/**
	 * Sets the count of decimal places for this slider.
	 * @param decimalPlaces
	 * 			  The precision
	 */
	public void setDecimalPlaces(final int decimalPlaces) {
		_decimalPlaces = decimalPlaces;
		this.updateValueText();
	}
	
	/**
	 * Gets the precision of this slider.
	 * @return
	 * 			  The precision
	 */
	public int getDecimalPlaces() {
		return _decimalPlaces;
	}
	
	/**
	 * Sets the value for the precision of this slider.
	 * @param precision
	 * 			  The precision
	 */
	private void setScrollbarPrecision(final int precision) {
		_populateEvents = false;
		double min = this.getDoubleFor(_min);
		double max = this.getDoubleFor(_max);
		int minWide = _sliderWide/_scrollbarPrecision; 
		double value = this.getDoubleFor(_currentValue);
		double manualValue = this.getDoubleFor(_manualValue);
		_scrollbarPrecision = precision;
		this.setMin(min);
		this.setMax(max);
		this.setSliderWide(minWide);
		this.setValue(value);
		this.setManualValue(manualValue);
		_populateEvents = true;
	}
	
	/**
	 * Sets the wide for the slider.
	 * @param wide
	 * 			  The wide
	 */
	public void setSliderWide(final int wide) {
		_sliderWide = wide*_scrollbarPrecision;
		this.setScrollbarMax(_max);
		_scrollBar.setExtent(_sliderWide);
	}
	
	/**
	 * Gets the slider wide.
	 * @return int
	 * 			  The slider wide
	 */
	public int getSliderWide() {
		return (_sliderWide/_scrollbarPrecision);
	}

	/**
	 * Set the increment value.
	 * 
	 * @param increment
	 *            The increment value.
	 */
	public void setIncrement(final double increment) {
		this.setScrollbarPrecision((int) Math.pow(10, this.getCountOfDecimals(increment)));
		int inc = (int)(increment*_scrollbarPrecision);
		if (inc==0) {
			inc = 1;
		}
		
		_scrollBar.setStepIncrement(inc);
		_scrollBar.setPageIncrement(inc);
	}
	
	/**
	 * Gets the count of numbers after the comma.
	 * @param value 
	 * 				The double value
	 * @return int 
	 * 				The count of numbers after the comma
	 */
	private int getCountOfDecimals(final double value) {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(30);
		String text = format.format(value);
		if (text.indexOf(",")>=0) {
			String aftercomma = text.substring(text.indexOf(","));
			if (aftercomma.equals(",0")) {
				return 0;
			}
			return Math.min(5, aftercomma.length()-1);
		}
		return 0;
	}

	/**
	 * Sets the orientation. 
	 * 
	 * @param horizontal
	 *            true for horizontal and false for vertical layout
	 */
	public void setOrientation(final boolean horizontal) {
		_scrollBar.setOrientation(horizontal ? Orientable.HORIZONTAL
				: Orientable.VERTICAL);
	}

	/**
	 * Set the current slider value.
	 * 
	 * <b>Important:</b> This method should only get called by the Controller
	 * and not by the figure itself!
	 * 
	 * @param value
	 *            the current slider value
	 */
	public void setValue(final double value) {
		// disable eventing
		_populateEvents = false;

		// store current value
		_currentValue = (int)(value*_scrollbarPrecision);

		// update scrollbar
		if (_currentValue < _min || _currentValue > _max) {
			// current value is out of the sliders range -> disable the slider
			_scrollBar.setEnabled(false);
		} else {
			_scrollBar.setEnabled(true);
			_scrollBar.setValue(_currentValue);
			_scrollBar.invalidate();
		}

		updateValueText();

		// enable eventing
		_populateEvents = true;
	}

	/**
	 * Set the current manual value.
	 * 
	 * <b>Important:</b> This method should only get called by the Controller
	 * and not by the figure itself!
	 * 
	 * @param value
	 *            the current slider value
	 */
	public void setManualValue(final double value) {
		int tempValue = (int) (value*_scrollbarPrecision);
		assert tempValue >= _min && tempValue <= _max;

		_manualValue = tempValue;

		updateValueText();
	}

	/**
	 * Updates the value labels text.
	 */
	private void updateValueText() {
		// update the value label text
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(_decimalPlaces);
		_valueLabel
				.setText("" + format.format(this.getDoubleFor(_currentValue)) + " [MAN: " + format.format(this.getDoubleFor(_manualValue)) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Gets a double, which has the given int value divided by the scrollbarPrecision.
	 * @param value
	 * 				The int value
	 * @return
	 * 				The corresponding double
	 */
	private double getDoubleFor(final int value) {
		return ((double)value)/_scrollbarPrecision;
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
		void sliderValueChanged(double newValue);
	}

}
