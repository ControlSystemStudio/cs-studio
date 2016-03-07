/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.preferences;

import java.util.HashMap;
import java.util.Set;

import org.csstudio.sds.model.initializers.WidgetInitializationService;
import org.csstudio.sds.model.initializers.WidgetInitializationService.ControlSystemSchemaDescriptor;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.localization.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A preference page to choose the widget model initialization schema.
 *
 * @author Stefan Hofer
 * @version $Revision: 1.8 $
 *
 */
public final class SchemaPreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * The proxies that hold the schema information.
     */
    private HashMap<String,ControlSystemSchemaDescriptor> _schemaDescriptors;

    /**
     * Constructor.
     */
    public SchemaPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setMessage(Messages.getString("SchemaPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFieldEditors() {
        final Set<String> schemaIds = _schemaDescriptors.keySet();

        String[][] labelAndValues = new String[schemaIds.size()][schemaIds.size()];
        int i = 0;

        for (String schemaId : schemaIds) {
            String label = _schemaDescriptors.get(schemaId).getDescription();
            labelAndValues[i++] = new String[] {label, schemaId};
        }

        RadioGroupFieldEditor radioFields = new RadioGroupFieldEditor(WidgetInitializationService.PROP_SCHEMA,
                Messages.getString("SchemaPreferencePage.RADIO_DESCRIPTION"), //$NON-NLS-1$
                1,
                labelAndValues,
                getFieldEditorParent());

        addField(radioFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return SdsUiPlugin.getCorePreferenceStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench) {
        _schemaDescriptors = WidgetInitializationService.getInstance().getInitializationSchemaDescriptors();
    }

}
