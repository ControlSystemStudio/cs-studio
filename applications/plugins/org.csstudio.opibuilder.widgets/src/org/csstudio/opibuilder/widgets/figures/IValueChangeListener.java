package org.csstudio.opibuilder.widgets.figures;
/**
	 * Definition of listeners that react on value change events.
	 * 
	 * @author Xihui Chen
	 * 
	 */
public interface IValueChangeListener {
		/**
		 * executed when value changed.
		 * 
		 * @param newValue
		 *            The new value.
		 */
		void valueChanged(double newValue);
}