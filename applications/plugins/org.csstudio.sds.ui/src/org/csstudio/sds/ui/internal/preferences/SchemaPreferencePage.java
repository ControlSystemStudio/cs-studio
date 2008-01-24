package org.csstudio.sds.ui.internal.preferences;

import java.util.HashMap;
import java.util.Set;

import org.csstudio.sds.model.initializers.ModelInitializationService;
import org.csstudio.sds.model.initializers.ModelInitializationService.ControlSystemSchemaDescriptor;
import org.csstudio.sds.preferences.PreferenceConstants;
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
 * @version $Revision$
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
		
		RadioGroupFieldEditor radioFields = new RadioGroupFieldEditor(PreferenceConstants.PROP_SCHEMA,
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
	public void init(final IWorkbench workbench) {
		_schemaDescriptors = ModelInitializationService.getInstance().getInitializationSchemaDescriptors();
	}

}
