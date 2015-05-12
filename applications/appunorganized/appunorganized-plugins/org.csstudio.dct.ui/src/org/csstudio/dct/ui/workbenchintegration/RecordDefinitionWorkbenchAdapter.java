package org.csstudio.dct.ui.workbenchintegration;

import org.csstudio.dct.metamodel.IRecordDefinition;

/**
 * UI adapter for {@link IRecordDefinition}.
 *
 * @author Sven Wende
 */
public final class RecordDefinitionWorkbenchAdapter extends BaseWorkbenchAdapter<IRecordDefinition> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetLabel(IRecordDefinition definition) {
        return definition.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doGetIcon(IRecordDefinition record) {
        return "icons/recorddefinition.png";
    }

}
