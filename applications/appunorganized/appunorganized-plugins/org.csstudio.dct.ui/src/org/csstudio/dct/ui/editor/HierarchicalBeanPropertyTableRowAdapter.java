package org.csstudio.dct.ui.editor;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.RGB;

/**
 * Table adapter that allows for an easy adaption of an arbitrary property of a
 * domain object.
 *
 * @author Sven Wende
 *
 */
public final class HierarchicalBeanPropertyTableRowAdapter extends AbstractTableRowAdapter<IElement> {
    private String key;
    private String property;
    private boolean readOnly;

    /**
     * Constructor.
     *
     * @param key
     *            the key for the key column
     * @param delegate
     *            the element
     * @param beanProperty
     *            the name of a bean property that should be used in the value
     *            column
     * @param readOnly
     *            true, if the table row should be read only
     */
    public HierarchicalBeanPropertyTableRowAdapter(String key, IElement delegate, String beanProperty, boolean readOnly) {
        super(delegate);
        this.key = key;
        this.property = beanProperty;
        this.readOnly = readOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetKey(IElement delegate) {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValue(IElement delegate) {
        Object result = AliasResolutionUtil.getPropertyViaHierarchy(delegate, property);
        return result!=null?result.toString():"";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValueForDisplay(IElement delegate) {
        return doGetValue(delegate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doSetValue(IElement delegate, Object value) {
        Command result = null;

        Object value2set = null;

        if (value != null && StringUtil.hasLength(value.toString())) {
            PropertyUtilsBean util = new PropertyUtilsBean();
            Class clazz;

            Object tmp = null;
            try {
                clazz = util.getPropertyDescriptor(delegate, property).getPropertyType();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            if (clazz == Boolean.class || "boolean".equalsIgnoreCase(clazz.getName())) {
                tmp = Boolean.parseBoolean(value.toString());
            } else if (clazz == Integer.class || "int".equalsIgnoreCase(clazz.getName())) {
                tmp = Integer.parseInt(value.toString());
            } else if (clazz == Double.class || "double".equalsIgnoreCase(clazz.getName())) {
                tmp = Double.parseDouble(value.toString());
            } else {
                tmp = value.toString();
            }

            value2set = tmp;
        }

        return new ChangeBeanPropertyCommand(delegate, property, value2set);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doCanModifyValue(IElement delegate) {
        return !readOnly;
    }

    @Override
    protected RGB doGetForegroundColorForValue(IElement delegate) {
        PropertyUtilsBean util = new PropertyUtilsBean();
        Object currentValue = null;

        try {
            currentValue = util.getProperty(delegate, property);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return (currentValue != null) ? ColorSettings.OVERRIDDEN_VALUE : ColorSettings.INHERITED_VALUE;
    }
}
