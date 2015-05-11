package org.csstudio.ui.util.composites;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.widgets.Composite;

/**
 * Implements the bean bound property notification scheme on top of a composite.
 * Subclasses should specify in the documentation which properties are bound.
 *
 * @author Gabriele Carcassi
 */
public abstract class BeanComposite extends Composite {

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
            this);

    /**
     * Pass through constructor to Composite.
     *
     * @param parent
     *            composite parent
     * @param style
     *            SWT style
     */
    public BeanComposite(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Adds a new listener.
     *
     * @param listener
     *            a new listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes the given listener.
     *
     * @param listener
     *            a listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Forwards events from the child widget as if events coming from this
     * widget.
     * <p>
     * This method is useful when this composite will be constructed with other
     * composites and will need to expose parts of them as its own.
     *
     * @param widgetProperty
     *            a property name of the widget inside the composite
     * @param panelProperty
     *            a property name of this widget
     * @return a new listener
     */
    protected void forwardPropertyChange(final BeanComposite childWidget,
            final String childProperty, final String property) {
        childWidget.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (childProperty.equals(evt.getPropertyName())) {
                    changeSupport.firePropertyChange(property,
                            evt.getOldValue(), evt.getNewValue());
                }
            }
        });
    }

}
