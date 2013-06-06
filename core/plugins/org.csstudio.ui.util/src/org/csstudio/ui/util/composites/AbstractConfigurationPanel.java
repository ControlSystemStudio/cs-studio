package org.csstudio.ui.util.composites;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import org.csstudio.ui.util.DelayedNotificator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
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
			
			private DelayedNotificator notificator = new DelayedNotificator(750, TimeUnit.MILLISECONDS);
			
			@Override
			public void modifyText(ModifyEvent e) {
				notificator.delayedExec(widget, new Runnable() {
					
					@Override
					public void run() {
						changeSupport.firePropertyChange(propertyName, null,
								widget.getText());
					}
				});
			}
		});
	}
	
	protected boolean getCheckBoxValue(final Button checkBox) {
		return checkBox.getSelection();
	}
	
	protected void setCheckBoxValue(final Button checkBox, final boolean newSelection) {
		checkBox.setSelection(newSelection);
	}
	
	protected void forwardCheckBodEvents(final Button checkBox, final String propertyName) {
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("propertyName", !getCheckBoxValue(checkBox), getCheckBoxValue(checkBox));
			}
		});
	}
	
}
