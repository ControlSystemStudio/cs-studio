package org.csstudio.ui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * The root for all the composites that are meant to be used as panels to configure
 * the settings (i.e. bean properties) of another widget.
 * <p>
 * Utility methods are supplied to make it easy to forward notifications
 * from widgets inside this composite as notifications of properties
 * of this composite.
 * 
 * @author Gabriele Carcassi
 */
public abstract class AbstractConfigurationPanel extends BeanComposite {

	/**
	 * Pass through constructor.
	 */
	public AbstractConfigurationPanel(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Creates a listener that forwards the change notification from a widget
	 * of the panel (widgetProperty) to a property of this composite (compositeProperty).
	 * 
	 * @param widgetProperty a property name of the widget inside the composite
	 * @param panelProperty a property name of this widget
	 * @return a new listener
	 */
	protected PropertyChangeListener forwardPropertyChangeListener(final String widgetProperty,
			final String panelProperty) {
		return new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (widgetProperty.equals(evt.getPropertyName())) {
					changeSupport.firePropertyChange(panelProperty, evt.getOldValue(), evt.getNewValue());
				}
			}
		};
	}

	/**
	 * Creates a listener that forwards the selection notification from a widget
	 * of the panel to a property of this composite (compositeProperty).
	 * 
	 * @param widgetProperty a property name of the widget inside the composite
	 * @param panelProperty a property name of this widget
	 * @return a new listener
	 */
	protected SelectionListener forwardSelectionListener(final String panelProperty) {
		return new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Find a solution for the values
				changeSupport.firePropertyChange(panelProperty, null, null);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	protected void forwardTextEvents(final Text widget, final String propertyName) {
		widget.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange(propertyName, null,
						widget.getText());
			}
		});
	}
	
}
