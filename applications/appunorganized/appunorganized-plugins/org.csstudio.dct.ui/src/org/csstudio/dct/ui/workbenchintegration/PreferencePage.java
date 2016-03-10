package org.csstudio.dct.ui.workbenchintegration;

import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_3_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_4_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.FIELD_DESCRIPTION_SHOW_DESCRIPTION;
import static org.csstudio.dct.PreferenceSettings.FIELD_DESCRIPTION_SHOW_INITIAL_VALUE;
import static org.csstudio.dct.PreferenceSettings.IO_NAME_SERVICE_ID;
import static org.csstudio.dct.PreferenceSettings.SENSOR_ID_SERVICE_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.ExtensionPointUtil;
import org.csstudio.dct.ISensorIdService;
import org.csstudio.dct.IoNameService;
import org.csstudio.dct.ServiceExtension;
import org.csstudio.dct.ui.Activator;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference Page for the DCT.
 *
 * @author Sven Wende
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     *{@inheritDoc}
     */
    @Override
    protected void createFieldEditors() {
        // settings for field description label
        addField(new BooleanFieldEditor(FIELD_DESCRIPTION_SHOW_DESCRIPTION.name(), FIELD_DESCRIPTION_SHOW_DESCRIPTION.getLabel(),
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(FIELD_DESCRIPTION_SHOW_INITIAL_VALUE.name(), FIELD_DESCRIPTION_SHOW_INITIAL_VALUE.getLabel(),
                getFieldEditorParent()));

        // settings for parameter proposals of the datalink() function
        addField(new StringListEditor(DATALINK_FUNCTION_PARAMETER_3_PROPOSAL.name(), DATALINK_FUNCTION_PARAMETER_3_PROPOSAL.getLabel(),
                getFieldEditorParent()));
        addField(new StringListEditor(DATALINK_FUNCTION_PARAMETER_4_PROPOSAL.name(), DATALINK_FUNCTION_PARAMETER_4_PROPOSAL.getLabel(),
                getFieldEditorParent()));

        // settings for choosing the sensor id service instance in case of multiple service
        // extensions available
        Map<String, ServiceExtension<ISensorIdService>> sensorIdServiceExtensions = ExtensionPointUtil
                .lookupNamingServiceExtensions(DctActivator.EXTPOINT_SENSOR_ID_SERVICES);

        if (sensorIdServiceExtensions.keySet().size() > 1) {
            addField(new ComboFieldEditor(SENSOR_ID_SERVICE_ID.name(), SENSOR_ID_SERVICE_ID.getLabel(),
                    resolveEntryAndNames(new ArrayList<ServiceExtension>(sensorIdServiceExtensions.values())), getFieldEditorParent()));
        }

        // settings for choosing the sensor id service instance in case of multiple service
        // extensions available
        Map<String, ServiceExtension<IoNameService>> ioNameServiceExtensions = ExtensionPointUtil
                .lookupNamingServiceExtensions(DctActivator.EXTPOINT_IO_NAME_SERVICE);

        if (ioNameServiceExtensions.keySet().size() > 1) {
            addField(new ComboFieldEditor(IO_NAME_SERVICE_ID.name(), IO_NAME_SERVICE_ID.getLabel(),
                    resolveEntryAndNames(new ArrayList<ServiceExtension>(ioNameServiceExtensions.values())), getFieldEditorParent()));
        }


    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return Activator.getCorePreferenceStore();
    }

    private static String[][] resolveEntryAndNames(List<ServiceExtension> extensions) {
        String[][] result = new String[extensions.size()][2];

        int i = 0;

        for (ServiceExtension extension : extensions) {
            result[i][0] = extension.getName();
            result[i][1] = extension.getId();
            i++;
        }
        return result;
    }

    private static final class StringListEditor extends ListEditor {
        private StringListEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        protected String createList(String[] items) {
            return StringUtil.toSeparatedString(Arrays.asList(items), ",");
        }

        @Override
        protected String getNewInputObject() {
            InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "New value",
                    "Please enter a new value", "", new IInputValidator() {
                        public String isValid(String newText) {
                            if (!StringUtil.hasLength(newText)) {
                                return "Value cannot be empty";
                            }
                            return null;
                        }
                    });

            String result = null;

            if (dialog.open() == Window.OK) {
                result = dialog.getValue();
            }

            return result;
        }

        @Override
        protected String[] parseString(String stringList) {
            return stringList.split(",");
        }
    }

}
