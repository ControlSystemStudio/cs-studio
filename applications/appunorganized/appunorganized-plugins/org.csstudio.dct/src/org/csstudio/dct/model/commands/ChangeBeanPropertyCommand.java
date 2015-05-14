/**
 *
 */
package org.csstudio.dct.model.commands;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.eclipse.gef.commands.Command;

/**
 * Command that changes a named bean property.
 *
 * @author Sven Wende
 *
 */
public final class ChangeBeanPropertyCommand extends Command {
    private Object delegate;
    private String propertyName;
    private Object value;
    private Object oldValue;

    /**
     * Constructor.
     * @param delegate the object
     * @param propertyName the name of the property
     * @param value the new value
     */
    public ChangeBeanPropertyCommand(Object delegate, String propertyName, Object value) {
        super();
        this.delegate = delegate;
        this.propertyName = propertyName;
        this.value = value;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        PropertyUtilsBean util = new PropertyUtilsBean();
        try {
            oldValue = util.getProperty(delegate, propertyName);
            util.setProperty(delegate, propertyName, value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        PropertyUtilsBean util = new PropertyUtilsBean();
        try {
            util.setProperty(delegate, propertyName, oldValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
