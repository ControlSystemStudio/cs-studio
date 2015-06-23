/**
 *
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * An Abstract class which provides the basic functionality expected from a
 * Composite used to represent a logbook property
 *
 * @author shroffk
 *
 */
public abstract class AbstractPropertyWidget extends Composite {

    private boolean editable;
    private LogEntryChangeset logEntryChangeset;

    /**
     * A constructor which creates the composite, registers the appropriate
     * listeners and initializes it with the logEntryChangeset
     *
     * @param parent
     * @param style
     * @param logEntryChangeset
     */
    public AbstractPropertyWidget(Composite parent, int style,
        LogEntryChangeset logEntryChangeset, boolean editable) {
    super(parent, style);
    if (logEntryChangeset != null) {
        this.logEntryChangeset = logEntryChangeset;
        this.logEntryChangeset
            .addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Display.getDefault().asyncExec(() -> {updateUI();});

            }
            });
    }
    this.editable = editable;
    }

    public boolean isEditable() {
    return editable;
    }

    public void setEditable(boolean editable) {
    this.editable = editable;
    updateUI();
    }

    public LogEntryChangeset getLogEntryChangeset() {
    return this.logEntryChangeset;
    }

    public abstract void updateUI();
}
