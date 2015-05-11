package org.csstudio.dct.ui.graphicalviewer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for elements of the graphical model.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractBase {
    private transient PropertyChangeSupport _pcsDelegate;

    /**
     * Standard constructor.
     */
    public AbstractBase() {
        _pcsDelegate = new PropertyChangeSupport(this);

    }

    /**
     * Adds a {@link PropertyChangeListener}.
     *
     * @param listener
     *            the listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            _pcsDelegate.addPropertyChangeListener(listener);
        }
    }

    /**
     * Removes a {@link PropertyChangeListener}.
     *
     * @param listener
     *            the listener
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            _pcsDelegate.removePropertyChangeListener(listener);
        }
    }

    /**
     * Returns the node´s caption.
     *
     * @return the caption
     */
    public abstract String getCaption();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getCaption();
    }

    /**
     * Report a property change to registered listeners.
     *
     * @param property
     *            the name of the property that changed
     * @param oldValue
     *            the old value of this property
     * @param newValue
     *            the new value of this property
     */
    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        if (_pcsDelegate.hasListeners(property)) {
            _pcsDelegate.firePropertyChange(property, oldValue, newValue);
        }
    }

}
