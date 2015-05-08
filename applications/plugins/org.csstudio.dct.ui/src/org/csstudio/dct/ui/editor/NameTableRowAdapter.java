package org.csstudio.dct.ui.editor;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.util.AliasResolutionException;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.ResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.RGB;

/**
 * Row adapter for the name of the record.
 *
 * @author Sven Wende
 *
 */
class NameTableRowAdapter extends AbstractTableRowAdapter<IElement> {

    public NameTableRowAdapter(IElement element) {
        super(element);
    }

    @Override
    protected String doGetKey(IElement element) {
        return "Name";
    }

    @Override
    protected String doGetValue(IElement element) {
        return AliasResolutionUtil.getNameFromHierarchy(element);
    }

    @Override
    protected String doGetValueForDisplay(IElement element) {
        String result =AliasResolutionUtil.getNameFromHierarchy(element);

        if (element.isInherited()) {
            try {
                result = ResolutionUtil.resolve(result, element);
            } catch (AliasResolutionException e) {
                setError(e.getMessage());
            }
        }

        return result;
    }

    @Override
    protected Command doSetValue(IElement element, Object value) {
        Command result = null;
        if (value == null || !value.equals(AliasResolutionUtil.getNameFromHierarchy(element))) {
            String value2set = null;

            if(value!=null && value.toString().length()>0) {
                value2set = value.toString();
            }

            result = new ChangeBeanPropertyCommand(element, "name", value2set);
        }

        return result;
    }

    @Override
    protected RGB doGetForegroundColorForValue(IElement element) {
        String name = element.getName();
        return (name != null && name.length() > 0) ? ColorSettings.OVERRIDDEN_VALUE : ColorSettings.INHERITED_VALUE;
    }

}
