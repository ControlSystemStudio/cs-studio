package org.csstudio.opibuilder.properties;

import org.eclipse.draw2d.IFigure;

/**
 * The handler to execute corresponding changes when property value changed. 
 * For example, refresh the widget figure when color property changed. 
 * 
 * @author Xihui Chen
 * 
 */
public interface IWidgetPropertyChangeHandler {
	/**
	 * Handle the change of an widget property by applying graphical operations
	 * to the given figure.
	 * 
	 * @param oldValue
	 *            The old property value.
	 * @param newValue
	 *            The new property value.
	 * @param figure
	 *            The figure to apply graphical operations to.
	 * @return true, if the figure needs to be repainted after the
	 *         property change handling.
	 */
	boolean handleChange(Object oldValue, Object newValue,
			IFigure figure);
}
