package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.widgets.figures.ScaledSliderFigure.IScaledSliderListener;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.geometry.Rectangle;

/**The figure for a spinner widget.
 * @author Xihui Chen
 *
 */
public class SpinnerFigure extends Figure {

	private double min = -100;
	private double max = 100;
	private double stepIncrement = 1;
	private double value = 0;
	
	private ArrowButton buttonUp, buttonDown;
	private LabelFigure labelFigure;
	private List<ISpinnerListener> spinnerListeners;
	
	private final static int BUTTON_WIDTH = 20;
	
	public SpinnerFigure() {
		spinnerListeners = new ArrayList<ISpinnerListener>();
		labelFigure = new LabelFigure();
		labelFigure.setText(Double.toString(value));
		add(labelFigure);
		
		buttonUp = new ArrowButton();
		buttonUp.setBorder(new ButtonBorder(ButtonBorder.SCHEMES.BUTTON_SCROLLBAR));
		buttonUp.setDirection(Orientable.NORTH);
		buttonUp.setFiringMethod(Clickable.REPEAT_FIRING);
		buttonUp.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent event) {
				stepUp();				
			}
		});
		add(buttonUp);
		
		buttonDown = new ArrowButton();
		buttonDown.setBorder(new ButtonBorder(ButtonBorder.SCHEMES.BUTTON_SCROLLBAR));
		buttonDown.setDirection(Orientable.NORTH);
		buttonDown.setFiringMethod(Clickable.REPEAT_FIRING);
		buttonDown.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent event) {
				stepDown();				
			}
		});
		add(buttonDown);
		
	}
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea();
		labelFigure.setBounds(new Rectangle(clientArea.x, clientArea.y, 
				clientArea.width - BUTTON_WIDTH, clientArea.height));
		buttonUp.setBounds(new Rectangle(clientArea.x + clientArea.width - BUTTON_WIDTH,
				clientArea.y, BUTTON_WIDTH, clientArea.height/2));
		buttonDown.setBounds(new Rectangle(clientArea.x + clientArea.width - BUTTON_WIDTH,
				clientArea.y + clientArea.height/2, BUTTON_WIDTH, clientArea.height/2));		
		super.layout();
	}
	
	
	/**
	 * Inform all slider listeners, that the manual value has changed.
	 * 
	 * @param newManualValue
	 *            the new manual value
	 */
	private void fireManualValueChange(final double newManualValue) {		
		
			for (ISpinnerListener l : spinnerListeners) {
				l.manualValueChanged(newManualValue);
			}
	}
	
	/**
	 * Cause the spinner to increase its value by its step increment;
	 */
	protected void stepUp() {
		if(setValue(getValue() + getStepIncrement()))
			fireManualValueChange(getValue());
	}

	/**
	 * Cause the spinner to decrease its value by its step increment;
	 */
	protected void stepDown() {
		if(setValue(getValue() - getStepIncrement()))
			fireManualValueChange(getValue());
	}
	
	/**
	 * @return the min
	 */
	public final double getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public final void setMin(double min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public final double getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public final void setMax(double max) {
		this.max = max;
	}

	/**
	 * @return the stepIncrement
	 */
	public final double getStepIncrement() {
		return stepIncrement;
	}

	/**
	 * @param stepIncrement the stepIncrement to set
	 */
	public final void setStepIncrement(double stepIncrement) {
		this.stepIncrement = stepIncrement;
	}

	/**
	 * @return the value
	 */
	public final double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 * @return true if value changed. false otherwise.
	 */
	public final boolean setValue(double value) {
		value = Math.max(getMin(), Math.min(getMax(), value));
		if (this.value == value)
			return false;
		this.value = value;
		labelFigure.setText(Double.toString(value));
		return true;
	}
	
	/**
	 * Definition of listeners that react on spinner manual value change events.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	public interface ISpinnerListener {
		/**
		 * React on a spinner manual value change event.
		 * 
		 * @param newValue
		 *            The new spinner value.
		 */
		void manualValueChanged(double newValue);
	}
	
}
