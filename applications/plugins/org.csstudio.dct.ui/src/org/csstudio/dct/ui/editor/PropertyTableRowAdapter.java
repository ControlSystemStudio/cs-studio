package org.csstudio.dct.ui.editor;

import java.util.Map;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.commands.ChangePropertyKeyCommand;
import org.csstudio.dct.model.commands.ChangePropertyValueCommand;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.util.ResolutionUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

/**
 * Table adapter for Properties.
 *
 * @author Sven Wende
 *
 */
public final class PropertyTableRowAdapter extends AbstractTableRowAdapter<IPropertyContainer> {
    private String propertyKey;

    /**
     * Constructor.
     *
     * @param delegate
     *            a property container
     * @param propertyKey
     *            the property name
     */
    public PropertyTableRowAdapter(IPropertyContainer delegate, String propertyKey) {
        super(delegate);
        this.propertyKey = propertyKey;
    }

    /**
     * Returns the property name.
     * @return the property name
     */
    public String getPropertyKey() {
        return propertyKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doCanModifyKey(IPropertyContainer record) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RGB doGetForegroundColorForValue(IPropertyContainer delegate) {
        Map<String, String> localProperties = delegate.getProperties();
        boolean inherited = !localProperties.containsKey(propertyKey);
        RGB rgb = inherited ? ColorSettings.INHERITED_VALUE : ColorSettings.OVERRIDDEN_VALUE;
        return rgb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetKey(IPropertyContainer delegate) {
        return propertyKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetKeyDescription(IPropertyContainer delegate) {
        return propertyKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValue(IPropertyContainer delegate) {
        return delegate.getFinalProperties().get(propertyKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetValueForDisplay(IPropertyContainer delegate) {
        String result = doGetValue(delegate);

        try {
            String input = delegate.getFinalProperties().get(propertyKey).toString();

            // FIXME: Harten Cast auflösen!
            result = ResolutionUtil.resolve(input, (IElement) delegate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doSetKey(IPropertyContainer delegate, Object key) {
        return new ChangePropertyKeyCommand(delegate, propertyKey, key != null ? key.toString() : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doSetValue(IPropertyContainer delegate, Object value) {
        return new ChangePropertyValueCommand(delegate, propertyKey, value != null ? value.toString() : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Image doGetImage(IPropertyContainer delegate, int columnIndex) {
        return columnIndex==0?CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/field.png"):null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doCompareTo(ITableRow row) {
        int result = 0;
        if (row instanceof PropertyTableRowAdapter) {
            result = propertyKey.compareTo(((PropertyTableRowAdapter) row).propertyKey);
        }

        return result;
    }
}
