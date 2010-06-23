package org.csstudio.opibuilder.widgets.figures;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.ButtonBorder.ButtonScheme;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**The figure for a spinner widget.
 * @author Xihui Chen
 *
 */
public class SpinnerFigure extends Figure {
	public enum NumericFormatType {
		DECIAML("Decimal"),
		EXP("Exponential"),
		HEX("Hex");
		
		private String description;
		private NumericFormatType(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i =0 ;
			for(NumericFormatType f : values()){
				result[i++] = f.toString();
			}
			return result;
		}
	}
	private static final String HEX_PREFIX = "0x"; //$NON-NLS-1$

	private double min = -100;
	private double max = 100;
	private double stepIncrement = 1;
	private double pageIncrement = 10;
	private double value = 0;
	
	private ArrowButton buttonUp, buttonDown;
	private LabelFigure labelFigure;
	private List<ISpinnerListener> spinnerListeners;
	
	private final static int BUTTON_WIDTH = 25;
	
	private NumericFormatType formatType;
	
	public SpinnerFigure(ExecutionMode mode) {
		formatType = NumericFormatType.DECIAML;
		spinnerListeners = new ArrayList<ISpinnerListener>();
		setRequestFocusEnabled(true);
		setFocusTraversable(true);
			addKeyListener(new KeyListener() {
				
				public void keyReleased(KeyEvent ke) {				
				}
				
				public void keyPressed(KeyEvent ke) {
					if(ke.keycode == SWT.ARROW_DOWN)
						stepDown();
					else if(ke.keycode == SWT.ARROW_UP)
						stepUp();
					else if(ke.keycode == SWT.PAGE_UP)
						pageUp();
					else if(ke.keycode == SWT.PAGE_DOWN)
						pageDown();
				}
			});
			
			addFocusListener(new FocusListener() {
				
				public void focusLost(FocusEvent fe) {
					repaint();
				}
				
				public void focusGained(FocusEvent fe) {
					repaint();
				}
			});
		
		
		labelFigure = new LabelFigure(){
			/**
			 * If this button has focus, this method paints a focus rectangle.
			 * 
			 * @param graphics Graphics handle for painting
			 */
			protected void paintBorder(Graphics graphics) {
				super.paintBorder(graphics);
				if (SpinnerFigure.this.hasFocus()) {
					graphics.setForegroundColor(ColorConstants.black);
					graphics.setBackgroundColor(ColorConstants.white);

					Rectangle area = getClientArea();					
					graphics.drawFocus(area.x, area.y, area.width-1, area.height-1);
					
				}
			}
		};
		labelFigure.setText(format(value));
		add(labelFigure);
		
		ButtonBorder buttonBorder = new ButtonBorder(new ButtonScheme(new Color[]{ColorConstants.buttonLightest},
				new Color[]{ColorConstants.buttonDarkest}));
		
		buttonUp = new ArrowButton();
		buttonUp.setBorder(buttonBorder);
		buttonUp.setDirection(Orientable.NORTH);
		buttonUp.setFiringMethod(Clickable.REPEAT_FIRING);
		buttonUp.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent event) {
				stepUp();	
				if(!hasFocus())
					requestFocus();
			}
		});
		add(buttonUp);
		
		buttonDown = new ArrowButton();
		buttonDown.setBorder(buttonBorder);
		buttonDown.setDirection(Orientable.SOUTH);
		buttonDown.setFiringMethod(Clickable.REPEAT_FIRING);
		buttonDown.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent event) {
				stepDown();		
				if(!hasFocus())
					requestFocus();
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
	
	@Override
	public void setEnabled(boolean value) {
		buttonUp.setEnabled(value);
		buttonDown.setEnabled(value);
		super.setEnabled(value);
	}
	
	
	public void addManualValueChangeListener(ISpinnerListener listener){
		if(listener != null)
			spinnerListeners.add(listener);
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
		if(manualSetValue(getValue() + getStepIncrement()))
			fireManualValueChange(getValue());
	}

	/**
	 * Cause the spinner to decrease its value by its step increment;
	 */
	protected void stepDown() {
		if(manualSetValue(getValue() - getStepIncrement()))
			fireManualValueChange(getValue());
	}
	
	
	/**
	 * Cause the spinner to increase its value by its step increment;
	 */
	protected void pageUp() {
		if(manualSetValue(getValue() + getPageIncrement()))
			fireManualValueChange(getValue());
	}

	/**
	 * Cause the spinner to decrease its value by its step increment;
	 */
	protected void pageDown() {
		if(manualSetValue(getValue() - getPageIncrement()))
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

	public void setPageIncrement(double pageIncrement) {
		this.pageIncrement = pageIncrement;
	}
	
	public double getPageIncrement() {
		return pageIncrement;
	}
	
	/**
	 * @return the value
	 */
	public final double getValue() {
		return value;
	}

	/**Set the value of the spinner. It will be coerced in the range.
	 * This only update the text. 
	 * It will not notify listeners about the value change.
	 * @param value the value to set
	 * @return true if value changed. false otherwise.
	 */
	public void setValue(double value) {
		if (this.value == value)
			return;
		this.value = value;
		labelFigure.setText(format(value));		
	}
	
	/**Set Value from manual control of the widget. Value will be coerced in range.
	 * @param value
	 */
	public boolean manualSetValue(double value){
		double oldValue = getValue();
		setValue(
				value < min ? min : (value > max ? max : value));
		return oldValue != getValue();
	}
	
	/**Set the displayed value in the spinner. It may out of the range.
	 * @param value the value to be displayed
	 */
	public final void setDisplayValue(double value){
		if(this.value == value)
			return;
		this.value = value;
		labelFigure.setText(format(value));
	}
	
	
	public LabelFigure getLabelFigure() {
		return labelFigure;
	}
	
	public void setFormatType(NumericFormatType formatType) {
		this.formatType = formatType;
		labelFigure.setText(format(value));		
	}
	
	private String format(double value){
		DecimalFormat format;
		switch (formatType) {
		
		case EXP:
			format = new DecimalFormat("0.#####################E0");			//$NON-NLS-1$
			return format.format(value);
		case HEX:
			return HEX_PREFIX + Long.toHexString((long)value);
		case DECIAML:			
		default:
			format = new DecimalFormat();
			format.setMaximumFractionDigits(100);
			format.setMinimumFractionDigits(0);
			return format.format(value);
		}
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
